package com.github.panarik.request

import com.sun.net.httpserver.Headers

data class Request(val headers: Headers, val body: String) {

    fun hasEmptyBody(): Boolean = body == ""

    override fun toString(): String =
        "headers=${headers.entries.joinToString()}, body=$body"

}

