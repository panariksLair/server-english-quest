package com.github.panarik.request.model

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Raw import:
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
data class Quiz(
    val id: String,
    val reviewed: Boolean,
    val difficult: String,
    val topic: String,
    val summary: String,
    val question: String,
    val wrong_answers: List<String>,
    val right_answer: String,
    val votes_positive: Int,
    val votes_negative: Int
)