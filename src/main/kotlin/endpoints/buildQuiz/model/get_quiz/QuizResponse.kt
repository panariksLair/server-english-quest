package com.github.panarik.endpoints.buildQuiz.model.get_quiz

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.panarik.log

private const val TAG = "[QuizResponse]"

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
data class QuizResponse(val id: String, val output: List<String>, val quiz: String = output.joinToString("")) {

    @JsonIgnore
    fun getSummary(): String {
        val regex = Regex("(?<=Summary)([\\S\\s]+)(?=(Question))")
        val block = regex.find(quiz)?.value ?: ""
        log.info("$TAG Summary regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
        try {
            val lines = block.split("\n")
            val rawSummary = lines.first { it.contains(Regex("[A-Za-z]")) }
            val prefix = Regex("^[*: \\d\\.]+").find(rawSummary)?.value ?: ""
            var summary = ""
            if (prefix == " ") {
                summary = rawSummary.drop(1)
            } else {
                summary = rawSummary.replace(prefix, "")
            }
            log.info("$TAG Summary: $summary")
            return summary
        } catch (e: Exception) {
            log.error("$TAG Error caught during parsing 'Summary' block. Original exception: ${e.message}. Original question block: $block")
            return ""
        }
    }

    @JsonIgnore
    fun getQuestion(): String {
        val regex = Regex("(?<=Question)([\\S\\s]+)(?=(Wrong [Aa]nswers))")
        val block = regex.find(quiz)?.value ?: ""
        log.info("$TAG Question regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
        try {
            val lines = block.split("\n")
            val questionLines = lines.filter { it.contains(Regex("[A-Za-z]")) }.toMutableList()
            val prefix = Regex("^[*: \\d\\.]+").find(questionLines[0])?.value ?: ""
            val questionFirstLine = if (prefix == " ") {
                questionLines[0].drop(1)
            } else {
                questionLines[0].replace(prefix, "")
            }
            questionLines[0] = questionFirstLine
            val question = questionLines.joinToString("\n")
            log.info("$TAG Question: $question")
            return question
        } catch (e: Exception) {
            log.error("$TAG Error caught during parsing 'Question' block. Original exception: ${e.message}. Original question block: $block")
            return ""
        }
    }

    @JsonIgnore
    fun getRightAnswer(): String {
        val regex = Regex("(?<=Right [Aa]nswer)[\\S\\s]+")
        val block = regex.find(quiz)?.value ?: ""
        log.info("$TAG RightAnswer regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
        try {
            val lines = block.split("\n")
            val rawAnswersLines = lines.filter { it.contains(Regex("[A-Za-z]")) }
            if (rawAnswersLines.isNotEmpty()) {
                val firstLine = rawAnswersLines[0]
                val prefix = Regex("(^[*: \\d\\.\\n]+)|([A-Da-d]\\) )").find(firstLine)?.value ?: ""
                val rightAnswer = if (prefix == " ") {
                    firstLine.drop(1)
                } else {
                    firstLine.replace(prefix, "")
                }
                log.info("$TAG Right Answer: $rightAnswer")
                return rightAnswer
            } else return ""
        } catch (e: Exception) {
            log.error("Error caught during parsing 'Right answer' block. Original exception: ${e.message}.")
            return ""
        }
    }

    @JsonIgnore
    fun getWrongAnswers(): List<String> {
        val regex = Regex("(?<=Wrong [Aa]nswers)([\\S\\s]+)(?=(Right [Aa]nswer))")
        val block = regex.find(quiz)?.value ?: ""
        log.info("$TAG WrongAnswers regex: ${regex.pattern}, input block: <Begin block>\n$block\n<End block>")
        try {
            var lines = block.split("\n")

            // In case if new lines is appeared like one String line: :**\n\n* Will have travelled\n\n* Am going to travel\n\n* Have been travelling\n\n**
            if (lines.size == 1) {
                lines = block.split("\\n")
            }
            val rawAnswers = lines.filter { it.contains(Regex("[A-Za-z]")) }
            val answers = rawAnswers.map { it.replace(Regex("(^[* \\d\\.]+)|([A-da-d]\\) )"), "") }
            log.info("$TAG Wrong Answers: ${answers.joinToString()}")
            return answers
        } catch (e: Exception) {
            log.error("$TAG Error caught during parsing 'Wrong answer' block. Original exception: ${e.message}. Original question block: $block")
            return emptyList()
        }

    }

}