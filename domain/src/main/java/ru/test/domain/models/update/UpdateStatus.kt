package ru.test.domain.models.update

data class UpdateStatus(
    val isAvailable: Boolean,
    val release: Release?
)