package com.github.panarik.request.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.panarik.log
import com.github.panarik.request.model.replicate.buildQuiz.Input
import com.github.panarik.request.model.replicate.buildQuiz.QuizBuilderRequest
import com.github.panarik.request.model.replicate.build_quiz.QuizBuilderResponse
import com.github.panarik.request.model.replicate.get_quiz.QuizResponse
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

private const val TAG = "[QuizBuilder]"

class QuizBuilder {

    private var idResponse: String? = null
    private var quizResponse: String? = null

    /**
     * @return Quiz body or empty string if something went wrong.
     */
    fun build(): String {
        buildQuiz()
        val quizResponse = getRawQuiz()
        val quiz = parseRawQuiz(quizResponse)
        val result = jacksonObjectMapper().writeValueAsString(QuizSession(quiz))
        return result
    }

    private fun buildQuiz() {
        val request = Request.Builder()
            .url("https://api.replicate.com/v1/models/meta/meta-llama-3-8b-instruct/predictions")
            .method("POST", createRequest("A1").toRequestBody())
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer r8_TF9Xx4keKqZHPRfFUYXEZ344nlyg5Iu1QiT3x")
            .build()
        OkHttpClient().newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                log.error("$TAG Error caught during quiz building. Original exception: ${e.message}")
                idResponse = ""
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 201) {
                    log.info("$TAG Response code = 201. Quiz is created!")
                    this@QuizBuilder.idResponse = response.body.string()
                } else {
                    log.info("$TAG Response code = ${response.code}. Message: ${response.body.string()}")
                    this@QuizBuilder.idResponse = ""
                }

            }
        })
        while (idResponse == null) {
            log.info("$TAG Waiting replicate.com answer...")
            Thread.sleep(500)
        }
    }

    private fun getRawQuiz(): QuizResponse {
        if (idResponse?.isNotEmpty() == true) {
            val replicate: QuizBuilderResponse = jacksonObjectMapper().readValue(idResponse!!)
            val request = Request.Builder()
                .url("https://api.replicate.com/v1/predictions/${replicate.id}")
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer r8_TF9Xx4keKqZHPRfFUYXEZ344nlyg5Iu1QiT3x")
                .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    log.error("$TAG Error caught during quiz getting. Original exception: ${e.message}")
                    quizResponse = ""
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        log.info("$TAG Response code = 200. Quiz is received!")
                        this@QuizBuilder.quizResponse = response.body.string()
                    } else {
                        log.info("$TAG Response code = ${response.code}. Message: ${response.body.string()}")
                        this@QuizBuilder.quizResponse = ""
                    }
                }
            })
            while (quizResponse == null) {
                log.info("$TAG Waiting replicate.com answer...")
                Thread.sleep(500)
            }
            return if (quizResponse?.isNotEmpty() == true) {
                jacksonObjectMapper().readValue(quizResponse!!)
            } else {
                QuizResponse(emptyList())
            }
        } else {
            return QuizResponse(emptyList())
        }
    }

    private fun createRequest(difficulty: String): String {
        val request = QuizBuilderRequest(
            Input(
                top_k = 0,
                top_p = 0.9,
                prompt = "Make one example of very random Quiz for learning English. Use any possible questions. Do not use Hello as right answer.\\nDifficulty - ${difficulty}.\\nQuiz should have these fields:\\n1. Summary. Several words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.",
                temperature = 0.6,
                system_prompt = "You are a English teacher",
                length_penalty = 1,
                max_new_tokens = 512,
                stop_sequences = "<|end_of_text|>,<|eot_id|>",
                prompt_template = "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\\n\\nYou are a helpful assistant<|eot_id|><|start_header_id|>user<|end_header_id|>\\n\\n{prompt}<|eot_id|><|start_header_id|>assistant<|end_header_id|>\\n\\n",
                presence_penalty = 0
            )
        )
        return jacksonObjectMapper().writeValueAsString(request)
    }

    /**
     * Input example:
     *
     * Here is an example of a quiz for learning English at the A1 level:
     *
     * **Summary:** "My Family"
     *
     * **Question:** What is the person you live with called?
     *
     * **Wrong answers:**
     *
     * 1. Friend
     * 2. Teacher
     * 3. Stranger
     *
     * **Right answer:** Family
     */
    private fun parseRawQuiz(quizResponse: QuizResponse): Quiz {
        val summary = Regex("(?<=\\*\\*Summary:\\*\\* )(.+)").find(quizResponse.quiz)?.value ?: ""
        val question = Regex("(?<=\\*\\*Question:\\*\\* )(.+)").find(quizResponse.quiz)?.value ?: ""
        val wrongAnswers =
            Regex("(?<=\\*\\*Wrong [Aa]nswers:\\*\\*)([\\S\\s]+)(?=(\\*\\*Right [Aa]nswer:\\*\\*))")
                .find(quizResponse.quiz)?.value ?: ""
        val wrongAnswer1 = wrongAnswers.substringAfter("1. ").substringBefore('\n')
        val wrongAnswer2 = wrongAnswers.substringAfter("2. ").substringBefore('\n')
        val wrongAnswer3 = wrongAnswers.substringAfter("3. ").substringBefore('\n')
        val rightAnswerRaw =
            Regex("(?<=\\*\\*Right [Aa]nswer:\\*\\*)[\\S\\s]+").find(quizResponse.quiz)?.value ?: ""
        val rightAnswer = rightAnswerRaw.replace("\n", "").replace(" ", "")
        return Quiz(summary, question, listOf(wrongAnswer1, wrongAnswer2, wrongAnswer3), rightAnswer)
    }

}