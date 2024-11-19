package dev.nateweisz.bytestore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ByteStore

fun main(args: Array<String>) {
    runApplication<ByteStore>(*args)
}
