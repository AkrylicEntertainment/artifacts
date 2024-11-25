package dev.nateweisz.bytestore.project.controller

import dev.nateweisz.bytestore.annotations.RateLimited
import dev.nateweisz.bytestore.lib.Github
import dev.nateweisz.bytestore.lib.takeFirst
import dev.nateweisz.bytestore.project.Project
import dev.nateweisz.bytestore.project.ProjectRepository
import dev.nateweisz.bytestore.project.build.Build
import dev.nateweisz.bytestore.project.build.BuildRepository
import dev.nateweisz.bytestore.project.build.BuildService
import dev.nateweisz.bytestore.project.build.BuildStatus
import dev.nateweisz.bytestore.project.data.ProjectCommitInfo
import jakarta.servlet.http.HttpSession
import org.json.JSONArray
import org.json.JSONObject
import org.kohsuke.github.GitHub
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    val projectRepository: ProjectRepository,
    val buildRepository: BuildRepository,
    val gitHub: GitHub,
    val buildService: BuildService
) {

    @Value("\${github.backend.token}")
    private lateinit var githubToken: String

    @GetMapping("/top")
    @RateLimited(10)
    fun getTopProjects(): List<Project> {
        return projectRepository.findTop10ByOrderByDownloadsDesc()
    }

    @GetMapping("/{username}/{repository}")
    @RateLimited(10)
    fun getProject(@PathVariable username: String, @PathVariable repository: String): Project {
        val existingProject = projectRepository.findByUsernameAndRepoName(username, repository)
        if (existingProject != null) {
            return existingProject
        }

        // Create a project
        // TODO: switch this logic into the project service
        // TODO: some sort of caching system
        val repo = gitHub.getRepository("$username/$repository") ?: throw IllegalArgumentException("Repository not found")
        val project = Project(
            id = repo.id.toString(),
            userId = repo.owner.id,
            username = repo.ownerName,
            repoName = repo.name,
            buildsRun = 0,
            downloads = 0
        )
        projectRepository.save(project)

        return project
    }

    @GetMapping("/{username}/{repository}/commits")
    @RateLimited(10)
    fun getProjectCommits(@PathVariable username: String, @PathVariable repository: String): ResponseEntity<List<ProjectCommitInfo>> {
        // fetch latest 15 commits from github
        // fetch all builds for project (max 15)
        // return list of commit display info along with status if they are built / have an ongoing build
        // TODO: we should cache these for like 5 minutes
        // check commits for builds
        // TODOL switch to a gRPC that just manages all the github requests and stuff (stand alone)
        // this is prob bad to get just by name and not id since we dont currently have a refreshing thing that updates the names
        val commits = parseCommits(Github.getRepositoryCommits(username, repository, githubToken)) ?: return ResponseEntity.status(404).build()
        val project = projectRepository.findByUsernameAndRepoName(username, repository) ?: return ResponseEntity.status(404).build()
        val builds = buildRepository.findByProjectIdAndCommitHashIn(project.id.toLong(), commits.map { commit -> commit.getString("sha") })

        // TODO: why the heck is this so slow
        return ResponseEntity.ok(commits.map { commit ->
            val build = builds.find { it.commitHash == commit.getString("sha") }
            ProjectCommitInfo(
                commitHash = commit.getString("sha"),
                commitMessage = commit.getJSONObject("commit").getString("message").takeFirst(50),
                author = commit.getJSONObject("commit").getJSONObject("author").getString("name"),
                date = commit.getJSONObject("commit").getJSONObject("author").getString("date"),
                buildInfo = build
            )
        })
    }

    @GetMapping("/user/{userId}")
    @RateLimited(10)
    fun getUserProjects(@PathVariable userId: Long, session: HttpSession): ResponseEntity<List<Project>> {
        if (session.getAttribute("user_id") as? Long != userId) {
            return ResponseEntity.status(501).build()
        }

        return  ResponseEntity.ok(projectRepository.findAllByUserId(userId))
    }

    /**
     * Trigger a build for a project.
     *
     * @return The build id that can be used to grab build logs and other information during the build
     */
    @PostMapping("/{username}/{repository}/build/{commitHash}")
    @RateLimited(3)
    fun build(@PathVariable username: String, @PathVariable repository: String, @PathVariable commitHash: String): ResponseEntity<Build> {
        val project = projectRepository.findByUsernameAndRepoName(username, repository) ?: return ResponseEntity.status(404).build()
        val existingBuild = buildRepository.findByProjectIdAndCommitHash(project.id.toLong(), commitHash)

        if (existingBuild != null) {
            return ResponseEntity.status(404).build()
        }

        // Validate commit hash
        if (!Github.isValidCommitHash(username, repository, commitHash, githubToken)) {
            return ResponseEntity.status(404).build()
        }

        // This honestly should be handled by the build agent
        // determine a suitable build agent
        val build = Build(
            projectId = project.id.toLong(),
            owner = project.username,
            repository = project.repoName,
            commitHash = commitHash,
            status = BuildStatus.IN_PROGRESS
        )
        val node = buildService.findOpenNode()
        if (node == null) {
            build.status = BuildStatus.QUEUED
            buildService.queueBuild(build)
            buildRepository.save(build)
            return ResponseEntity.ok(build)
        }

        buildService.startBuildOn(node, build)
        return ResponseEntity.ok(build)
    }

    private fun parseCommits(commits: JSONArray?): List<JSONObject>? {
        if (commits == null) {
            return null
        }

        val commitList = mutableListOf<JSONObject>()
        for (i in 0 until commits.length()) {
            commitList.add(commits.getJSONObject(i))
        }
        return commitList
    }
}