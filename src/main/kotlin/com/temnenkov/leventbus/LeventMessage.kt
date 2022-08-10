package com.temnenkov.leventbus

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import java.io.Serializable
import java.time.Duration

data class LeventMessage(
    val id: String = NanoIdUtils.randomNanoId(),
    val from: String? = null,
    val to: String,
    val payload: String,
    val maxDuration: Duration = Duration.ofSeconds(30)
) : Serializable
