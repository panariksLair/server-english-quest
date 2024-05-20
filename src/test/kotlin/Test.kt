import com.github.panarik.endpoints.buildQuiz.QuizVerifications
import com.github.panarik.model.Quiz
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Test {

    @Test
    fun duplicatedAnswers() {
        val quiz = Quiz(
            id = "1",
            reviewed = false,
            difficult = "B1",
            topic = "Test",
            summary = "Summary",
            question = "Question",
            wrong_answers = listOf("had quickly done, did quickly, quickly did"),
            right_answer = "had quickly done",
            votes_positive = 0,
            votes_negative = 0,
        )
        val actual = QuizVerifications(quiz).isValid()
        Assertions.assertEquals(actual, false)
    }

}