package com.github.panarik.endpoints.buildQuiz

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.panarik.log
import com.github.panarik.request.model.Quiz
import com.github.panarik.request.model.QuizSession

class QuizParser {

    fun encode(quiz: Quiz): String =
        try {
            val result = jacksonObjectMapper().writeValueAsString(QuizSession(quiz.id, quiz))
            result
        } catch (e: Exception) {
            log.error("Error caught during quiz writing. Original exception: ${e.message}")
            ""
        }


}