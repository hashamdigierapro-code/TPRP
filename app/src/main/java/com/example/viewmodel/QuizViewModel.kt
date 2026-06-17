package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiClient
import com.example.data.*
import com.example.quiz.CustomQuizParseResult
import com.example.quiz.QuizEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class Screen {
    Home, QuizPlay, Leaderboard, AIImport, GarbageCollection, Statistics
}

data class QuizUiState(
    val currentScreen: Screen = Screen.Home,
    val selectedSubject: String = "General Knowledge",
    val selectedDifficulty: String = "Easy",
    
    // Quiz play variables
    val quizActiveQuestions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val isAnswerSelected: Boolean = false,
    val lastSelectedOption: String = "", // "A", "B", "C", "D"
    val score: Int = 0,
    val timerRemainingSeconds: Int = 0, // for Medium/Hard/Asian timers
    
    // Insult / Motivation overlay
    val showFeedbackOverlay: Boolean = false,
    val isCorrectAnswer: Boolean = false,
    val feedbackMessage: String = "",
    
    // User progress
    val userStats: UserStats = UserStats(),
    val leaderboardPlayers: List<LeaderboardPlayer> = emptyList(),
    val logsList: List<HistoryLog> = emptyList(),
    val uniqueSubjects: List<String> = listOf("General Knowledge", "Science", "Math", "Computer Science"),
    val tooltipMessage: String? = "Welcome! Tap a subject, pick a difficulty, and click 'START QUIZ' to challenge yourself!",
    val lastMockResultText: String? = null,
    
    // Garbage Collector / Trash Bin Lists
    val trashedQuestions: List<QuizQuestion> = emptyList(),
    val trashedLogs: List<HistoryLog> = emptyList(),
    
    // AI Creator Dashboard States
    val aiTopic: String = "",
    val aiDifficultySelected: String = "Medium",
    val aiGenerating: Boolean = false,
    val aiGenerationProgress: Int = 0,
    val aiError: String? = null,
    val aiSuccess: String? = null,
    val manualAiUrlLink: String = "https://api.openai.com/v1", // Editable custom AI endpoints
    
    // Raw TXT Manual Import States
    val rawTxtToImport: String = "",
    val txtImportError: String? = null,
    val txtImportSuccess: String? = null,
    
    // Application settings and visual styling preferences
    val darkTheme: Boolean = true, // True by default as user requested dark style Arabic design
    val selectedVisualStyle: String = "Arabic Lantern", // "Arabic Lantern", "Elegant Dark", "Bento Grid", "Frosted Glass", "Vibrant Palette", "Clean Minimalist"
    val selectedCountry: String = "Malaysia",

    // Profile variables
    val allProfiles: List<UserProfile> = emptyList(),
    val activeProfile: UserProfile? = null
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    private val prefs = application.getSharedPreferences("QuizAppPrefs", android.content.Context.MODE_PRIVATE)

    private fun saveState(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    init {
        val db = QuizDatabase.getDatabase(application)
        repository = QuizRepository(db)

        // Read and restore previously saved state for complete app state preservation across restarts
        val restoredScreenStr = prefs.getString("current_screen", Screen.Home.name)
        val restoredScreen = try { Screen.valueOf(restoredScreenStr ?: "Home") } catch(e: Exception) { Screen.Home }
        val restoredSubject = prefs.getString("selected_subject", "General Knowledge") ?: "General Knowledge"
        val restoredDifficulty = prefs.getString("selected_difficulty", "Easy") ?: "Easy"
        val restoredCountry = prefs.getString("selected_country", "Malaysia") ?: "Malaysia"

        _uiState.update {
            it.copy(
                currentScreen = restoredScreen,
                selectedSubject = restoredSubject,
                selectedDifficulty = restoredDifficulty,
                selectedCountry = restoredCountry
            )
        }

        // Initialize database questions & user stats
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            observeDatabaseFlows()
        }
    }

    private fun observeDatabaseFlows() {
        viewModelScope.launch {
            repository.userStats.collect { stats ->
                if (stats != null) {
                    _uiState.update { it.copy(userStats = stats) }
                }
            }
        }

        viewModelScope.launch {
            repository.allProfiles.collect { profiles ->
                _uiState.update { it.copy(allProfiles = profiles) }
            }
        }

        viewModelScope.launch {
            repository.activeProfile.collect { active ->
                if (active != null) {
                    _uiState.update { 
                        it.copy(
                            activeProfile = active,
                            selectedVisualStyle = active.selectedStyle,
                            darkTheme = active.isDarkTheme,
                            selectedCountry = active.primaryCountry
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.leaderboard.collect { players ->
                _uiState.update { it.copy(leaderboardPlayers = players) }
            }
        }

        viewModelScope.launch {
            repository.historyLogs.collect { logs ->
                _uiState.update { it.copy(logsList = logs) }
            }
        }

        viewModelScope.launch {
            repository.allUniqueSubjects.collect { subjects ->
                if (subjects.isNotEmpty()) {
                    _uiState.update { it.copy(uniqueSubjects = subjects) }
                }
            }
        }

        viewModelScope.launch {
            repository.deletedQuestions.collect { dq ->
                _uiState.update { it.copy(trashedQuestions = dq) }
            }
        }

        viewModelScope.launch {
            repository.deletedHistoryLogs.collect { dl ->
                _uiState.update { it.copy(trashedLogs = dl) }
            }
        }
    }

    // --- Screen Route Transitions ---
    fun navigateTo(screen: Screen) {
        cancelTimer()
        _uiState.update { it.copy(currentScreen = screen, tooltipMessage = null) }
        saveState("current_screen", screen.name)
        
        // Hide overlay on transition
        _uiState.update { it.copy(showFeedbackOverlay = false) }

        // Set contextual descriptive tooltips for new users
        val tooltip = when (screen) {
            Screen.Home -> "Toggle Dark theme, select subjects, or choose 'Asian Mode' for extreme academic parental roasts!"
            Screen.AIImport -> "Generate custom MCQs automatically using Gemini AI on any study topic, or paste standard text files."
            Screen.Leaderboard -> "Compete in live-action real-time leaderboard ranks. Ranks update with score gains!"
            Screen.GarbageCollection -> "Review, restore or permanently delete soft-removed quizzes or historical logs to free space."
            Screen.Statistics -> "Track your historical scores, success stats, level progress gauges, and detailed gameplay metrics."
            else -> null
        }
        _uiState.update { it.copy(tooltipMessage = tooltip) }
    }

    fun toggleTheme() {
        val current = _uiState.value.darkTheme
        _uiState.update { it.copy(darkTheme = !current) }
        viewModelScope.launch {
            val active = repository.userProfileDao.getActiveProfileDirect()
            if (active != null) {
                repository.updateProfile(active.copy(isDarkTheme = !current))
            }
        }
    }

    fun selectVisualStyle(style: String) {
        _uiState.update { it.copy(selectedVisualStyle = style) }
        viewModelScope.launch {
            val active = repository.userProfileDao.getActiveProfileDirect()
            if (active != null) {
                repository.updateProfile(active.copy(selectedStyle = style))
            }
        }
    }

    fun selectCountry(country: String) {
        val cleanCountry = country.replace(Regex("[^a-zA-Z ]"), "").trim()
        _uiState.update { it.copy(selectedCountry = cleanCountry, selectedSubject = "Govt Jobs Prep ($cleanCountry)") }
        saveState("selected_country", cleanCountry)
        saveState("selected_subject", "Govt Jobs Prep ($cleanCountry)")
        viewModelScope.launch {
            val active = repository.userProfileDao.getActiveProfileDirect()
            if (active != null) {
                repository.updateProfile(active.copy(primaryCountry = cleanCountry))
            }
        }
    }

    // --- Profile Operations ---
    fun createProfile(name: String, avatar: String, country: String) {
        viewModelScope.launch {
            repository.createProfile(name, avatar, country, _uiState.value.selectedVisualStyle, _uiState.value.darkTheme)
        }
    }

    fun selectProfile(id: Int) {
        viewModelScope.launch {
            repository.selectProfile(id)
        }
    }

    fun deleteProfile(id: Int) {
        viewModelScope.launch {
            repository.deleteProfile(id)
        }
    }

    fun resumeActiveQuiz() {
        val activeF = _uiState.value.activeProfile ?: return
        val questionsJson = activeF.activeQuizQuestionsJson
        if (questionsJson.isNullOrEmpty()) return

        viewModelScope.launch {
            val ids = questionsJson.split(",").mapNotNull { it.toIntOrNull() }
            if (ids.isNotEmpty()) {
                val allQ = repository.quizDao.getAllActiveQuestions().firstOrNull() ?: emptyList()
                val questions = ids.mapNotNull { id -> allQ.find { it.id == id } }
                if (questions.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            quizActiveQuestions = questions,
                            currentQuestionIndex = activeF.activeQuizCurrentIndex,
                            score = activeF.activeQuizScore,
                            selectedSubject = activeF.activeQuizSubject ?: it.selectedSubject,
                            selectedDifficulty = activeF.activeQuizDifficulty ?: it.selectedDifficulty,
                            isAnswerSelected = false,
                            lastSelectedOption = "",
                            currentScreen = Screen.QuizPlay,
                            showFeedbackOverlay = false
                        )
                    }
                    startQuestionCountdown(questions[activeF.activeQuizCurrentIndex].difficulty)
                }
            }
        }
    }

    fun abandonQuizSession() {
        viewModelScope.launch {
            val activeP = repository.userProfileDao.getActiveProfileDirect() ?: return@launch
            val updated = activeP.copy(
                activeQuizQuestionsJson = null,
                activeQuizSubject = null,
                activeQuizDifficulty = null,
                activeQuizCurrentIndex = 0,
                activeQuizScore = 0
            )
            repository.updateProfile(updated)
        }
    }

    fun selectSubject(subject: String) {
        _uiState.update { it.copy(selectedSubject = subject) }
        saveState("selected_subject", subject)
    }

    fun selectDifficulty(difficulty: String) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
        saveState("selected_difficulty", difficulty)
    }

    fun dismissTooltip() {
        _uiState.update { it.copy(tooltipMessage = null) }
    }

    // --- Active Quiz Taking Loop ---
    fun startQuizSession() {
        val subject = _uiState.value.selectedSubject
        val difficulty = _uiState.value.selectedDifficulty

        viewModelScope.launch {
            val questions = repository.quizDao.getQuestionsForQuiz(subject, difficulty)
            if (questions.isEmpty()) {
                _uiState.update {
                    it.copy(
                        tooltipMessage = "Oh! No MCQs found for $subject ($difficulty). Try Generating questions using Gemini in the Dynamic AI panel!"
                    )
                }
                return@launch
            }

            // Shuffle questions to ensure dynamic gameplay
            val shuffled = questions.shuffled().take(10)
            val idsStr = shuffled.map { it.id }.joinToString(",")

            val activeProfile = repository.userProfileDao.getActiveProfileDirect()
            if (activeProfile != null) {
                repository.updateProfile(activeProfile.copy(
                    activeQuizQuestionsJson = idsStr,
                    activeQuizSubject = subject,
                    activeQuizDifficulty = difficulty,
                    activeQuizCurrentIndex = 0,
                    activeQuizScore = 0,
                    totalTries = activeProfile.totalTries + 1
                ))
            }

            _uiState.update {
                it.copy(
                    quizActiveQuestions = shuffled,
                    currentQuestionIndex = 0,
                    isAnswerSelected = false,
                    lastSelectedOption = "",
                    score = 0,
                    currentScreen = Screen.QuizPlay,
                    showFeedbackOverlay = false
                )
            }

            startQuestionCountdown(shuffled[0].difficulty)
        }
    }

    private fun startQuestionCountdown(difficulty: String) {
        cancelTimer()
        val limit = QuizEngine.getTimeLimitPerQuestion(difficulty)
        if (limit > 0) {
            _uiState.update { it.copy(timerRemainingSeconds = limit) }
            countdownJob = viewModelScope.launch {
                var remaining = limit
                while (remaining > 0) {
                    delay(1000)
                    remaining--
                    _uiState.update { it.copy(timerRemainingSeconds = remaining) }
                }
                // Time's up! Force incorrect answer (Savage Roast triggered)
                triggerTimeoutInsult()
            }
        } else {
            _uiState.update { it.copy(timerRemainingSeconds = 0) }
        }
    }

    private fun cancelTimer() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun triggerTimeoutInsult() {
        cancelTimer()
        val current = _uiState.value.quizActiveQuestions.getOrNull(_uiState.value.currentQuestionIndex) ?: return
        val insult = QuizEngine.getRandomInsult(current.difficulty) + " (YOU RAN OUT OF TIME!)"

        viewModelScope.launch {
            val idx = _uiState.value.currentQuestionIndex
            val activeProfile = repository.userProfileDao.getActiveProfileDirect()
            if (activeProfile != null) {
                repository.updateProfile(activeProfile.copy(
                    activeQuizCurrentIndex = idx,
                    activeQuizScore = _uiState.value.score
                ))
            }
        }

        _uiState.update {
            it.copy(
                isAnswerSelected = true,
                lastSelectedOption = "TIMEOUT",
                isCorrectAnswer = false,
                feedbackMessage = insult,
                showFeedbackOverlay = true
            )
        }
    }

    fun submitAnswerSelection(option: String) {
        if (_uiState.value.isAnswerSelected) return
        cancelTimer()

        val currentIndex = _uiState.value.currentQuestionIndex
        val questions = _uiState.value.quizActiveQuestions
        val currentQuestion = questions.getOrNull(currentIndex) ?: return

        val isCorrect = option == currentQuestion.correctAnswer
        val message = if (isCorrect) {
            QuizEngine.getRandomMotivation(currentQuestion.difficulty)
        } else {
            QuizEngine.getRandomInsult(currentQuestion.difficulty)
        }

        val newScore = if (isCorrect) _uiState.value.score + 1 else _uiState.value.score

        viewModelScope.launch {
            val activeProfile = repository.userProfileDao.getActiveProfileDirect()
            if (activeProfile != null) {
                repository.updateProfile(activeProfile.copy(
                    activeQuizCurrentIndex = currentIndex,
                    activeQuizScore = newScore
                ))
            }
        }

        _uiState.update {
            it.copy(
                isAnswerSelected = true,
                lastSelectedOption = option,
                isCorrectAnswer = isCorrect,
                feedbackMessage = message,
                score = newScore,
                showFeedbackOverlay = true
            )
        }
    }

    fun proceedToNextQuestion() {
        _uiState.update { it.copy(showFeedbackOverlay = false) }

        val nextIndex = _uiState.value.currentQuestionIndex + 1
        val questions = _uiState.value.quizActiveQuestions

        if (nextIndex < questions.size) {
            val updatedScore = _uiState.value.score
            viewModelScope.launch {
                val activeProfile = repository.userProfileDao.getActiveProfileDirect()
                if (activeProfile != null) {
                    repository.updateProfile(activeProfile.copy(
                        activeQuizCurrentIndex = nextIndex,
                        activeQuizScore = updatedScore
                    ))
                }
            }
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    isAnswerSelected = false,
                    lastSelectedOption = ""
                )
            }
            startQuestionCountdown(questions[nextIndex].difficulty)
        } else {
            // End of quiz session! Award XP, update profile, and log
            finalizeQuizCompletion()
        }
    }

    private fun finalizeQuizCompletion() {
        val quizSize = _uiState.value.quizActiveQuestions.size
        val correctCount = _uiState.value.score
        val difficulty = _uiState.value.selectedDifficulty
        val subject = _uiState.value.selectedSubject

        // Calculate earned XP based on difficulty multipliers
        val multiplier = QuizEngine.getXpReward(difficulty)
        val xpWon = correctCount * multiplier
        
        // Mockup Public Service Exam Validation
        val mockResult = if (subject.startsWith("Govt Jobs Prep")) {
            val percentage = (correctCount.toFloat() / quizSize.toFloat()) * 100f
            val requiredPercentage = when {
                subject.contains("Pakistan") -> 50f
                subject.contains("India") -> 33f // UPSC prelims typically have varied cutoffs, setting 33% CSAT
                subject.contains("Saudi") -> 60f // Qiyas standard
                subject.contains("UAE") || subject.contains("Malaysia") || subject.contains("Egypt") -> 50f // General public service average
                else -> 50f
            }
            if (percentage >= requiredPercentage) {
                "PASSED ✅ (${percentage.toInt()}%) - You met the Public Service standard ($requiredPercentage%) for $subject!"
            } else {
                "FAILED ❌ (${percentage.toInt()}%) - You missed the Public Service standard of $requiredPercentage% for $subject."
            }
        } else null

        viewModelScope.launch {
            repository.saveQuizResult(
                subject = subject,
                difficulty = difficulty,
                correctCount = correctCount,
                totalQuestions = quizSize,
                xpGained = xpWon
            )

            _uiState.update {
                it.copy(
                    currentScreen = Screen.Statistics,
                    lastMockResultText = mockResult,
                    tooltipMessage = "Quiz completed! You scored $correctCount/$quizSize and earned $xpWon XP toward your rank!"
                )
            }
        }
    }

    // --- AI Generator controls (Gemini) ---
    fun updateAiTopic(topic: String) {
        _uiState.update { it.copy(aiTopic = topic, aiError = null, aiSuccess = null) }
    }

    fun updateAiDifficulty(diff: String) {
        _uiState.update { it.copy(aiDifficultySelected = diff) }
    }

    fun updateManualAiUrl(url: String) {
        _uiState.update { it.copy(manualAiUrlLink = url) }
    }

    fun generateAiMcqs(apiKeyFromUser: String? = null) {
        val topic = _uiState.value.aiTopic.trim()
        val difficulty = _uiState.value.aiDifficultySelected
        val manualUrl = _uiState.value.manualAiUrlLink.trim()

        if (topic.isBlank()) {
            _uiState.update { it.copy(aiError = "Please specify a topic or subject first!") }
            return
        }

        _uiState.update { it.copy(aiGenerating = true, aiGenerationProgress = 0, aiError = null, aiSuccess = null) }

        viewModelScope.launch {
            // Fake progress updater loop
            val progressJob = launch {
                for (i in 1..90) {
                    delay(30)
                    _uiState.update { it.copy(aiGenerationProgress = i) }
                }
            }
            try {
                val questions = GeminiApiClient.generateMcqs(topic, difficulty, count = 5, customKey = apiKeyFromUser, manualUrl = manualUrl.ifBlank { null })
                progressJob.cancel()
                _uiState.update { it.copy(aiGenerationProgress = 100) }
                delay(200) // slight visual pause before success message
                if (questions.isNotEmpty()) {
                    repository.quizDao.insertQuestions(questions)
                    _uiState.update {
                        it.copy(
                            aiGenerating = false,
                            aiSuccess = "Success! Generated 5 tricky MCQs on '$topic' ($difficulty). These are permanently added to your study database!",
                            aiTopic = ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            aiGenerating = false,
                            aiError = "No MCQs were generated. Try refining your topic name."
                        )
                    }
                }
            } catch (e: Exception) {
                progressJob.cancel()
                Log.e("QuizViewModel", "AI Generation Error", e)
                _uiState.update {
                    it.copy(
                        aiGenerating = false,
                        aiGenerationProgress = 0,
                        aiError = "Generation failed: ${e.localizedMessage}. Verify your Gemini API key in the secrets panel."
                    )
                }
            }
        }
    }

    // --- Manual File Import (TXT Parser) ---
    fun updateRawTxt(text: String) {
        _uiState.update { it.copy(rawTxtToImport = text, txtImportError = null, txtImportSuccess = null) }
    }

    fun importCustomTxtQuiz() {
        val text = _uiState.value.rawTxtToImport
        if (text.isBlank()) {
            _uiState.update { it.copy(txtImportError = "Import content cannot be blank!") }
            return
        }

        viewModelScope.launch {
            when (val parsed = QuizEngine.parseCustomQuiz(text)) {
                is CustomQuizParseResult.Success -> {
                    repository.quizDao.insertQuestions(parsed.questions)
                    _uiState.update {
                        it.copy(
                            txtImportSuccess = "Success! Loaded ${parsed.questions.size} custom MCQs on '${parsed.subject}' successfully!",
                            rawTxtToImport = ""
                        )
                    }
                }
                is CustomQuizParseResult.Error -> {
                    _uiState.update { it.copy(txtImportError = parsed.message) }
                }
            }
        }
    }

    // --- Garbage Collector / Waste management (Soft Deletion) ---
    fun softDeleteQuestionById(id: Int) {
        viewModelScope.launch {
            repository.quizDao.softDeleteQuestion(id)
        }
    }

    fun restoreQuestionById(id: Int) {
        viewModelScope.launch {
            repository.quizDao.restoreDeletedQuestion(id)
        }
    }

    fun permanentlyPurgeQuestionById(id: Int) {
        viewModelScope.launch {
            repository.quizDao.permanentlyDeleteQuestion(id)
        }
    }

    fun emptyQuestionTrashBin() {
        viewModelScope.launch {
            repository.quizDao.clearTrashBinQuestions()
        }
    }

    fun softDeleteLogById(id: Int) {
        viewModelScope.launch {
            repository.historyLogDao.softDeleteLog(id)
        }
    }

    fun restoreLogById(id: Int) {
        viewModelScope.launch {
            repository.historyLogDao.restoreDeletedLog(id)
        }
    }

    fun permanentlyPurgeLogById(id: Int) {
        viewModelScope.launch {
            repository.historyLogDao.permanentlyDeleteLog(id)
        }
    }

    fun emptyLogsTrashBin() {
        viewModelScope.launch {
            repository.historyLogDao.clearTrashBinLogs()
        }
    }

    // Helper: generate quick template text to make TXT copy-pasting effortless
    fun loadTxtTemplate() {
        val template = """
            Subject: Space Chronicles
            Difficulty: Hard

            Q: What is the approximate age of the universe in billions of years?
            A: 4.5 billion years
            B: 13.8 billion years
            C: 9.3 billion years
            D: 25.1 billion years
            Correct: B

            Q: Which mission successfully landed the first humans on the Moon?
            A: Apollo 11
            B: Gemini 4
            C: Apollo 13
            D: Artemis 1
            Correct: A
        """.trimIndent()
        _uiState.update { it.copy(rawTxtToImport = template, txtImportSuccess = null, txtImportError = null) }
    }
}
