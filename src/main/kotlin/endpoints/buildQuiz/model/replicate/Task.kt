package com.github.panarik.endpoints.buildQuiz.model.replicate

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.panarik.log
import com.github.panarik.request.model.replicate.buildQuiz.Input
import com.github.panarik.endpoints.buildQuiz.model.build_quiz.QuizBuilderRequest
import kotlin.random.Random

private const val TAG = "[Task]"

/**
 * Task for AI.
 */
data class Task(val difficult: String) {

    val topic: String = createTopic(difficult)
    val request: String = createRequest(difficult, topic)

    private fun createTopic(difficulty: String): String {
        val themes = when (difficulty) {
            "A1", "A2" -> {
                listOf(
                    "",
                    "Present Simple",
                    "Past Simple",
                    "Present Continuous",
                    "Past Continuous",
                    "Future Simple",
                    "Present Perfect Simple"
                )
            }

            "B1", "B2" -> {
                listOf(
                    "",

                    //Wiki:
                    "Words: nouns",
                    "Words: verbs",
                    "Words: adjectives",
                    "Words: adverbs",
                    "Words: pronoun",
                    "Words: prepositions",
                    "Words: conjunctions",
                    "Words: determiners",
                    "Words: exclamations",

                    // Sky-eng
                    "Tenses: Be going to (Predictions)",
                    "Tenses: Be Going to (Plans)",
                    "Tenses: Going to and Present Continuous",
                    "Tenses: Past Continuous",
                    "Tenses: Past Perfect",
                    "Tenses: Past Simple of to be",
                    "Tenses: Past Simple. Irregular Verbs. Extended list",
                    "Tenses: Present Simple and Present Continuous",
                    "Tenses: Present Continuous",
                    "Tenses: Present Perfect (ever/never)",
                    "Tenses: Present Perfect (regular irregular verbs)",
                    "Tenses: Present Perfect (verb '“'to be'”')",
                    "Tenses: Present Perfect (yet, just, already)",
                    "Tenses: Present Perfect and 'for' or 'since'",
                    "Tenses: Present Perfect and Past Simple",
                    "Tenses: Present Tense. To be. Negatives & Questions",
                    "Tenses: Present Tense. To be. Positive",
                    "Tenses: Will (for predictions)",
                    "Tenses: Will (for promises, offers and decisions)",
                    "Tenses: Would like to",
                    "Tenses: Tense review (present, past and future)",
                    "Tenses: Passive ('be' and Past Participle)",
                    "Tenses: Future Continuous",
                    "Tenses: Future Perfect",
                    "Tenses: Future Forms: 'Will'",
                    "Tenses: Future Forms: 'Be Going To'",
                    "Verbs: Expressing Movement",
                    "Verbs: 'So', 'Neither' and Auxiliaries",
                    "Verbs: Uses of the infinitive",
                    "Verbs: Verb + -ing after like",
                    "Adverb: Of Degree",
                    "Adverb: with '-ly'",
                    "Adverb: without '-ly'",
                    "Adverbs and Adjectives with the Same Form",
                    "Adjectives: with '-ly'",
                    "Adjectives: Comparative Constructions",
                    "Adjectives: Formation",
                    "Adjectives: Superlative Constructions",
                    "Adjectives: Superlative Degree",
                    "Sentences: Conjunctions",
                    "Sentences: Telling the Time",
                    "Sentences: There is/There are",
                    "Sentences: There was / There were",
                    "Sentence Structure: Clauses Relative",
                    "Sentence Structure: Clauses Relative",
                    "Sentence Structure: words order in Afirmative Sentences",
                    "Sentence Structure: words order in Interrogative Sentences",
                    "Sentence Structure: in Negative Sentences",
                    "Countable or Uncountable Nouns",
                    "Modals: Can / Can't",
                    "Modals: May or Might and Infinitive (Possibility)",
                    "Modals: Should or Shouldn't (Advice)",
                    "Modals: Deductions About the Past and Present",
                    "Pronouns: Object Pronouns",
                    "Pronouns: Possessive 's",
                    "Pronouns: Possessive Determiners",
                    "Pronouns: Possessive Pronouns",
                    "Pronouns: Something, Anything, Nothing, etc.",
                    "Prepositions of Time",
                    "Reported speech",
                    "Reporting Questions",
                    "Reported Commands",
                    "Conditionals: First Conditional",
                    "Conditionals: Second Conditional",
                    "Clauses if: First Conditional",
                    "Clauses if: Second Conditional",
                    "Auxiliary Verbs: Used to",
                    "Auxiliary Verbs: Didn't Use to",
                    "Modal Verbs: Have to,",
                    "Modal Verbs: Don't have to",
                    "Modal Verbs: Must",
                    "Modal Verbs: Mustn't",
                    "Multiword Verbs: Verb with No Object",
                    "Multiword Verbs: Verb with an Inseparable Particle and Object",
                    "Multiword Verbs: Verb with a Separable Particle and Object",
                    "Multiword Verbs: Verb with Two Inseparable Particles and Object",
                    "Noun: 'The'",
                    "Noun: 'A'",
                    "Noun: 'An'",
                    "Noun: 'A Lot of'",
                    "Noun: 'How Many' or 'How Much'",
                    "Noun: 'Few' or 'A Few'",
                    "Noun: 'Little' or 'A Little'",
                    "Noun: 'Much'",
                    "Noun: 'Many'",
                    "Countable Nouns: Regular Plural Form",
                    "Countable Nouns: Irregular Plural Form",
                    "Pronoun: Relative",
                    "Pronoun: Demonstrative",
                    "Spelling: Verb + -ing after like",
                    "Spelling: Present Simple and Present Continuous",
                    "Spelling: Adverbs",
                    "Spelling: Comparative adjectives and adverbs",
                    "Spelling: Superlative adjectives and adverbs",
                    "Gradable and Non-gradable",
                    "British English vs. American English",
                    "British English vs. American English: Word derivation and compounds",
                    "British English vs. American English: Vocabulary",
                    "British English vs. American English: Words and phrases with different meanings",
                    "British English vs. American English: Different terms in different dialects",
                    "British English vs. American English: Spelling",
                    "British English vs. American English: Idiosyncratic differences",
                    "British English vs. American English: Figures of speech",
                    "British English vs. American English: Social and cultural differences",
                    "British English vs. American English: General terms",
                    "British English vs. American English: Subject-verb agreement",
                    "British English vs. American English: Use of that and which in restrictive and non-restrictive relative clauses",
                    "Capital Letters and Apostrophes",
                    "Conditionals: Third and Mixed",
                    "Conditionals: Zero, First, and Second",
                    "Contrasting Ideas: ‘Although,’ ‘Despite,’ and Others",
                    "Intensifiers: ‘So’ and ‘Such’",
                )
            }

            else -> {
                listOf("")
            }
        }
        val theme = themes[Random.nextInt(0, themes.lastIndex)]
        log.info("$TAG Current quiz theme: $theme")
        return theme
    }

    private fun createRequest(difficulty: String, theme: String): String {
        val request = QuizBuilderRequest(
            Input(
                top_k = 0,
                top_p = 0.9,
                prompt = "Create one Quiz for learn English ${if (theme.isNotEmpty()) "about $theme " else ""}. \\nDifficulty - ${difficulty}.\\nQuiz should have these fields:\\n1. One field: **Summary**.\\n2. One field: **Question**. \\n3. One text block named **Wrong Answers** with three wrong (incorrect English grammar) answers. \\n4. One text block named **Right answer** with right answer.",
                temperature = 0.6,
                system_prompt = "You are a English teacher",
                length_penalty = 1,
                max_new_tokens = 512,
                stop_sequences = "<|end_of_text|>,<|eot_id|>",
                prompt_template = "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\\n\\nYou are a helpful assistant<|eot_id|><|start_header_id|>user<|end_header_id|>\\n\\n{prompt}<|eot_id|><|start_header_id|>assistant<|end_header_id|>\\n\\n",
                presence_penalty = 0
            )
        )
        return jacksonObjectMapper().writeValueAsString(request)
    }

}