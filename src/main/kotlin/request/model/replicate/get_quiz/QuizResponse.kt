package com.github.panarik.request.model.replicate.get_quiz

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Example:
 * {
 *  "id":"f54wm7yewhrgp0cf459a89mqy4",
 *  "model":"replicate-internal/llama-3-8b-instruct-int8-triton",
 *  "version":"dp-a557b7387b4940df25b23f779dc534c4",
 *  "input":{
 *   "length_penalty":1,
 *   "max_new_tokens":512,
 *   "presence_penalty":0,
 *   "prompt":"Make one example of Quiz for learning English.\\nDifficulty - A1.\\nQuiz should have these fields:\\n1. Summary. Two words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.",
 *   "prompt_template":"\u003c|begin_of_text|\u003e\u003c|start_header_id|\u003esystem\u003c|end_header_id|\u003e\\n\\nYou are a helpful assistant\u003c|eot_id|\u003e\u003c|start_header_id|\u003euser\u003c|end_header_id|\u003e\\n\\n{prompt}\u003c|eot_id|\u003e\u003c|start_header_id|\u003eassistant\u003c|end_header_id|\u003e\\n\\n",
 *   "stop_sequences":"\u003c|end_of_text|\u003e,\u003c|eot_id|\u003e",
 *   "system_prompt":"You are a English teacher",
 *   "temperature":0.6,
 *   "top_k":0,
 *   "top_p":0.9
 *   },
 *  "logs":"Random seed used: `48396`\nNote: Random seed will not impact output if greedy decoding is used.\nFormatted prompt: `\u003c|begin_of_text|\u003e\u003c|start_header_id|\u003esystem\u003c|end_header_id|\u003e\\n\\nYou are a helpful assistant\u003c|eot_id|\u003e\u003c|start_header_id|\u003euser\u003c|end_header_id|\u003e\\n\\nMake one example of Quiz for learning English.\\nDifficulty - A1.\\nQuiz should have these fields:\\n1. Summary. Two words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.\u003c|eot_id|\u003e\u003c|start_header_id|\u003eassistant\u003c|end_header_id|\u003e\\n\\n`Random seed used: `48396`\nNote: Random seed will not impact output if greedy decoding is used.\nFormatted prompt: `\u003c|begin_of_text|\u003e\u003c|start_header_id|\u003esystem\u003c|end_header_id|\u003e\\n\\nYou are a helpful assistant\u003c|eot_id|\u003e\u003c|start_header_id|\u003euser\u003c|end_header_id|\u003e\\n\\nMake one example of Quiz for learning English.\\nDifficulty - A1.\\nQuiz should have these fields:\\n1. Summary. Two words with current quiz name.\\n2. Question.\\n3. Three wrong answers. One word each.\\n4. One right answer. One word.\u003c|eot_id|\u003e\u003c|start_header_id|\u003eassistant\u003c|end_header_id|\u003e\\n\\n`",
 *  "output":["Here"," is"," an"," example"," of"," a"," Quiz"," for"," learning"," English",","," with"," a"," difficulty"," level"," of"," A","1",".\n\n","**","Summary",":**"," \"","What"," is"," your"," name","?\"\n\n","**","Question",":**"," What"," is"," your"," name","?\n\n","**","Wrong"," answers",":","**\n\n","1","."," House","\n","2","."," Car","\n","3","."," Book","\n\n","**","Right"," answer",":**"," Name","\n\n","Note",":"," The"," right"," answer"," is"," a"," single"," word",","," \"","Name","\"."],
 *  "error":null,
 *  "status":"succeeded",
 *  "created_at":"2024-04-27T20:50:26.148Z",
 *  "started_at":"2024-04-27T20:50:26.149108508Z",
 *  "completed_at":"2024-04-27T20:50:26.771131718Z",
 *  "urls":{
 *   "cancel":"https://api.replicate.com/v1/predictions/f54wm7yewhrgp0cf459a89mqy4/cancel",
 *   "get":"https://api.replicate.com/v1/predictions/f54wm7yewhrgp0cf459a89mqy4"
 *   },
 *  "metrics":{
 *   "input_token_count":58,
 *   "output_token_count":68,
 *   "time_to_first_token":0.024271143,
 *   "tokens_per_second":113.75954065717636
 *   }
 *  }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class QuizResponse(val output: List<String>, val quiz: String = output.joinToString(""))