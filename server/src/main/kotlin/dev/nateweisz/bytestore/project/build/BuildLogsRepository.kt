package dev.nateweisz.bytestore.project.build

import org.springframework.data.mongodb.repository.MongoRepository

interface BuildLogsRepository : MongoRepository<BuildLogs, String> {
}