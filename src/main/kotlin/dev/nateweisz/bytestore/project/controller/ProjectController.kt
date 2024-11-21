package dev.nateweisz.bytestore.project.controller

import dev.nateweisz.bytestore.annotations.RateLimited
import dev.nateweisz.bytestore.project.Project
import dev.nateweisz.bytestore.project.ProjectRepository
import org.kohsuke.github.GitHub
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects")
class ProjectController(val projectRepository: ProjectRepository, val gitHub: GitHub) {

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
}