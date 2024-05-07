package com.github.panarik.endpoints.buildQuiz

import com.github.panarik.log
import com.github.panarik.request.model.Quiz

private const val TAG = "[QuizVerifications]"

class QuizVerifications(val quiz: Quiz) {

    fun isValid(): Boolean {
        log.info("$TAG Begin Quiz verification...")
        val isValid = !hasEmptyFields() && !hasDuplicatedAnswers() && !questionContainsAnswer()
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
            quiz.summary.isEmpty() || quiz.question.isEmpty() || quiz.wrong_answers.size < 3 || quiz.right_answer.isEmpty()
        return if (hasEmptyFields) {
            log.error("$TAG Quiz has empty fields!")
            true
        } else {
            log.info("$TAG Empty fields verification is passed.")
            false
        }
    }

    private fun hasDuplicatedAnswers(): Boolean {
        val allAnswers = mutableListOf(quiz.right_answer).also { it.addAll(quiz.wrong_answers) }
        val uniqueAnswersCount = allAnswers.toSet().size
        return if (uniqueAnswersCount == 4) {
            log.info("$TAG Duplicated answers verification is passed.")
            false
        } else {
            log.error("$TAG Quiz has duplicated answers!")
            true
        }
    }

    private fun questionContainsAnswer(): Boolean {
        val questionContainsAnswer = quiz.question.contains(quiz.right_answer) || quiz.question == quiz.right_answer
        return if (questionContainsAnswer) {
            log.error("$TAG Quiz question contains right answer!")
            true
        } else {
            log.info("$TAG QuestionContainsAnswer verification is passed.")
            false

        }
    }

}