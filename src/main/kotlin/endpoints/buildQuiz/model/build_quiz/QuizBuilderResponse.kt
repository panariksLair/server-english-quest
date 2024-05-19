package com.github.panarik.endpoints.buildQuiz.model.build_quiz

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Response example:
 * {
 * "id":"8psshkwaw1rgm0cf3w7vg4hg20",
 * "model":"replicate-internal/llama-3-8b-instruct-int8-triton",
 * "version":"dp-a557b7387b4940df25b23f779dc534c4",
 * "input":{
 *  "length_penalty":1,
 *  "max_new_tokens":512,
 *  "presence_penalty":0,
 *  "prompt":"Make one example of Quiz for learning English.\\nDifficulty - A1.\\nQuiz should have these fields:\\n1. Summary. Two words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.",
 *  "prompt_template":"\u003c|begin_of_text|\u003e\u003c|start_header_id|\u003esystem\u003c|end_header_id|\u003e\\n\\nYou are a helpful assistant\u003c|eot_id|\u003e\u003c|start_header_id|\u003euser\u003c|end_header_id|\u003e\\n\\n{prompt}\u003c|eot_id|\u003e\u003c|start_header_id|\u003eassistant\u003c|end_header_id|\u003e\\n\\n",
 *  "stop_sequences":"\u003c|end_of_text|\u003e,\u003c|eot_id|\u003e",
 *  "system_prompt":"You are a English teacher",
 *  "temperature":0.6,
 *  "top_k":0,
 *  "top_p":0.9
 * },
 * "logs":"",
 * "error":null,
 * "status":"starting",
 * "created_at":"2024-04-27T10:17:43.392Z",
 * "urls":{
 *  "cancel":"https://api.replicate.com/v1/predictions/8psshkwaw1rgm0cf3w7vg4hg20/cancel",
 *  "get":"https://api.replicate.com/v1/predictions/8psshkwaw1rgm0cf3w7vg4hg20"
 *  }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class QuizBuilderResponse(val id: String)