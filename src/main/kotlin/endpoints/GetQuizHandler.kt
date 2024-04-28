package com.github.panarik.endpoints

import com.github.panarik.request.Request
import com.github.panarik.request.model.QuizBuilder
import com.github.panarik.response.Response
import com.sun.net.httpserver.HttpHandler

class GetQuizHandler : Handler(), HttpHandler {

    override fun responseFromClientRequest(request: Request): Response =
        if (isValidRequest(request)) {
            val difficulty = request.headers["Difficulty"]?.get(0) ?: "B1"
            val quiz = QuizBuilder().build(difficulty)
            Response(200, quiz)
        } else Response(404, "Client error")

    override fun isValidRequest(request: Request): Boolean = true

}