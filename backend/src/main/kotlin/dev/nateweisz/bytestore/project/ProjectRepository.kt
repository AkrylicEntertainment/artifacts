package dev.nateweisz.bytestore.project

import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectRepository : MongoRepository<Project, Long>{
}