package com.github.panarik.endpoints.buildQuiz

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.panarik.log
import com.github.panarik.request.model.Quiz
import com.github.panarik.request.model.replicate.buildQuiz.Input
import com.github.panarik.request.model.replicate.buildQuiz.QuizBuilderRequest
import com.github.panarik.request.model.replicate.build_quiz.QuizBuilderResponse
import com.github.panarik.request.model.replicate.get_quiz.QuizResponse
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import kotlin.random.Random

class QuizBuilder {

    private var tag = "[QuizBuilder]"

    fun build(difficulty: String): Quiz {
        log.info("$tag Start building new quiz...")
        val quizId = buildQuiz(difficulty).id
        Thread.sleep(2000) // ToDo: also waiting little time because we have bug from Replicate side.
        val quizResponse = requestRawQuiz(quizId)
        val quiz = parseRawQuiz(quizResponse)
        log.info("$tag Quiz is finished.")
        return quiz
    }

    private fun buildQuiz(difficulty: String): QuizBuilderResponse {
        val request = Request.Builder()
            .url("https://api.replicate.com/v1/models/meta/meta-llama-3-8b-instruct/predictions")
            .method("POST", createRequest(difficulty, themeBuilder(difficulty)).toRequestBody())
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

    private fun requestRawQuiz(id: String): QuizResponse {
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

    private fun createRequest(difficulty: String, theme: String): String {
        val request = QuizBuilderRequest(
            Input(
                top_k = 0,
                top_p = 0.9,
                prompt = "Create one Quiz for learn English ${if (theme.isNotEmpty()) "about $theme " else ""}. \\nDifficulty - ${difficulty}.\\nQuiz should have these fields:\\n1. One field: **Summary**.\\n2. One field: **Question**. \\n3. One text block named **Wrong Answers** with three wrong (incorrect English grammar) answers. \\n4. One text block named **Right answer** with right answer.",
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
        log.info("$tag Starting parse raw quiz....")
        try {
            val summary = getSummary(quizResponse.quiz)
            log.info("$tag Summary: $summary")
            val question = getQuestion(quizResponse.quiz)
            log.info("$tag Question: $question")
            val rightAnswer = getRightAnswer(quizResponse.quiz)
            log.info("$tag Right Answer: $rightAnswer")
            val answers = getWrongAnswers(quizResponse.quiz)
            log.info("$tag Wrong Answers: ${answers.joinToString()}")
            log.info("$tag Raw quiz is parsed.")
            return Quiz(quizResponse.id, summary, question, answers, rightAnswer)
        } catch (e: Exception) {
            log.error("$tag Error caught during raw quiz parsing. Original exception: ${e.message}. Original raw quiz: ${quizResponse.quiz}")
            return Quiz("", "", "", emptyList(), "")
        }

    }

    private fun getSummary(input: String): String {
        val regex = Regex("(?<=Summary)([\\S\\s]+)(?=(Question))")
        val block = regex.find(input)?.value ?: ""
        log.info("$tag Summary regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
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
            log.error("$tag Error caught during parsing 'Summary' block. Original exception: ${e.message}. Original question block: $block")
            return ""
        }
    }

    private fun getQuestion(input: String): String {
        val regex = Regex("(?<=Question)([\\S\\s]+)(?=(Wrong [Aa]nswers))")
        val block = regex.find(input)?.value ?: ""
        log.info("$tag Question regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
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
            log.error("$tag Error caught during parsing 'Question' block. Original exception: ${e.message}. Original question block: $block")
            return ""
        }

    }

    private fun getWrongAnswers(input: String): List<String> {
        val regex = Regex("(?<=Wrong [Aa]nswers)([\\S\\s]+)(?=(Right [Aa]nswer))")
        val block = regex.find(input)?.value ?: ""
        log.info("$tag WrongAnswers regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
        try {
            var lines = block.split("\n")

            // In case if new lines is appeared like one String line: :**\n\n* Will have travelled\n\n* Am going to travel\n\n* Have been travelling\n\n**
            if (lines.size == 1) {
                lines = block.split("\\n")
            }
            val rawAnswers = lines.filter { it.contains(Regex("[A-Za-z]")) }
            val answers = rawAnswers.map { it.replace(Regex("(^[* \\d\\.]+)|([A-da-d]\\) )"), "") }
            return answers
        } catch (e: Exception) {
            log.error("$tag Error caught during parsing 'Wrong answer' block. Original exception: ${e.message}. Original question block: $block")
            return emptyList()
        }

    }

    private fun getRightAnswer(input: String): String {
        val regex = Regex("(?<=Right [Aa]nswer)[\\S\\s]+")
        val block = regex.find(input)?.value ?: ""
        log.info("$tag RightAnswer regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
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
        val theme = themes[Random.nextInt(0, themes.lastIndex)]
        log.info("$tag Current quiz theme: $theme")
        return theme
    }

    private fun verifyQuiz(quiz: Quiz) {
        if (quiz.isValid()) {
            log.info("$tag Quiz is valid.")
        } else {
            log.error("$tag Quiz is invalid. Summary=${quiz.summary} Question=${quiz.question} RightAnswer=${quiz.right_answer} WrongAnswers=${quiz.wrong_answers.joinToString()}")
        }
    }
}