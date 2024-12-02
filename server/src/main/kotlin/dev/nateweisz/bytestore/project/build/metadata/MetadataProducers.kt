package dev.nateweisz.bytestore.project.build.metadata

interface MetadataProducers {
    fun produceMetadata(): Map<String, String>

    // TODO: do this in the futuer (we may never need it)
}