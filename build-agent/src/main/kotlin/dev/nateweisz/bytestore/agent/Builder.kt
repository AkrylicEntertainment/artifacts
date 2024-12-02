package dev.nateweisz.bytestore.agent

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.WaitResponse
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.URLEncoder
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

private const val MAX_UPLOAD_SIZE = 1024 * 1024 * 500

@OptIn(InternalAPI::class)
fun startBuild(session: DefaultClientWebSocketSession, owner: String, repository: String, commitHash: String, buildId: String, buildSecret: String) {
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
            .withCmd("./build.sh $owner $repository $commitHash")
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

                        val response = client.post("http://localhost:8080/api/projects/$buildId/finish/$buildSecret") {
                            contentType(ContentType.MultiPart.FormData)
                            setBody(
                                MultiPartFormDataContent(
                                    formData {
                                        append(
                                            "archive",
                                            outputFile.inputStream().asInput(),
                                            Headers.build {
                                                append(HttpHeaders.ContentType, "application/octet-stream")
                                                append(HttpHeaders.ContentDisposition, "filename=output.jar")
                                                append(HttpHeaders.ContentLength, outputFile.length().toString())
                                            }
                                        )
                                    }
                                )
                            )
                            timeout {
                                requestTimeoutMillis = 5 * 60 * 1000
                                connectTimeoutMillis = 30 * 1000
                            }
                        }

                        LOGGER.info { "Build finished: ${response.status}" }
                    }
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
