package dev.nateweisz.bytestore.project.github

import org.kohsuke.github.GitHub
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class GitHubConfig {

    @Bean
    @Scope("singleton")
    fun gitHub(): GitHub {
        return GitHub.connectAnonymously()
    }
}