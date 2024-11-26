package dev.nateweisz.bytestore.lib

fun String.takeFirst(n: Int): String {
    if (n >= this.length) {
        return this
    }

    return this.substring(0, n)
}