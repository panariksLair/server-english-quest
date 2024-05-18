package com.github.panarik.request.model.replicate

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.panarik.log
import com.github.panarik.request.model.replicate.buildQuiz.Input
import com.github.panarik.request.model.replicate.buildQuiz.QuizBuilderRequest
import kotlin.random.Random

private const val TAG = "[Task]"

/**
 * Task for AI.
 */
data class Task(val difficult: String) {

    val topic: String = createTopic(difficult)
    val request: String = createRequest(difficult, topic)

    private fun createTopic(difficulty: String): String {
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
        log.info("$TAG Current quiz theme: $theme")
        return theme
    }

    fun createRequest(difficulty: String, theme: String): String {
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

}