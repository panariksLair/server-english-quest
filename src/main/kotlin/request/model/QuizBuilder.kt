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
import kotlin.random.Random

private const val TAG = "[QuizBuilder]"

class QuizBuilder {

    private var idResponse: String? = null
    private var quizResponse: String? = null

    /**
     * @return Quiz body or empty string if something went wrong.
     */
    fun build(difficulty: String): String {
        buildQuiz(difficulty)
        val quizResponse = getRawQuiz()
        val quiz = parseRawQuiz(quizResponse)
        val result = jacksonObjectMapper().writeValueAsString(QuizSession(quiz))
        return result
    }

    private fun buildQuiz(difficulty: String) {
        val request = Request.Builder()
            .url("https://api.replicate.com/v1/models/meta/meta-llama-3-8b-instruct/predictions")
            .method("POST", createRequest(difficulty, themeBuilder(difficulty)).toRequestBody())
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
                        val quiz = response.body.string()
                        this@QuizBuilder.quizResponse = quiz
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

    private fun createRequest(difficulty: String, theme: String): String {
        val request = QuizBuilderRequest(
            Input(
                top_k = 0,
                top_p = 0.9,
                prompt = "Create one Quiz for learn English ${if (theme.isNotEmpty()) "about $theme " else ""}. \\nDifficulty - ${difficulty}.\\nQuiz should have these fields:\\n1. One field: **Summary**.\\n2. One field: **Question**. \\n3. One text block named **Wrong Answers** with three wrong answers. \\n4. One text block named **Right answer** with right answer.",
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
        try {
            val summary = getSummary(quizResponse.quiz)
            val question = getQuestion(quizResponse.quiz)
            val rightAnswer = getRightAnswer(quizResponse.quiz)
            val answers = getWrongAnswers(quizResponse.quiz)
            return Quiz(summary, question, answers, rightAnswer)
        } catch (e: Exception) {
            log.error("$TAG Error caught during raw quiz parsing. Original exception: ${e.message}. Original raw quiz: ${quizResponse.quiz}")
            return Quiz("", "", emptyList(), "")
        }

    }

    private fun getSummary(input: String): String {
        val block = Regex("(?<=Summary)([\\S\\s]+)(?=(Question))").find(input)?.value ?: ""
        try {
            val lines = block.split("\n")
            val rawSummary = lines.first { it.contains(Regex("[A-Za-z]")) }
            val prefix = Regex("^[*: \\d\\.]+").find(rawSummary)?.value ?: ""
            var summary = ""
            if (prefix == " ") {
                summary = rawSummary.drop(1)
            } else {
                summary = rawSummary.replace(prefix, "")
            }
            return summary
        } catch (e: Exception) {
            log.error("$TAG Error caught during parsing 'Summary' block. Original exception: ${e.message}. Original question block: $block")
            return ""
        }

    }

    private fun getQuestion(input: String): String {
        val block = Regex("(?<=Question)([\\S\\s]+)(?=(Wrong [Aa]nswers))").find(input)?.value ?: ""
        try {
            val lines = block.split("\n")
            val questionLines = lines.filter { it.contains(Regex("[A-Za-z]")) }.toMutableList()
            val prefix = Regex("^[*: \\d\\.]+").find(questionLines[0])?.value ?: ""
            val questionFirstLine = if (prefix == " ") {
                questionLines[0].drop(1)
            } else {
                questionLines[0].replace(prefix, "")
            }
            questionLines[0] = questionFirstLine
            val question = questionLines.joinToString("\n")
            return question
        } catch (e: Exception) {
            log.error("$TAG Error caught during parsing 'Question' block. Original exception: ${e.message}. Original question block: $block")
            return ""
        }

    }

    private fun getWrongAnswers(input: String): List<String> {
        val block =
            Regex("(?<=Wrong [Aa]nswers)([\\S\\s]+)(?=(Right [Aa]nswer))")
                .find(input)?.value ?: ""
        try {
            val lines = block.split("\n")
            val rawAnswers = lines.filter { it.contains(Regex("[A-Za-z]")) }
            val answers = rawAnswers.map { it.replace(Regex("(^[* \\d\\.]+)|([A-da-d]\\) )"), "") }
            return answers
        } catch (e: Exception) {
            log.error("$TAG Error caught during parsing 'Wrong answer' block. Original exception: ${e.message}. Original question block: $block")
            return emptyList()
        }

    }

    private fun getRightAnswer(input: String): String {
        val block = Regex("(?<=Right [Aa]nswer)[\\S\\s]+").find(input)?.value ?: ""
        try {
            val lines = block.split("\n")
            val rawAnswersLines = lines.filter { it.contains(Regex("[A-Za-z]")) }
            if (rawAnswersLines.isNotEmpty()) {
                val firstLine = rawAnswersLines[0]
                val prefix = Regex("(^[*: \\d\\.\\n]+)|([A-Da-d]\\) )").find(firstLine)?.value ?: ""
                val result = if (prefix == " ") {
                    firstLine.drop(1)
                } else {
                    firstLine.replace(prefix, "")
                }
                return result
            } else return ""
        } catch (e: Exception) {
            log.error("Error caught during parsing 'Right answer' block. Original exception: ${e.message}.")
            return ""
        }
    }

    private fun themeBuilder(difficulty: String): String {
        val themes = when (difficulty) {
            "A1", "A2" -> {
                listOf(
                    "",
                    "Present Simple",
                    "Past Simple",
                    "Present Continuous",
                    "Past Continuous",
                    "Future Simple",
                    "Present Perfect Simple"
                )
            }

            "B1", "B2" -> {
                listOf(
                    "",
                    "Gradable and Non-gradable",
                    "British English vs. American English",
                    "Capital Letters and Apostrophes",
                    "Conditionals: Third and Mixed",
                    "Conditionals: Zero, First, and Second",
                    "Contrasting Ideas: ‘Although,’ ‘Despite,’ and Others",
                    "Different Uses of ‘Used To’",
                    "Future Continuous and Future Perfect",
                    "Future Forms: ‘Will,’ ‘Be Going To,’ and Present Continuous",
                    "Intensifiers: ‘So’ and ‘Such’",
                    "Modals: Deductions About the Past and Present"
                )
            }

            else -> {
                listOf("")
            }
        }
        return themes[Random.nextInt(0, themes.lastIndex)]
    }
}