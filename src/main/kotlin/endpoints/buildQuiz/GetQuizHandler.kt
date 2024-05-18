package com.github.panarik.endpoints.buildQuiz

import com.github.panarik.endpoints.Handler
import com.github.panarik.log
import com.github.panarik.request.Request
import com.github.panarik.request.model.replicate.Task
import com.github.panarik.response.Response
import com.github.panarik.service.DatabaseManager
import com.sun.net.httpserver.HttpHandler

private const val TAG = "[GetQuizHandler]"

class GetQuizHandler : Handler(), HttpHandler {

    override fun responseFromClientRequest(request: Request): Response {
        log.info("$TAG Starting response from client request...")
        if (isValidRequest(request)) {
            val difficulty = request.headers["Difficulty"]?.get(0) ?: "B1"
            val task = Task(difficulty)
            log.info("$TAG Difficulty is $difficulty")
            val quiz = QuizBuilder().build(task)
            log.info("$TAG Quiz is created.")
            if (QuizVerifications(quiz).isValid()) {
                DatabaseManager.safe(quiz)
                val encodedQuiz = QuizParser().encode(quiz)
                log.info("$TAG Quiz encoded and ready to send. id=${quiz.id}")
                return Response(200, encodedQuiz)
            } else {
                val response = Response(204, "")
                log.info("$TAG Sending invalid quiz answer. Code=${response.code}, Body=${response.body}")
                return response
            }

        } else {
            val response = Response(404, "")
            log.info("$TAG Sending client error answer. Code=${response.code}, Body=${response.body}")
            return response
        }
    }

    override fun isValidRequest(request: Request): Boolean {
        log.info("$TAG Request is valid.")
        return true
    }
}