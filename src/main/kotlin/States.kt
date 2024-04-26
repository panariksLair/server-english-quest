package com.github.panarik

object ServerState {

    private var state: States = States.OPERATIONAL

    fun setState(states: States) {
        this.state = states
    }

    fun getState() = state
}

enum class States {
    OPERATIONAL,
    DATABASE_ERROR
}