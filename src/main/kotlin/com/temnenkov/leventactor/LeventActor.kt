package com.temnenkov.leventactor

import com.temnenkov.leventbus.LeventMessage

interface LeventActor {
    fun handleMessage(leventMessage: LeventMessage)
}
