package com.temnenkov.leventbus

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun LeventMessage.toByteArray(): ByteArray {
    ByteArrayOutputStream().use { bos ->
        ObjectOutputStream(bos).use { oos ->
            oos.writeObject(this)
        }
        return bos.toByteArray()
    }
}

fun ByteArray.toLeventMessage(): LeventMessage {
    ByteArrayInputStream(this).use { bis ->
        ObjectInputStream(bis).use { ois ->
            return ois.readObject() as LeventMessage
        }
    }
}
