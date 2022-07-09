package com.temnenkov.leventbus

import java.io.Serializable
import java.time.Duration

data class LeventMessage(
    val id: String,
    val from: String,
    val to: String,
    val payload: String,
    val maxDuration: Duration
) : Serializable
