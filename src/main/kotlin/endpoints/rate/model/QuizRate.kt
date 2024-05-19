package com.github.panarik.endpoints.rate.model

/**
 * @param rate can be only 1 or -1
 */
data class QuizRate(val quiz_id: String, val rate: Int) {

    fun isValid(): Boolean =
        quiz_id.isNotEmpty() && (rate == 1 || rate == -1)

}