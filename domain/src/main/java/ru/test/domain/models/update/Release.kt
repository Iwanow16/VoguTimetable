package ru.test.domain.models.update

data class Release(
    val version: String,
    val changelog: String,
    val downloadUrl: String,
    val fileSize: Long
)