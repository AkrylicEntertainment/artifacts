package dev.nateweisz.bytestore.project.build

import dev.nateweisz.bytestore.node.Node
import dev.nateweisz.bytestore.node.State
import dev.nateweisz.bytestore.node.service.NodeService
import dev.nateweisz.bytestore.node.websocket.NodeSocketHandler
import dev.nateweisz.bytestore.node.websocket.s2n.RequestBuildMessage
import org.redundent.kotlin.xml.xml
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors

@Service
class BuildService(
    val nodeService: NodeService,
    @Lazy val nodeSocketHandler: NodeSocketHandler,
    private val buildRepository: BuildRepository,
    private val buildLogsRepository: BuildLogsRepository
) {
    private val deadNodeExecutor = Executors.newSingleThreadScheduledExecutor()
    // Key: build id, Value: (node id, build)
    val currentBuilds: MutableMap<String, Pair<String, Build>> = mutableMapOf()
    val queuedBuilds: MutableList<Build> = mutableListOf()
    val currentBuildSecrets: MutableMap<String, String> = mutableMapOf() // build id to build secret (used to upload jar to main server)

    init {
        // this executors goal is to check if any queued builds can be started
        deadNodeExecutor.scheduleWithFixedDelay({
            val openNode = findOpenNode()
            if (openNode != null && queuedBuilds.isNotEmpty()) {
                val build = queuedBuilds.removeAt(0)
                startBuildOn(openNode, build)
            }
        }, 0, 300, java.util.concurrent.TimeUnit.MILLISECONDS)
    }

    fun findOpenNode(): Node? {
        return nodeService.nodes.find { it.state == State.ACTIVE && !currentBuilds.any { build -> build.value.first == it.id } }
    }

    fun queueBuild(build: Build) {
        queuedBuilds.add(build)
    }

    fun startBuildOn(node: Node, build: Build) {
        val secret = UUID.randomUUID().toString()
        currentBuildSecrets[build.id.toString()] = secret

        currentBuilds[build.id.toString()] = Pair(node.id, build)
        nodeSocketHandler.sendMessageToNode(node.id, 0x00, RequestBuildMessage(build.owner, build.repository, build.commitHash, build.id.toString(), secret))
    }

    fun isBuilding(owner: String, repository: String, commitHash: String): Boolean {
        return currentBuilds.values.any { it.second.owner == owner && it.second.repository == repository && it.second.commitHash == commitHash }
    }

    fun finishBuild(webSocketId: String, status: BuildStatus, logs: String, pom: String? = null) {
        val nodeId = NodeSocketHandler.sessionIdToNodeId[webSocketId] ?: throw RuntimeException("Somebody's getting *******")
        val buildId = currentBuilds.keys.find { currentBuilds[it]?.first == nodeId } ?: throw RuntimeException("Somebody's getting *******")
        val build = (currentBuilds[buildId] ?: throw RuntimeException("Somebody's getting *******")).second

        if (status != BuildStatus.SUCCESS) {
            currentBuilds.remove(buildId) // if it successfully finished, it will be removed after we receive the archive
        }

        build.status = status
        build.buildBy = nodeId

        buildRepository.save(build)
        buildLogsRepository.save(BuildLogs(buildId = build.id, logs = logs))

        if (status == BuildStatus.SUCCESS) {
            val projectDir = File(".build-artifacts/${build.owner}/${build.repository}/${build.commitHash}")
            projectDir.mkdirs()

            val pomFile = File(projectDir, "pom.xml")
            pomFile.writeText(pom!!)
        }
    }
}