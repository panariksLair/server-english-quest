package com.github.panarik.endpoints

import com.github.panarik.ServerState
import com.github.panarik.States
import com.github.panarik.log
import com.github.panarik.request.RequestVerifications
import com.github.panarik.request.Request
import com.github.panarik.response.Response
import com.github.panarik.response.ResponseSender
import com.sun.net.httpserver.HttpExchange
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class Handler {

    abstract fun responseFromClientRequest(request: Request): Response

    /**
     * Each handler verify incoming request for some user errors.
     */
    abstract fun isValidRequest(request: Request): Boolean

    /**
     * Main handler method which handle all requests. All child types of requests (GET, POST, etc.) will be handled child method which overrides responseFromClientRequest() method.
     */
    open fun handle(exchange: HttpExchange) {
        val request = getRequest(exchange)
        val response =
            responseFromServerRules() ?: responseFromCommonVerifications(request) ?: responseFromClientRequest(request)
        ResponseSender(response, exchange).send()
    }

    open fun getRequest(exchange: HttpExchange): Request {
        val requestBody = BufferedReader(InputStreamReader(exchange.requestBody)).readText()
        return Request(exchange.requestHeaders, requestBody)
    }

    /**
     *  First step off all answers. If we caught some error here (it can be server security error or database error) we do not perform any further actions.
     */
    private fun responseFromServerRules(): Response? = when (ServerState.getState()) {
        States.DATABASE_ERROR -> {
            log.error("HANDLER_ROOT: Database connection error during client GET request.")
            Response(500, "Database connection error: Please contact administrator.")
        }

        else -> null
    }

    private fun responseFromCommonVerifications(request: Request): Response? =
        if (RequestVerifications().isValidRequest(request)) {
            null
        } else {
            Response(400, "Bad request")
        }

}