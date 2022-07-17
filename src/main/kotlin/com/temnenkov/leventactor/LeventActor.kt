package com.temnenkov.leventactor

import com.temnenkov.leventbus.LeventBusForActor
import com.temnenkov.leventbus.LeventMessage

interface LeventActor<T, E> {
    fun handleMessage(leventMessage: LeventMessage, bus: LeventBusForActor<T, E>)
}
