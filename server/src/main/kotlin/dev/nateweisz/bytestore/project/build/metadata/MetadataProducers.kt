package dev.nateweisz.bytestore.project.build.metadata

interface MetadataProducers {
    fun produceMetadata(): Map<String, String>
}