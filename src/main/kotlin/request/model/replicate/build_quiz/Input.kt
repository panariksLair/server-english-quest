package com.github.panarik.request.model.replicate.buildQuiz

/**
 * Example:
 * "top_k": 0,
 * "top_p": 0.9,
 * "prompt": "Make one example of Quiz for learning English.\\nDifficulty - A1.\\nQuiz should have these fields:\\n1. Summary. Two words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.",
 * "temperature": 0.6,
 * "system_prompt": "You are a English teacher",
 * "length_penalty": 1,
 * "max_new_tokens": 512,
 * "stop_sequences": "<|end_of_text|>,<|eot_id|>",
 * "prompt_template": "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\\n\\nYou are a helpful assistant<|eot_id|><|start_header_id|>user<|end_header_id|>\\n\\n{prompt}<|eot_id|><|start_header_id|>assistant<|end_header_id|>\\n\\n",
 * "presence_penalty": 0
 */
data class Input(
    val top_k:Int,
    val top_p:Double,
    val prompt:String,
    val temperature:Double,
    val system_prompt:String,
    val length_penalty:Int,
    val max_new_tokens:Int,
    val stop_sequences:String,
    val prompt_template:String,
    val presence_penalty:Int)