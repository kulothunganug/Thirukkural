package com.kulothunganug.thirukkural


fun formatTransliteration(text: String): String {
    val words = text.split(Regex("\\s+"))
    val firstLine = words.take(4).joinToString(" ")
    val secondLine = words.slice(4 until 7).joinToString(" ")
    return "$firstLine\n$secondLine"
}