package dev.nateweisz.bytestore.project.data

import dev.nateweisz.bytestore.project.build.Build

data class ProjectCommitInfo(
    val commitHash: String,
    val commitMessage: String, // Stripped to first 50 characters
    val author: String,
    val date: String,
    val buildInfo: Build? = null
) {}