package com.example.firebaseauthentication1

import java.util.UUID

data class Email(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val subject: String,
    val preview: String,
    val timestamp: Long = System.currentTimeMillis()
)