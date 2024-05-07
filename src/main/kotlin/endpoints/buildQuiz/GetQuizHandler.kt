package com.github.panarik.endpoints.buildQuiz

import com.github.panarik.endpoints.Handler
import com.github.panarik.request.Request
import com.github.panarik.response.Response
import com.sun.net.httpserver.HttpHandler

class GetQuizHandler : Handler(), HttpHandler {

    override fun responseFromClientRequest(request: Request): Response {
        if (isValidRequest(request)) {
            val difficulty = request.headers["Difficulty"]?.get(0) ?: "B1"
            val quiz = QuizBuilder().build(difficulty)
            if (QuizVerifications(quiz).isValid()) {
                val encodedQuiz = QuizParser().encode(quiz)
                return Response(200, encodedQuiz)
            } else {
                return Response(204, "Invalid Quiz")
            }

        } else return Response(404, "Client error")
    }


    override fun isValidRequest(request: Request): Boolean = true

}