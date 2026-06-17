package com.example.quiz

import com.example.data.QuizQuestion
import kotlin.random.Random

object QuizEngine {

    // XP constants for correct answers by difficulty
    fun getXpReward(difficulty: String): Int {
        return when (difficulty.lowercase()) {
            "easy" -> 15
            "medium" -> 30
            "hard" -> 60
            "asian" -> 120
            else -> 15
        }
    }

    // Time limit in seconds for questions by difficulty (0 means no limit)
    fun getTimeLimitPerQuestion(difficulty: String): Int {
        return when (difficulty.lowercase()) {
            "easy" -> 0
            "medium" -> 30
            "hard" -> 15
            "asian" -> 8 // Extreme 8-second timer for Asian Mode!
            else -> 0
        }
    }

    // Savage insults when user fails a question, categorized by difficulty
    fun getRandomInsult(difficulty: String): String {
        val insults = when (difficulty.lowercase()) {
            "easy" -> listOf(
                "Oh dear. My cat stepped on the screen and would have picked better.",
                "Are you distracted or did you actually think that was correct?",
                "That was a slow soft-ball and you missed it completely.",
                "Maybe reading is not your strongest suit. Let's practice!",
                "Even standard tap-water has more cognitive activity than that answer.",
                "Did you close your eyes and pray before tapping?",
                "Wow, setting the bar low, and yet you brought a shovel."
            )
            "medium" -> listOf(
                "Incorrect! Somewhere, a tree is crying because it wasted oxygen on you.",
                "Don't quit your day job... wait, do you even have one?",
                "Your brain is on permanent power-saving mode, isn't it?",
                "That was disappointing, but not entirely unexpected.",
                "If confusion was an art form, you would be Leonardo da Vinci.",
                "Your intellect is in quarantine. Please let it out.",
                "Have you tried turning your brain off and on again?"
            )
            "hard" -> listOf(
                "Ouch! You might want to apply some ice to that burn.",
                "You confidently picked the worst possible answer. Impressive, honestly!",
                "Error 404: Knowledge not found in this cerebrum.",
                "Are you guessing or just clicking randomly with blind hope?",
                "If brains were dynamite, you wouldn't have enough to blow your nose.",
                "I've seen dial-up modems retrieve information faster than you.",
                "You're proof that evolution can sometimes take a coffee break."
            )
            "asian" -> listOf(
                "Aiyah! Only got B+? Neighbor's son got A+++ and started 3 companies at age 4!",
                "Failure! Go wash the rice! You bring shame to ancestors!",
                "My calculator has more processing power and emotional intelligence than you.",
                "If you spent half the time studying as you do failing this quiz, you would be a neurosurgeon!",
                "Your cousin became a doctor at age 12, and you can't even solve this MCQ? High blood pressure rising!",
                "Stupidity level: Off the charts. No boba tea or internet for you for 3 years!",
                "Go tell your ancestors why you picked that option. They are crying in heaven!",
                "Only got B+? In this house, B+ stands for 'Banished'!",
                "When I was your age, I created the electricity you are using to fail this question!",
                "You select this wrong answer and expect me to pay for college? Go do dishes now!"
            )
            else -> listOf(
                "That's incorrect. Try to study harder next time."
            )
        }
        return insults[Random.nextInt(insults.size)]
    }

    // Motivational praises when correct
    fun getRandomMotivation(difficulty: String): String {
        val praises = when (difficulty.lowercase()) {
            "asian" -> listOf(
                "Ancestors are smiling! You achieved honor! For now...",
                "Not bad! Still not doctor yet, but you got 1 question correct. Continue!",
                "A+++! Neighbor's son is sweating! He only got 99.9%, you got 100%!",
                "Ah, okay! Finally you put down phone and study! Good boy/girl!",
                "Very good! Your ancestors stop rolling in their graves for 10 seconds!"
            )
            else -> listOf(
                "Outstanding! You possess an actual working brain!",
                "Correct! Your IQ just leaped by 50 points!",
                "Genius move! Promptly upload your brain to the cloud!",
                "Superb accuracy! Absolutely legendary!",
                "Boom! Mind like a sharp laser beam!",
                "Phenomenal correctness. No roasts for you this time!",
                "Magnificent! You are cruising past the local competition!"
            )
        }
        return praises[Random.nextInt(praises.size)]
    }

    // Parse quiz questions from copy-pasted text or read TXT files
    fun parseCustomQuiz(rawText: String): CustomQuizParseResult {
        try {
            val lines = rawText.lines().map { it.trim() }
            var currentSubject = "Custom Import"
            var currentDifficulty = "Medium"
            val questions = mutableListOf<QuizQuestion>()

            var tempQuestionText = ""
            var tempA = ""
            var tempB = ""
            var tempC = ""
            var tempD = ""
            var tempAnswer = ""

            for (line in lines) {
                if (line.isEmpty()) continue

                // Check overall configurations
                if (line.startsWith("Subject:", ignoreCase = true)) {
                    currentSubject = line.substringAfter(":").trim()
                    continue
                }
                if (line.startsWith("Difficulty:", ignoreCase = true)) {
                    val diff = line.substringAfter(":").trim()
                    if (listOf("easy", "medium", "hard", "asian").contains(diff.lowercase())) {
                        currentDifficulty = diff.replaceFirstChar { it.uppercase() }
                    }
                    continue
                }

                // Question or options parsing
                when {
                    line.startsWith("Q:", ignoreCase = true) -> {
                        // If we already have a parsed question pending, save it
                        if (tempQuestionText.isNotEmpty() && tempA.isNotEmpty() && tempB.isNotEmpty() && tempAnswer.isNotEmpty()) {
                            questions.add(
                                QuizQuestion(
                                    subject = currentSubject,
                                    questionText = tempQuestionText,
                                    optionA = tempA,
                                    optionB = tempB,
                                    optionC = if (tempC.isEmpty()) "N/A" else tempC,
                                    optionD = if (tempD.isEmpty()) "N/A" else tempD,
                                    correctAnswer = tempAnswer.uppercase(),
                                    difficulty = currentDifficulty,
                                    isCustom = true
                                )
                            )
                            // reset
                            tempQuestionText = ""
                            tempA = ""
                            tempB = ""
                            tempC = ""
                            tempD = ""
                            tempAnswer = ""
                        }
                        tempQuestionText = line.substringAfter(":").trim()
                    }
                    line.startsWith("A:", ignoreCase = true) -> {
                        tempA = line.substringAfter(":").trim()
                    }
                    line.startsWith("B:", ignoreCase = true) -> {
                        tempB = line.substringAfter(":").trim()
                    }
                    line.startsWith("C:", ignoreCase = true) -> {
                        tempC = line.substringAfter(":").trim()
                    }
                    line.startsWith("D:", ignoreCase = true) -> {
                        tempD = line.substringAfter(":").trim()
                    }
                    line.startsWith("Correct:", ignoreCase = true) || line.startsWith("Answer:", ignoreCase = true) -> {
                        tempAnswer = line.substringAfter(":").trim().uppercase()
                        if (tempAnswer.length > 1) {
                            // Extract just the letter A, B, C, D
                            tempAnswer = tempAnswer.take(1)
                        }
                    }
                }
            }

            // check if there is an outstanding last question
            if (tempQuestionText.isNotEmpty() && tempA.isNotEmpty() && tempB.isNotEmpty() && tempAnswer.isNotEmpty()) {
                questions.add(
                    QuizQuestion(
                        subject = currentSubject,
                        questionText = tempQuestionText,
                        optionA = tempA,
                        optionB = tempB,
                        optionC = if (tempC.isEmpty()) "N/A" else tempC,
                        optionD = if (tempD.isEmpty()) "N/A" else tempD,
                        correctAnswer = tempAnswer.uppercase(),
                        difficulty = currentDifficulty,
                        isCustom = true
                    )
                )
            }

            if (questions.isEmpty()) {
                return CustomQuizParseResult.Error("No valid MCQs were parsed. Make sure they follow the Q: / A: / B: / Correct: format.")
            }

            return CustomQuizParseResult.Success(currentSubject, currentDifficulty, questions)
        } catch (e: Exception) {
            return CustomQuizParseResult.Error("Fail to parse: ${e.localizedMessage}")
        }
    }
}

sealed class CustomQuizParseResult {
    data class Success(val subject: String, val difficulty: String, val questions: List<QuizQuestion>) : CustomQuizParseResult()
    data class Error(val message: String) : CustomQuizParseResult()
}
