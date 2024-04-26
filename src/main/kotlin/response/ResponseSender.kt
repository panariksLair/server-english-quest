package com.github.panarik.response

import com.github.panarik.log
import com.sun.net.httpserver.HttpExchange

class ResponseSender(private val response: Response, private val exchange: HttpExchange) {

    private val TAG: String = "SENDER:"

    fun send() {
        exchange.sendResponseHeaders(response.code, response.length)
        val os = exchange.responseBody
        os.write(response.body.toByteArray())
        log.info ( "$TAG Response is sent. Code=${response.code} Size=${response.length}" )
        os.close()
    }

}