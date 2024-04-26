package com.github.panarik.service

import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class QuizBuilder {

    private var response = ""

    fun build() {
        getQuiz()
        while (response.isEmpty()) {

        }
    }

    private fun getQuiz() {
        val client = OkHttpClient()
        val body = "What is your name?"
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer r8_TF9Xx4keKqZHPRfFUYXEZ344nlyg5Iu1QiT3x")
            .put(body.toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }
        })

    }

}