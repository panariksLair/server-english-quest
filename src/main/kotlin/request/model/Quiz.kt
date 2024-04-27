package com.github.panarik.request.model

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
    val summary: String,
    val question: String,
    val wrong_answers: List<String>,
    val right_answer: String
)