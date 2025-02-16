package com.github.panarik.endpoints.buildQuiz

import com.github.panarik.log
import com.github.panarik.model.Quiz

private const val TAG = "[QuizVerifications]"

class QuizVerifications(val quiz: Quiz) {

    fun isValid(): Boolean {
        log.info("$TAG Begin Quiz verification...")
        val isValid = !hasEmptyFields() && !hasDuplicatedAnswers()
        return if (isValid) {
            log.info("$TAG Quiz verification is passed.")
            true
        } else {
            log.error("$TAG Quiz verification is failed!")
            false
        }
    }

    private fun hasEmptyFields(): Boolean {
        val hasEmptyFields =
            quiz.summary.isEmpty() ||
                    quiz.question.isEmpty() ||
                    quiz.wrong_answer_1.isEmpty() ||
                    quiz.wrong_answer_2.isEmpty() ||
                    quiz.wrong_answer_3.isEmpty() ||
                    quiz.right_answer.isEmpty()
        return if (hasEmptyFields) {
            log.error("$TAG Quiz has empty fields!")
            true
        } else {
            log.info("$TAG Empty fields verification is passed.")
            false
        }
    }

    private fun hasDuplicatedAnswers(): Boolean {
        val allAnswers = mutableListOf(quiz.right_answer, quiz.wrong_answer_1, quiz.wrong_answer_2, quiz.wrong_answer_3)
        val uniqueAnswersCount = allAnswers.toSet().size
        return if (uniqueAnswersCount == allAnswers.size) {
            log.info("$TAG Duplicated answers verification is passed.")
            false
        } else {
            log.error("$TAG Quiz has duplicated answers!")
            true
        }
    }
}