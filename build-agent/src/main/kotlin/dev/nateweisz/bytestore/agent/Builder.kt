package dev.nateweisz.bytestore.agent

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.WaitResponse
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.net.URI
import java.nio.ByteBuffer

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

fun startBuild(session: DefaultClientWebSocketSession, owner: String, repository: String, commitHash: String, buildSecret: String) {
    runCatching {
        LOGGER.info { "Starting build for https://github.com/$owner/$repository" }
        baseBuildDir.deleteRecursively()
        baseBuildDir.mkdirs()

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
        LOGGER.info { "Docker container has been started: ${createContainerRequest.id}" }

        dockerClient.startContainerCmd(createContainerRequest.id).exec()
        dockerClient.waitContainerCmd(createContainerRequest.id)
            .exec(object : ResultCallback<WaitResponse> {
                override fun onStart(closeable: Closeable?) {
                }

                override fun onNext(item: WaitResponse) {
                    LOGGER.info { "Build output code: ${item.statusCode} (0 is success)" }
                }

                override fun onError(throwable: Throwable) {
                    // handle failed build
                    LOGGER.atWarn {
                        message = "Build failed"
                        cause = throwable
                        payload = buildMap(2) {
                            put("owner", owner)
                            put("repository", repository)
                        }
                    }

                    // send build finished packet
                    val buffer = ByteBuffer.allocate(4 + 4 + "FAILED".length + 4 + "".length).apply {
                        putInt(0x01)
                        writeString("FAILED")
                        writeString("")
                    }

                    runBlocking {
                        session.send(Frame.Binary(true, buffer.array()))
                    }
                }

                override fun onComplete() {
                    LOGGER.info { "Build finished" }
                    val outputStream: InputStream = dockerClient.copyArchiveFromContainerCmd(createContainerRequest.id, "./artifacts/output.jar")
                        .exec()
                    val outputFile = baseBuildDir.resolve("output.jar")

                    val pomStream: InputStream = dockerClient.copyArchiveFromContainerCmd(createContainerRequest.id, "./artifacts/pom.xml")
                        .exec()
                    val pomFile = baseBuildDir.resolve("pom.xml")

                    outputFile.outputStream().use { outputStream.copyTo(it) }
                    pomFile.outputStream().use { pomStream.copyTo(it) }
                    dockerClient.removeContainerCmd(createContainerRequest.id).exec()

                    //client.post {  }
                    // send build finished packet
                    // id, status, logs, pom
                    val pomText = pomFile.readText()
                    val buffer = ByteBuffer.allocate(4 + 4 + "SUCCESS".length + 4 + 0 + 4 + pomText.length).apply {
                        putInt(0x01)
                        writeString("SUCCESS")
                        writeString("")
                        writeString(pomText)
                    }

                    runBlocking {
                        session.send(Frame.Binary(true, buffer.array()))

                        // now we do ktor client jar thing here
                    }

                    // post jar to thing
                }

                override fun close() {
                    // handle failed build
                    LOGGER.atWarn {
                        message = "Build failed because docker container closed"
                        payload = buildMap(2) {
                            put("owner", owner)
                            put("repository", repository)
                        }
                    }

                    // send build finished packet
                    val buffer = ByteBuffer.allocate(4 + 4 + "FAILED".length + 4 + "".length).apply {
                        putInt(0x01)
                        writeString("FAILED")
                        writeString("")
                    }

                    runBlocking {
                        session.send(Frame.Binary(true, buffer.array()))
                    }
                }
            })


    }.onFailure {
        LOGGER.atError {
            message = "Failed to start build"
            cause = it
            payload = buildMap(2) {
                put("owner", owner)
                put("repository", repository)
            }
        }
    }
}