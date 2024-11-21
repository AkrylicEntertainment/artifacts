package dev.nateweisz.bytestore.project.storage

sealed interface FileSystem {
    /**
     * @return the total space available in bytes
     */
    fun remainingSpace(): Long

    /**
     * @return true if the file was successfully written, false otherwise
     */
    fun write(path: String, data: ByteArray): Boolean

    /**
     * @return the file data as a byte array, or null if the file does not exist
     */
    fun read(path: String): ByteArray
}