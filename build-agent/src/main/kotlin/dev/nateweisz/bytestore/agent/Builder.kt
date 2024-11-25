package dev.nateweisz.bytestore.agent

import kotlinx.serialization.descriptors.PrimitiveKind
import org.eclipse.jgit.api.Git
import java.io.File

private val baseBuildDir = File(".builds").also {
    if (!it.exists()) {
        it.mkdirs()
    }
}

fun startBuild(owner: String, repository: String) {
    val buildDir = baseBuildDir.resolve("$owner/$repository")
    buildDir.delete()
    buildDir.mkdirs()

    Git.cloneRepository()
        .setURI("https://github.com/$owner/$repository.git")
        .setDirectory(buildDir)
        .call()

    // When it's finished clear it
    Thread.sleep(10000)
    buildDir.deleteRecursively()
}