package dev.nateweisz.bytestore.project.storage


class LocalFileSystem : FileSystem{
    override fun remainingSpace(): Long {
        TODO("Not yet implemented")
    }

    override fun write(path: String, data: ByteArray): Boolean {
        TODO("Not yet implemented")
    }

    override fun read(path: String): ByteArray {
        TODO("Not yet implemented")
    }
}