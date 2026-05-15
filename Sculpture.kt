package com.shilpa.kala

data class Sculpture(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val style: String = "",
    val history: String = "",
    val artistName: String = "",
    val artistWhatsapp: String = "",
    val artistUid: String = "",
    val artistLocation: String = "", // e.g., Shivarapatna
    val timelineImages: List<String> = emptyList()
)