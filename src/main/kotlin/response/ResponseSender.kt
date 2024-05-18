package com.github.panarik.response

import com.github.panarik.log
import com.sun.net.httpserver.HttpExchange

private const val TAG: String = "[SENDER]"

class ResponseSender(private val response: Response, private val exchange: HttpExchange) {

    fun send() {
        log.info("$TAG Sending response...")
        val outputArray = response.body.toByteArray()
        exchange.sendResponseHeaders(response.code, outputArray.size.toLong())
        val os = exchange.responseBody
        log.info("$TAG Response code=${exchange.responseCode}, headers=${exchange.responseHeaders.entries}, body=${response.body}")
        try {
            if (exchange.responseCode == 200) {
                os.write(outputArray)
                log.info("$TAG Response is sent with body. body_length_symbols=${response.body.length}, body_length_bytes=${outputArray.size}")
            }
            log.info("$TAG Response is sent without body.")
        } catch (e: Exception) {
            log.error("$TAG Exception caught during sending output stream. Message=${e.message} Cause=${e.cause}")
        }
        os.close()
        log.info("$TAG Output stream is closed.")
    }

}