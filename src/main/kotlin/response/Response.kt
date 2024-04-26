package com.github.panarik.response

data class Response(val code: Int, val body: String) {
    val length: Long = body.length.toLong()
}