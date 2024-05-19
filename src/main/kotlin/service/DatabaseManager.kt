package com.github.panarik.service

import com.github.panarik.endpoints.rate.model.QuizRate
import com.github.panarik.log
import com.github.panarik.model.Quiz
import org.postgresql.ds.PGSimpleDataSource
import java.sql.Connection
import java.sql.Statement

object DatabaseManager {

    private const val TAG = "[DatabaseManager]"
    private const val CONNECTION_URL =
        "jdbc:postgresql://assist-sql-9883.7tc.aws-eu-central-1.cockroachlabs.cloud:26257/english_quiz?sslmode=verify-full&password=RIy-K1TrBxEUG00cQ5PGwg&user=panariks"
    private lateinit var db: PGSimpleDataSource
    private lateinit var connection: Connection
    private lateinit var statement: Statement

    fun connect() {
        db = PGSimpleDataSource()
        db.setUrl(CONNECTION_URL)
        db.sslRootCert = "postgresql.crt"
        if (db.connection.isValid(10)) {
            log.info("$TAG Database connected.")
            connection = db.connection
            statement = db.connection.createStatement()

        } else {
            log.error("$TAG, Can't connect to database!")
        }
    }

    fun safe(quiz: Quiz) {
        try {
            statement.executeUpdate(getQuery(quiz))
            log.info("$TAG Quiz id ${quiz.id} is sent to database.")
        } catch (e: Exception) {
            log.error("Exception caught during inserting into database. Message:${e.message}")
        }
    }

    fun rate(quiz: QuizRate): Boolean {
        val rateColumn = when (quiz.rate) {
            1 -> "votes_positive"
            -1 -> "votes_negative"
            else -> {
                log.error("$TAG Wrong rate caught from client. Sending error message.")
                return false
            }
        }
        try {
            val selectQuery = "SELECT $rateColumn FROM quizes WHERE id=?"
            val preparedSelect = connection.prepareStatement(selectQuery)
            preparedSelect.setString(1, quiz.quiz_id)
            val existingRateResult = preparedSelect.executeQuery()
            existingRateResult.next()
            val existingRate = existingRateResult.getString(rateColumn).toInt()
            val updateQuery = "UPDATE quizes SET $rateColumn = ? WHERE id = ?"
            val preparedUpdate = connection.prepareStatement(updateQuery)
            preparedUpdate.setInt(1, existingRate + 1)
            preparedUpdate.setString(2, quiz.quiz_id)
            preparedUpdate.execute()
            return true
        } catch (e: Exception) {
            log.error("$TAG Error caught during database operations. Message: ${e.message}")
            return false
        }
    }

    private fun getQuery(quiz: Quiz): String {
        try {
            val summary = quiz.summary.replace("'", "''")
            val question = quiz.question.replace("'", "''")
            val rightAnswer = quiz.right_answer.replace("'", "''")
            val wrongAnswers = quiz.wrong_answers.map { it.replace("'", "''") }
            val query =
                "insert into quizes (id, reviewed, difficult, topic, votes_positive, votes_negative, summary, question, right_answer, wrong_answer_1, wrong_answer_2, wrong_answer_3) VALUES ('${quiz.id}', ${quiz.reviewed}, '${quiz.difficult}', '${quiz.topic}', ${quiz.votes_positive}, ${quiz.votes_negative}, '${summary}', '${question}', '${rightAnswer}', '${wrongAnswers[0]}', '${wrongAnswers[1]}', '${wrongAnswers[2]}');"
            return query
        } catch (e: Exception) {
            log.error("$TAG Error caught during parse query. Message: ${e.message}")
            return ""
        }
    }

}