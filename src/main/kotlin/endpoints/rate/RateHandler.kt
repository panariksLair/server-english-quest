package com.github.panarik.endpoints.rate

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.panarik.endpoints.Handler
import com.github.panarik.endpoints.buildQuiz.QuizParser
import com.github.panarik.endpoints.rate.model.QuizRate
import com.github.panarik.log
import com.github.panarik.request.Request
import com.github.panarik.response.Response
import com.github.panarik.service.DatabaseManager

private const val TAG = "[RateHandler]"

class RateHandler : Handler() {

    override fun responseFromClientRequest(request: Request): Response {
        log.info("$TAG Starting response from client request...")
        val quizRate = getQuizRate(request.body)
        if (quizRate != null && quizRate.isValid()) {
            log.info("$TAG Quiz parsed. Sending quiz rate to database.")
            return if (DatabaseManager.rate(quizRate)) {
                Response(200, "OK")
            } else {
                Response(400, "Bad request")
            }
        } else {
            log.error("Exception caught during Quiz parsing. (Quiz rate = $quizRate) Sending error response...")
            return Response(400, "Bad request")
        }
    }

    override fun isValidRequest(request: Request): Boolean {
        return true
    }

    private fun getQuizRate(input: String): QuizRate? =
        try {
            log.info("$TAG Parsing request body to QuizRate object...")
            val quizRate = jacksonObjectMapper().readValue(input, QuizRate::class.java)
            quizRate
        } catch (e: Exception) {
            log.info("$TAG Exception caught during rate parsing. Message: ${e.message}. Input string: $input")
            null
        }

}