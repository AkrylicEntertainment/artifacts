package dev.nateweisz.bytestore.agent

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.WaitResponse
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.net.URI

private val baseBuildDir = File(".builds").also {
    it.deleteRecursively()
    if (!it.exists()) {
        it.mkdirs()
    }
}

val dockerClientConfig: DefaultDockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
val dockerHttpClient: DockerHttpClient = ApacheDockerHttpClient.Builder()
    .dockerHost(URI.create("npipe:////./pipe/dockerDesktopLinuxEngine"))
    .build()

val dockerClient: DockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient)

fun startBuild(owner: String, repository: String) {
    runCatching {
        val buildDir = baseBuildDir.resolve(owner)
        buildDir.deleteRecursively()
        buildDir.mkdirs()

        // Close is immediately after since we won't need it anymore
            /* Git.cloneRepository()
            .setURI("https://github.com/$owner/$repository.git")
            .setDirectory(buildDir.resolve(repository))
            .call()
            .close()

             */

        // connect to docker
        // run git clone https://github.com/$owner/$repository.git project
        // Run gradle build
        // wait for command to exit
        // Check the command status
        // If it's successful, move the jar to the build directory
        // If it's not, log the error
        val createContainerRequest = dockerClient.createContainerCmd("build-agent:latest")
            .withCmd("./build.sh $owner $repository")
            .withPlatform("linux/amd64")
            .withHostConfig(
                HostConfig.newHostConfig()
                    // 3gb of memory
                    // 1 cpu
                    .withMemory(3L * 1024 * 1024 * 1024)
                    .withCpuCount(1)
            )
            .exec()

        dockerClient.startContainerCmd(createContainerRequest.id).exec()
        dockerClient.waitContainerCmd(createContainerRequest.id)
            .exec(object : ResultCallback<WaitResponse> {
                override fun onStart(closeable: Closeable?) {
                }

                override fun onNext(item: WaitResponse) {
                    println(item.statusCode)
                }

                override fun onError(throwable: Throwable) {
                    // handle failed build
                    throwable.printStackTrace()
                }

                override fun onComplete() {
                    val outputStream: InputStream = dockerClient.copyArchiveFromContainerCmd(createContainerRequest.id, "./artifacts/output.jar")
                        .exec()

                    val outputFile = buildDir.resolve("output.jar")
                    outputFile.outputStream().use { outputStream.copyTo(it) }
                    dockerClient.removeContainerCmd(createContainerRequest.id).exec()
                }

                override fun close() {
                    // handle failed build
                }
            })


    }.onFailure {
        it.printStackTrace()
    }
}