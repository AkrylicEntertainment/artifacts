package dev.nateweisz.bytestore

import org.kohsuke.github.GitHub
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope

@SpringBootApplication
class ByteStore

fun main(args: Array<String>) {
    runApplication<ByteStore>(*args)
}