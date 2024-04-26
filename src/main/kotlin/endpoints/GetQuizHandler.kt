package com.github.panarik.endpoints

import com.github.panarik.requests.Request
import com.github.panarik.response.Response
import com.github.panarik.service.QuizBuilder
import com.sun.net.httpserver.HttpHandler

class GetQuizHandler : Handler(), HttpHandler {

    override fun responseFromClientRequest(request: Request): Response =
        if (isValidRequest(request)) {
            Response(200, "OK")
        } else Response(404, "Client error")

    override fun isValidRequest(request: Request): Boolean = true

}