package com.github.panarik.endpoints.buildQuiz.model.build_quiz

import com.github.panarik.request.model.replicate.buildQuiz.Input

/**
 * Example:
 *   -H "Authorization: Bearer $REPLICATE_API_TOKEN" \
 *   -H "Content-Type: application/json" \
 *   -d $'{
 *     "input": {
 *       "top_k": 0,
 *       "top_p": 0.9,
 *       "prompt": "Make one example of Quiz for learning English.\\nDifficulty - A1.\\nQuiz should have these fields:\\n1. Summary. Two words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.",
 *       "temperature": 0.6,
 *       "system_prompt": "You are a English teacher",
 *       "length_penalty": 1,
 *       "max_new_tokens": 512,
 *       "stop_sequences": "<|end_of_text|>,<|eot_id|>",
 *       "prompt_template": "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\\n\\nYou are a helpful assistant<|eot_id|><|start_header_id|>user<|end_header_id|>\\n\\n{prompt}<|eot_id|><|start_header_id|>assistant<|end_header_id|>\\n\\n",
 *       "presence_penalty": 0
 *     }
 *   }' \
 *   https://api.replicate.com/v1/models/meta/meta-llama-3-8b-instruct/predictions
 */
data class QuizBuilderRequest(val input: Input)