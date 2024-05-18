package com.github.panarik.endpoints.buildQuiz

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.panarik.log
import com.github.panarik.request.model.Quiz
import com.github.panarik.request.model.replicate.Task
import com.github.panarik.request.model.replicate.build_quiz.QuizBuilderResponse
import com.github.panarik.request.model.replicate.get_quiz.QuizResponse
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class QuizBuilder {

    private var tag = "[QuizBuilder]"

    fun build(task: Task): Quiz {
        log.info("$tag Start building new quiz...")
        val quizId = createRawQuiz(task).id
        Thread.sleep(2000) // ToDo: also waiting little time because we have bug from Replicate side.
        val quizResponse = getRawQuiz(quizId)
        val quiz = Quiz(
            id = quizId,
            reviewed = false,
            difficult = task.difficult,
            topic = task.topic,
            summary = quizResponse.getSummary(),
            question = quizResponse.getQuestion(),
            wrong_answers = quizResponse.getWrongAnswers(),
            right_answer = quizResponse.getRightAnswer(),
            votes_positive = 0,
            votes_negative = 0
        )
        log.info("$tag Quiz is finished.")
        return quiz
    }

    private fun createRawQuiz(task: Task): QuizBuilderResponse {
        val request = Request.Builder()
            .url("https://api.replicate.com/v1/models/meta/meta-llama-3-8b-instruct/predictions")
            .method("POST", task.request.toRequestBody())
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer r8_TF9Xx4keKqZHPRfFUYXEZ344nlyg5Iu1QiT3x")
            .build()
        try {
            val response = OkHttpClient().newCall(request).execute()
            if (response.code == 201) {
                log.info("$tag Quiz is created! Response code = 201.")
                val quizId: QuizBuilderResponse = jacksonObjectMapper().readValue(response.body.string())
                tag = tag.plus(" (${quizId.id})")
                return quizId
            } else {
                log.error("$tag Response code = ${response.code}. Message: ${response.body.string()}")
                tag = tag.plus(": ${UUID.randomUUID()}")
                return QuizBuilderResponse("")
            }
        } catch (e: Exception) {
            log.error("$tag Error caught during quiz building. Original exception: ${e.message}")
            tag = tag.plus(": ${UUID.randomUUID()}")
            return QuizBuilderResponse("")
        }
    }

    private fun getRawQuiz(id: String): QuizResponse {
        if (id.isNotEmpty()) {
            val request = Request.Builder()
                .url("https://api.replicate.com/v1/predictions/$id")
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer r8_TF9Xx4keKqZHPRfFUYXEZ344nlyg5Iu1QiT3x")
                .build()
            return try {
                val response = OkHttpClient().newCall(request).execute()
                if (response.code == 200) {
                    log.info("$tag Response code = 200. Quiz is received!")
                    parseRawQuiz(response.body.string())
                } else {
                    log.info("$tag Response code = ${response.code}. Message: ${response.body.string()}")
                    return QuizResponse("", emptyList())
                }
            } catch (e: Exception) {
                log.error("$tag Error caught during quiz getting. Original exception: ${e.message}")
                return QuizResponse("", emptyList())
            }
        } else {
            log.error("$tag Empty quiz id received from replicate.com")
            return QuizResponse("", emptyList())
        }
    }

    private fun parseRawQuiz(answer: String): QuizResponse =
        if (answer.isNotEmpty()) {
            log.info("$tag Raw quiz response: $answer")
            val quiz: QuizResponse = jacksonObjectMapper().readValue(answer)
            log.info("$tag Raw quiz output: ${quiz.output.joinToString("")}")
            quiz
        } else {
            log.error("$tag Empty quiz response received.")
            QuizResponse("", emptyList())
        }
}