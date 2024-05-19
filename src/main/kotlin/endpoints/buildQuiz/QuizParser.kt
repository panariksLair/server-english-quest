package com.github.panarik.endpoints.buildQuiz

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.panarik.log
import com.github.panarik.model.Quiz
import com.github.panarik.model.QuizSession

private const val TAG = "[QuizParser]"

class QuizParser {

    fun encode(quiz: Quiz): String? =
        try {
            jacksonObjectMapper().writeValueAsString(QuizSession(quiz.id, quiz))
        } catch (e: Exception) {
            log.error("$TAG Error caught during quiz writing. Original exception: ${e.message}")
            null
        }

    fun decode(input: String): Quiz? =
        try {
            jacksonObjectMapper().readValue(input, Quiz::class.java)
        } catch (e: Exception) {
            log.error("$TAG Error caught during quiz reading. Original exception: ${e.message}")
            null
        }


}