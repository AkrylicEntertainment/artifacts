package dev.nateweisz.bytestore.project.controller

import dev.nateweisz.bytestore.annotations.RateLimited
import dev.nateweisz.bytestore.project.Project
import dev.nateweisz.bytestore.project.ProjectRepository
import dev.nateweisz.bytestore.project.build.BuildRepository
import dev.nateweisz.bytestore.project.data.ProjectCommitInfo
import org.kohsuke.github.GitHub
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/api/projects")
class ProjectController(val projectRepository: ProjectRepository, val buildRepository: BuildRepository, val gitHub: GitHub) {

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
    fun getProjectCommits(@PathVariable username: String, @PathVariable repository: String): List<ProjectCommitInfo> {
        // fetch latest 15 commits from github
        // fetch all builds for project (max 15)
        // return list of commit display info along with status if they are built / have an ongoing build
        // TODO: we should cache these for like 5 minutes
        val repo = gitHub.getRepository("$username/$repository") ?: throw IllegalArgumentException("Repository not found")
        val commits = repo.listCommits()
            .withPageSize(10)
            .iterator()
        val commitList = commits.nextPage();

        // check commits for builds
        val builds = buildRepository.findByProjectIdAndCommitHashIn(repo.id, commitList.map { it.shA1 })

        // TODO: why the heck is this so slow
        return commitList.map { commit ->
            val build = builds.find { it.commitHash == commit.shA1 }
            ProjectCommitInfo(
                commitHash = commit.shA1,
                commitMessage = commit.commitShortInfo.message,
                author = commit.committer.name,
                date = commit.commitDate.toLocaleString(),
                buildInfo = build
            )
        }
    }
}