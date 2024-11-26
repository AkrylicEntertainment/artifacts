package dev.nateweisz.bytestore.project.github

import org.kohsuke.github.GitHub
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class GitHubConfig {

    @Value("\${github.backend.token}")
    private lateinit var githubToken: String

    @Bean
    @Scope("singleton")
    fun gitHub(): GitHub {
        return GitHub.connectUsingOAuth(githubToken)
    }
}