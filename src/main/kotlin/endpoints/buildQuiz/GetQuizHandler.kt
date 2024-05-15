package com.github.panarik.endpoints.buildQuiz

import com.github.panarik.endpoints.Handler
import com.github.panarik.log
import com.github.panarik.request.Request
import com.github.panarik.response.Response
import com.sun.net.httpserver.HttpHandler

private const val TAG = "[GetQuizHandler]"

class GetQuizHandler : Handler(), HttpHandler {

    override fun responseFromClientRequest(request: Request): Response {
        log.info("$TAG Starting response from client request...")
        if (isValidRequest(request)) {
            val difficulty = request.headers["Difficulty"]?.get(0) ?: "B1"
            log.info("$TAG Difficulty is $difficulty")
            val quiz = QuizBuilder().build(difficulty)
            log.info("$TAG Quiz is created.")
            if (QuizVerifications(quiz).isValid()) {
                val encodedQuiz = QuizParser().encode(quiz)
                log.info("$TAG Quiz encoded and ready to send.")
                return Response(200, encodedQuiz)
            } else {
                log.info("$TAG Sending invalid quiz answer.")
                return Response(204, "Invalid Quiz")
            }

        } else {
            log.info("$TAG Sending client error answer.")
            return Response(404, "Client error")
        }
    }

    override fun isValidRequest(request: Request): Boolean {
        log.info("$TAG Request is valid.")
        return true
    }
}