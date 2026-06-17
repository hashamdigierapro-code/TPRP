package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class QuizRepository(private val database: QuizDatabase) {

    val quizDao = database.quizDao()
    val userStatsDao = database.userStatsDao()
    val historyLogDao = database.historyLogDao()
    val leaderboardDao = database.leaderboardDao()
    val userProfileDao = database.userProfileDao()

    val allUniqueSubjects: Flow<List<String>> = quizDao.getUniqueSubjects()
    val userStats: Flow<UserStats?> = userStatsDao.getUserStatsFlow()
    val historyLogs: Flow<List<HistoryLog>> = historyLogDao.getAllLogs()
    val leaderboard: Flow<List<LeaderboardPlayer>> = leaderboardDao.getLeaderboard()
    
    val allProfiles: Flow<List<UserProfile>> = userProfileDao.getAllProfilesFlow()
    val activeProfile: Flow<UserProfile?> = userProfileDao.getActiveProfileFlow()

    suspend fun createProfile(name: String, avatar: String, country: String, style: String, isDark: Boolean): Long {
        userProfileDao.deactivateAllProfiles()
        val newProfile = UserProfile(
            username = name,
            avatarEmoji = avatar,
            primaryCountry = country,
            selectedStyle = style,
            isDarkTheme = isDark,
            isCurrentActive = true
        )
        val profileId = userProfileDao.insertOrUpdateProfile(newProfile)
        
        // Push stats to the legacy UserStats for compatibility
        userStatsDao.insertOrUpdateUserStats(
            UserStats(username = name, totalXP = 100, level = 1)
        )

        // Also enter the user into the leaderboard!
        val allCompetitors = leaderboardDao.getLeaderboard().firstOrNull() ?: emptyList()
        val userItem = allCompetitors.firstOrNull { it.isUser }
        if (userItem != null) {
            leaderboardDao.insertPlayer(userItem.copy(name = name, xp = 100, avatarEmoji = avatar, countryCode = getCountryCode(country)))
        } else {
            leaderboardDao.insertPlayer(
                LeaderboardPlayer(name = name, xp = 100, countryCode = getCountryCode(country), avatarEmoji = avatar, isUser = true)
            )
        }
        return profileId
    }

    private fun getCountryCode(country: String): String {
        return when (country.lowercase()) {
            "malaysia" -> "MY"
            "india" -> "IN"
            "pakistan" -> "PK"
            "saudi arabia" -> "SA"
            "uae" -> "AE"
            "egypt" -> "EG"
            else -> "US"
        }
    }

    suspend fun selectProfile(id: Int) {
        userProfileDao.deactivateAllProfiles()
        userProfileDao.activateProfile(id)
        
        val profile = userProfileDao.getProfileById(id) ?: return
        
        // Push stats to the legacy UserStats for compatibility
        userStatsDao.insertOrUpdateUserStats(
            UserStats(
                username = profile.username,
                totalXP = profile.totalXP,
                level = profile.level,
                correctAnswersCount = profile.correctAnswersCount,
                wrongAnswersCount = profile.wrongAnswersCount,
                quizzesCompleted = profile.quizzesCompleted
            )
        )

        // Sync to leaderboard
        val allCompetitors = leaderboardDao.getLeaderboard().firstOrNull() ?: emptyList()
        val userItem = allCompetitors.firstOrNull { it.isUser }
        if (userItem != null) {
            leaderboardDao.insertPlayer(
                userItem.copy(
                    name = profile.username, 
                    xp = profile.totalXP, 
                    avatarEmoji = profile.avatarEmoji, 
                    countryCode = getCountryCode(profile.primaryCountry)
                )
            )
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun deleteProfile(id: Int) {
        userProfileDao.deleteProfile(id)
    }

    // Trash bin elements (Garbage Collection)
    val deletedQuestions: Flow<List<QuizQuestion>> = quizDao.getDeletedQuestions()
    val deletedHistoryLogs: Flow<List<HistoryLog>> = historyLogDao.getDeletedLogs()

    // Initialize Default App Data if empty
    suspend fun initializeDatabaseIfEmpty() {
        // Initialize default profiles if empty
        val existingProfiles = userProfileDao.getAllProfilesFlow().firstOrNull() ?: emptyList()
        if (existingProfiles.isEmpty()) {
            // Create a default premium challenger profile
            createProfile("Challenger Neo", "🌙", "Malaysia", "Arabic Lantern", true)
        }

        // 1. Initialize user stats
        val currentStats = userStatsDao.getUserStatsDirect()
        if (currentStats == null) {
            userStatsDao.insertOrUpdateUserStats(
                UserStats(username = "Challenger Neo", totalXP = 120, level = 1)
            )
        }

        // 2. Initialize default quiz database if no active questions exist
        val activeQuestions = quizDao.getAllActiveQuestions().firstOrNull() ?: emptyList()
        if (activeQuestions.isEmpty()) {
            val defaults = generateDefaultQuestions()
            quizDao.insertQuestions(defaults)
        }

        // 3. Initialize default leaderboard competitors
        val competitors = leaderboardDao.getLeaderboard().firstOrNull() ?: emptyList()
        if (competitors.isEmpty()) {
            val defaultCompetitors = listOf(
                LeaderboardPlayer(name = "Zayn Malik", xp = 2450, countryCode = "AE", avatarEmoji = "👑"),
                LeaderboardPlayer(name = "Miko Tan", xp = 1980, countryCode = "SG", avatarEmoji = "🐼"),
                LeaderboardPlayer(name = "Dr. Sabeen", xp = 1620, countryCode = "MY", avatarEmoji = "🐨"),
                LeaderboardPlayer(name = "Ahmed Salah", xp = 1350, countryCode = "EG", avatarEmoji = "🦅"),
                LeaderboardPlayer(name = "Kavita Rao", xp = 1100, countryCode = "IN", avatarEmoji = "🐯"),
                LeaderboardPlayer(name = "Yasmin Al-Farsi", xp = 930, countryCode = "AE", avatarEmoji = "🐪"),
                LeaderboardPlayer(name = "Challenger Neo", xp = 120, countryCode = "MY", avatarEmoji = "🎮", isUser = true)
            )
            leaderboardDao.insertPlayers(defaultCompetitors)
        }
    }

    // Save game results, updates total XP, increments level, inserts a history log,
    // and updates the user's score on the leaderboard too!
    suspend fun saveQuizResult(
        subject: String,
        difficulty: String,
        correctCount: Int,
        totalQuestions: Int,
        xpGained: Int
    ) {
        val activeProf = userProfileDao.getActiveProfileDirect()
        val usernameToUse = activeProf?.username ?: "Challenger Neo"
        
        // 1. Update user statistics and experience levels
        val stats = userStatsDao.getUserStatsDirect() ?: UserStats()
        val newCorrectCount = stats.correctAnswersCount + correctCount
        val newWrongCount = stats.wrongAnswersCount + (totalQuestions - correctCount)
        val newTotalXP = stats.totalXP + xpGained
        val newLevel = (newTotalXP / 300) + 1

        val updatedStats = stats.copy(
            username = usernameToUse,
            correctAnswersCount = newCorrectCount,
            wrongAnswersCount = newWrongCount,
            quizzesCompleted = stats.quizzesCompleted + 1,
            totalXP = newTotalXP,
            level = newLevel
        )
        userStatsDao.insertOrUpdateUserStats(updatedStats)

        // 2. Clear mid-quiz progress and increment attempts/wins/losses on active profile
        if (activeProf != null) {
            val isSuccess = correctCount >= (totalQuestions / 2)
            
            val now = System.currentTimeMillis()
            val dayInMs = 24 * 60 * 60 * 1000L
            val currentDay = now / dayInMs
            val lastPlayedDay = activeProf.lastPlayedDate / dayInMs
            
            val newStreak = when {
                activeProf.lastPlayedDate == 0L -> 1
                currentDay - lastPlayedDay == 1L -> activeProf.currentStreak + 1
                currentDay - lastPlayedDay == 0L -> activeProf.currentStreak
                else -> 1
            }

            val updatedProfile = activeProf.copy(
                totalXP = activeProf.totalXP + xpGained,
                level = ((activeProf.totalXP + xpGained) / 300) + 1,
                correctAnswersCount = activeProf.correctAnswersCount + correctCount,
                wrongAnswersCount = activeProf.wrongAnswersCount + (totalQuestions - correctCount),
                quizzesCompleted = activeProf.quizzesCompleted + 1,
                totalTries = activeProf.totalTries + 1,
                succeededQuizzesCount = activeProf.succeededQuizzesCount + if (isSuccess) 1 else 0,
                failedQuizzesCount = activeProf.failedQuizzesCount + if (isSuccess) 0 else 1,
                currentStreak = newStreak,
                lastPlayedDate = now,
                activeQuizQuestionsJson = null,
                activeQuizSubject = null,
                activeQuizDifficulty = null,
                activeQuizCurrentIndex = 0,
                activeQuizScore = 0
            )
            userProfileDao.insertOrUpdateProfile(updatedProfile)
        }

        // 3. Insert dynamic study session log
        val log = HistoryLog(
            subject = subject,
            difficulty = difficulty,
            score = correctCount,
            totalQuestions = totalQuestions,
            xpGained = xpGained
        )
        historyLogDao.insertLog(log)

        // 4. Update active user score on highscore leaderboard
        val allCompetitors = leaderboardDao.getLeaderboard().firstOrNull() ?: emptyList()
        val userItem = allCompetitors.firstOrNull { it.isUser }
        if (userItem != null) {
            val codeToUse = activeProf?.let { getCountryCode(it.primaryCountry) } ?: "MY"
            val avatarToUse = activeProf?.avatarEmoji ?: "🎮"
            leaderboardDao.insertPlayer(
                userItem.copy(name = usernameToUse, xp = if (activeProf != null) (activeProf.totalXP + xpGained) else newTotalXP, countryCode = codeToUse, avatarEmoji = avatarToUse)
            )
        } else {
            val codeToUse = activeProf?.let { getCountryCode(it.primaryCountry) } ?: "MY"
            val avatarToUse = activeProf?.avatarEmoji ?: "🎮"
            leaderboardDao.insertPlayer(
                LeaderboardPlayer(name = usernameToUse, xp = if (activeProf != null) (activeProf.totalXP + xpGained) else newTotalXP, countryCode = codeToUse, avatarEmoji = avatarToUse, isUser = true)
            )
        }

        // Simulate real-time competing environment right after a quiz!
        simulateCompetitorProgress()
    }

    // Leaderboard Live Simulation: generates random XP jumps for other simulated online competitors
    suspend fun simulateCompetitorProgress() {
        val allCompetitors = leaderboardDao.getLeaderboard().firstOrNull() ?: emptyList()
        for (competitor in allCompetitors) {
            if (!competitor.isUser) {
                // 30% chance for other players to progress and score dynamic XP additions!
                if (Random.nextFloat() < 0.35f) {
                    val incrementalXp = Random.nextInt(15, 70)
                    leaderboardDao.updatePlayerXp(competitor.id, competitor.xp + incrementalXp)
                }
            }
        }
    }

    // Populate initial items for quizzes
    private fun generateDefaultQuestions(): List<QuizQuestion> {
        val defaultList = listOf(
            // --- GENERAL KNOWLEDGE ---
            QuizQuestion(
                subject = "General Knowledge",
                difficulty = "Easy",
                questionText = "Which planet is known as the Red Planet?",
                optionA = "Venus", optionB = "Mars", optionC = "Jupiter", optionD = "Saturn",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "General Knowledge",
                difficulty = "Easy",
                questionText = "How many colors are there in a standard rainbow?",
                optionA = "6", optionB = "7", optionC = "8", optionD = "9",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "General Knowledge",
                difficulty = "Medium",
                questionText = "What is the capital city of Australia?",
                optionA = "Sydney", optionB = "Melbourne", optionC = "Canberra", optionD = "Brisbane",
                correctAnswer = "C"
            ),
            QuizQuestion(
                subject = "General Knowledge",
                difficulty = "Hard",
                questionText = "Which treaty officially ended World War I in 1919?",
                optionA = "Treaty of Versailles", optionB = "Treaty of Paris", optionC = "Treaty of London", optionD = "Treaty of Berlin",
                correctAnswer = "A"
            ),
            QuizQuestion(
                subject = "General Knowledge",
                difficulty = "Asian",
                questionText = "What insect is highly revered and cultivated for premium natural silk extraction?",
                optionA = "Drosophila melanogaster", optionB = "Apis mellifera", optionC = "Bombyx mori", optionD = "Anopheles gambiae",
                correctAnswer = "C"
            ),

            // --- SCIENCE ---
            QuizQuestion(
                subject = "Science",
                difficulty = "Easy",
                questionText = "What is the chemical formula for water?",
                optionA = "CO2", optionB = "H2O", optionC = "NaCl", optionD = "O2",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Science",
                difficulty = "Medium",
                questionText = "Which organ in the human body is primarily responsible for pumping blood?",
                optionA = "Lungs", optionB = "Kidneys", optionC = "Brain", optionD = "Heart",
                correctAnswer = "D"
            ),
            QuizQuestion(
                subject = "Science",
                difficulty = "Hard",
                questionText = "Which subatomic particle carries a negative electric charge?",
                optionA = "Proton", optionB = "Neutron", optionC = "Electron", optionD = "Quark",
                correctAnswer = "C"
            ),
            QuizQuestion(
                subject = "Science",
                difficulty = "Asian",
                questionText = "Which compound is the main structural component of plant cell walls, composed of linear glucose chains?",
                optionA = "Cellulose", optionB = "Chitin", optionC = "Glycogen", optionD = "Amylose",
                correctAnswer = "A"
            ),

            // --- MATH ---
            QuizQuestion(
                subject = "Math",
                difficulty = "Easy",
                questionText = "What is 15 - 7 * 2?",
                optionA = "16", optionB = "1", optionC = "6", optionD = "10",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Math",
                difficulty = "Medium",
                questionText = "What is the value of 5 factorial (5!)?",
                optionA = "60", optionB = "120", optionC = "24", optionD = "150",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Math",
                difficulty = "Hard",
                questionText = "Solve for x: 3x - 7 = 5x + 3",
                optionA = "x = -5", optionB = "x = 5", optionC = "x = -2", optionD = "x = 2",
                correctAnswer = "A"
            ),
            QuizQuestion(
                subject = "Math",
                difficulty = "Asian",
                questionText = "What is the limit of (sin(x)/x) as x approaches 0?",
                optionA = "0", optionB = "1", optionC = "Infinity", optionD = "Undefined",
                correctAnswer = "B"
            ),

            // --- COMPUTER SCIENCE ---
            QuizQuestion(
                subject = "Computer Science",
                difficulty = "Easy",
                questionText = "What does HTML stand for in layout development?",
                optionA = "HyperText Markup Language", optionB = "HighText Machined Language", optionC = "HyperLink Mixed Language", optionD = "Home Tool Markup Language",
                correctAnswer = "A"
            ),
            QuizQuestion(
                subject = "Computer Science",
                difficulty = "Medium",
                questionText = "Which protocol is standard for fetching websites securely over the web?",
                optionA = "FTP", optionB = "HTTP", optionC = "HTTPS", optionD = "SMTP",
                correctAnswer = "C"
            ),
            QuizQuestion(
                subject = "Computer Science",
                difficulty = "Hard",
                questionText = "Which data structure operates on a 'First In, First Out' (FIFO) access schedule?",
                optionA = "Stack", optionB = "Queue", optionC = "Binary Tree", optionD = "Hash Map",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Computer Science",
                difficulty = "Asian",
                questionText = "What is the average time complexity of searching inside a balanced Red-Black Search Tree?",
                optionA = "O(1)", optionB = "O(N)", optionC = "O(log N)", optionD = "O(N log N)",
                correctAnswer = "C"
            ),

            // --- GOVT JOBS PREP (MALAYSIA) ---
            QuizQuestion(
                subject = "Govt Jobs Prep (Malaysia)",
                difficulty = "Easy",
                questionText = "What represents the supreme law of the federation in Malaysia, superseding any other laws passed by Parliament?",
                optionA = "Acts of Parliament", optionB = "The Federal Constitution", optionC = "Royal Decrees of the Agong", optionD = "English Common Law",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Malaysia)",
                difficulty = "Medium",
                questionText = "Which judicial body in Malaysia holds the supreme constitutional authority to interpret the Federal Constitution?",
                optionA = "The High Court of Malaya", optionB = "The Court of Appeal", optionC = "The Federal Court", optionD = "The Syariah Court",
                correctAnswer = "C"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Malaysia)",
                difficulty = "Hard",
                questionText = "What is the official title of Malaysia's Head of State, who is elected for a five-year term from among the nine Malay Rulers?",
                optionA = "Prime Minister (Perdana Menteri)", optionB = "Yang di-Pertuan Agong", optionC = "Sultan of Selangor", optionD = "Governor (Yang di-Pertua Negeri)",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Malaysia)",
                difficulty = "Asian",
                questionText = "Under Article 153 of the Federal Constitution of Malaysia, the Yang di-Pertuan Agong is responsible for safeguarding the special position of which group?",
                optionA = "All Malaysian citizens equally", optionB = "State Royalty and Nobles", optionC = "Malays and natives of Sabah and Sarawak", optionD = "Members of the Civil Service SPA",
                correctAnswer = "C"
            ),

            // --- GOVT JOBS PREP (INDIA) ---
            QuizQuestion(
                subject = "Govt Jobs Prep (India)",
                difficulty = "Easy",
                questionText = "How many members are nominated by the President of India to the Rajya Sabha (the Upper House of Parliament)?",
                optionA = "10 members", optionB = "12 members", optionC = "15 members", optionD = "20 members",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (India)",
                difficulty = "Medium",
                questionText = "Which Article of the Indian Constitution empowers the President of India to impose President's Rule in a State due to constitutional machinery failure?",
                optionA = "Article 352", optionB = "Article 356", optionC = "Article 360", optionD = "Article 368",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (India)",
                difficulty = "Hard",
                questionText = "The landmark concept of 'Basic Structure Doctrine' of the Indian Constitution was propounded in which historic Supreme Court judgment?",
                optionA = "Golaknath v. State of Punjab", optionB = "Kesavananda Bharati v. State of Kerala", optionC = "Minerva Mills v. Union of India", optionD = "Maneka Gandhi v. Union of India",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (India)",
                difficulty = "Asian",
                questionText = "Which of the following organizations or bodies is NOT a constitutional body established directly under the Constitution of India?",
                optionA = "NITI Aayog", optionB = "Election Commission", optionC = "Finance Commission", optionD = "Union Public Service Commission (UPSC)",
                correctAnswer = "A"
            ),

            // --- GOVT JOBS PREP (PAKISTAN) ---
            QuizQuestion(
                subject = "Govt Jobs Prep (Pakistan)",
                difficulty = "Easy",
                questionText = "Who was the first Prime Minister of Pakistan appointed immediately following independence in August 1947?",
                optionA = "Muhammad Ali Jinnah", optionB = "Liaquat Ali Khan", optionC = "Khawaja Nazimuddin", optionD = "Chaudhry Muhammad Ali",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Pakistan)",
                difficulty = "Medium",
                questionText = "In which year was the Objectives Resolution, which serves as the foundational framework of Pakistan's constitution-making, adopted by the Constituent Assembly?",
                optionA = "1947", optionB = "1948", optionC = "1949", optionD = "1956",
                correctAnswer = "C"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Pakistan)",
                difficulty = "Hard",
                questionText = "The strategically vital Gilgit-Baltistan region of Pakistan is bordered by how many sovereign nations?",
                optionA = "2 countries", optionB = "3 countries", optionC = "4 countries", optionD = "None",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Pakistan)",
                difficulty = "Asian",
                questionText = "Which Amendment to the 1973 Constitution of Pakistan permanently revoked the President's unilateral power to dissolve the National Assembly under Article 58(2)(b)?",
                optionA = "13th Amendment", optionB = "17th Amendment", optionC = "18th Amendment", optionD = "21st Amendment",
                correctAnswer = "C"
            ),

            // --- GOVT JOBS PREP (SAUDI ARABIA) ---
            QuizQuestion(
                subject = "Govt Jobs Prep (Saudi Arabia)",
                difficulty = "Easy",
                questionText = "Which city serves as the official administrative capital, political hub, and seat of government of the Kingdom of Saudi Arabia?",
                optionA = "Jeddah", optionB = "Riyadh", optionC = "Mecca", optionD = "Medina",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Saudi Arabia)",
                difficulty = "Medium",
                questionText = "Which Royal Decree outlines Saudi Arabia's foundational legal frameworks, public administration, state system, and fundamental citizen rights?",
                optionA = "The Shura Regulatory Act of 1992", optionB = "The Basic Law of Governance of 1991", optionC = "The General Administrative Charter", optionD = "The Council of Ministers Statutes",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Saudi Arabia)",
                difficulty = "Hard",
                questionText = "Which massive futuristic giga-project, crucial for Saudi Vision 2030, is currently under construction in the country's northwest Tabuk Province?",
                optionA = "Qiddiya Entertainment District", optionB = "NEOM Smart Megacity", optionC = "The Red Sea Tourism Masterplan", optionD = "Diriyah Gate Historical Development",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Saudi Arabia)",
                difficulty = "Asian",
                questionText = "According to the Basic Law of Governance of Saudi Arabia, who possesses the absolute authority to form and dissolve the Council of Ministers?",
                optionA = "Joint Shura Council vote", optionB = "The King of Saudi Arabia", optionC = "The Prime Minister by ministerial decree", optionD = "The Ministry of Human Resources Civil Bureau",
                correctAnswer = "B"
            ),

            // --- GOVT JOBS PREP (UAE) ---
            QuizQuestion(
                subject = "Govt Jobs Prep (UAE)",
                difficulty = "Easy",
                questionText = "In which year was the sovereign federal state of the United Arab Emirates (UAE) officially established by the joining of the emirates?",
                optionA = "1968", optionB = "1971", optionC = "1973", optionD = "1975",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (UAE)",
                difficulty = "Medium",
                questionText = "Under the UAE Federal Constitution, which entity sits as the highest constitutional body, representing the rulers of all seven emirates?",
                optionA = "The Federal National Council", optionB = "The Federal Supreme Council", optionC = "The Council of Ministers", optionD = "The Cabinet Office",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (UAE)",
                difficulty = "Hard",
                questionText = "What is the legislative, advisory body of the UAE, consisting of 40 representatives representing different emirates?",
                optionA = "The Federal Supreme Council", optionB = "The Federal National Council (FNC)", optionC = "The Abu Dhabi Executive Council", optionD = "The National Human Resources Authority",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (UAE)",
                difficulty = "Asian",
                questionText = "Which specialized federal authority in the United Arab Emirates is responsible for auditing federal accounts and ensuring optimal administrative integrity?",
                optionA = "The Central Bank of the UAE", optionB = "The Federal Audit Authority (FAA)", optionC = "The Ministry of State for Financial Affairs", optionD = "The Executive Council Bureau",
                correctAnswer = "B"
            ),

            // --- GOVT JOBS PREP (EGYPT) ---
            QuizQuestion(
                subject = "Govt Jobs Prep (Egypt)",
                difficulty = "Easy",
                questionText = "Which critical maritime canal, managed exclusively by Egypt, serves as the premier global shipping link between the Red Sea and the Mediterranean Sea?",
                optionA = "Panama Canal", optionB = "Bosporus Strait", optionC = "Suez Canal", optionD = "Strait of Malacca",
                correctAnswer = "C"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Egypt)",
                difficulty = "Medium",
                questionText = "Egypt restructured civil services, compensation, merit-evaluation, and public appointments in 2016 through which landmark piece of legislation?",
                optionA = "Emergency Decree Law of 2011", optionB = "Constitutional Reform Law of 2014", optionC = "Suez Canal Special Authority Law of 2015", optionD = "Civil Service Law No. 81 of 2016",
                correctAnswer = "D"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Egypt)",
                difficulty = "Hard",
                questionText = "Which independent regulatory body in Egypt is officially tasked with managing human resource development, job training, and organization structures of government ministries?",
                optionA = "Ministry of Foreign Affairs", optionB = "The Central Agency for Organization and Administration (CAQA)", optionC = "The Supreme Constitutional Court", optionD = "The Administrative Control Authority",
                correctAnswer = "B"
            ),
            QuizQuestion(
                subject = "Govt Jobs Prep (Egypt)",
                difficulty = "Asian",
                questionText = "According to Egypt's Civil Service Law No. 81 of 2016, what is the legal status regarding the creation of temporary/exceptional contracts in executive state agencies?",
                optionA = "Strictly prohibited; all administrative hiring must be via nationwide public merit competition", optionB = "Permitted for up to 5% of agency budget seats", optionC = "Requires only governorate approval before appointment", optionD = "Permitted up to 10% for consultancy purposes",
                correctAnswer = "A"
            )
        )
        
        val moreQuestions = mutableListOf<QuizQuestion>()
        moreQuestions.addAll(defaultList)
        
        // Add 30 Random Math Questions
        val mathRandom = kotlin.random.Random(42)
        for(i in 1..30) {
            val a = mathRandom.nextInt(1, 100)
            val b = mathRandom.nextInt(1, 100)
            val isAdd = mathRandom.nextBoolean()
            val op = if (isAdd) "+" else "-"
            val ans = if (isAdd) (a + b) else (a - b)
            val wr1 = ans + mathRandom.nextInt(1, 10)
            val wr2 = ans - mathRandom.nextInt(1, 10)
            val wr3 = ans + mathRandom.nextInt(11, 20)
            
            // Randomly place correct answer
            val slots = mutableListOf(ans.toString(), wr1.toString(), wr2.toString(), wr3.toString())
            slots.shuffle(mathRandom)
            val correctIdx = slots.indexOf(ans.toString())
            val correctChar = ('A' + correctIdx).toString()
            
            moreQuestions.add(
                QuizQuestion(
                    subject = "Math",
                    difficulty = if (i > 20) "Hard" else if (i > 10) "Medium" else "Easy",
                    questionText = "What is $a $op $b?",
                    optionA = slots[0], optionB = slots[1], optionC = slots[2], optionD = slots[3],
                    correctAnswer = correctChar
                )
            )
        }
        
        // Add 20 Geography Questions
        val geoPairs = listOf(
            "France" to "Paris", "Germany" to "Berlin", "Japan" to "Tokyo", "Italy" to "Rome", "Spain" to "Madrid",
            "Canada" to "Ottawa", "Australia" to "Canberra", "Brazil" to "Brasilia", "India" to "New Delhi", "China" to "Beijing",
            "Russia" to "Moscow", "South Korea" to "Seoul", "Mexico" to "Mexico City", "Egypt" to "Cairo", "Turkey" to "Ankara",
            "Argentina" to "Buenos Aires", "Saudi Arabia" to "Riyadh", "South Africa" to "Pretoria", "Sweden" to "Stockholm", "Norway" to "Oslo"
        )
        val dummyCities = listOf("New York", "Dubai", "Sydney", "Toronto", "Istanbul", "Shanghai", "Mumbai", "Los Angeles", "Barcelona", "Amsterdam")
        
        for ((idx, pair) in geoPairs.withIndex()) {
            val country = pair.first
            val city = pair.second
            val options = mutableListOf(city, dummyCities[(idx) % dummyCities.size], dummyCities[(idx+1) % dummyCities.size], dummyCities[(idx+2) % dummyCities.size])
            options.shuffle(mathRandom)
            
            val correctIdx = options.indexOf(city)
            val correctChar = ('A' + correctIdx).toString()
            
            moreQuestions.add(
                QuizQuestion(
                    subject = "Geography",
                    difficulty = if (idx > 10) "Medium" else "Easy",
                    questionText = "What is the capital of $country?",
                    optionA = options[0], optionB = options[1], optionC = options[2], optionD = options[3],
                    correctAnswer = correctChar
                )
            )
        }
        
        // Add 20 Science Questions
        val elements = listOf(
            "Oxygen" to "O", "Hydrogen" to "H", "Carbon" to "C", "Nitrogen" to "N", "Helium" to "He",
            "Iron" to "Fe", "Gold" to "Au", "Silver" to "Ag", "Copper" to "Cu", "Sodium" to "Na",
            "Calcium" to "Ca", "Potassium" to "K", "Magnesium" to "Mg", "Chlorine" to "Cl", "Sulfur" to "S",
            "Zinc" to "Zn", "Lead" to "Pb", "Uranium" to "U", "Titanium" to "Ti", "Neon" to "Ne"
        )
        val dummySymbols = listOf("X", "Y", "Z", "A", "B", "M", "L", "D", "E", "W")
        for ((idx, pair) in elements.withIndex()) {
            val element = pair.first
            val symbol = pair.second
            
            val options = mutableListOf(symbol, dummySymbols[(idx) % dummySymbols.size], dummySymbols[(idx+1) % dummySymbols.size], dummySymbols[(idx+2) % dummySymbols.size])
            options.shuffle(mathRandom)
            val correctIdx = options.indexOf(symbol)
            val correctChar = ('A' + correctIdx).toString()
            
            moreQuestions.add(
                QuizQuestion(
                    subject = "Science",
                    difficulty = "Medium",
                    questionText = "What is the chemical symbol for $element?",
                    optionA = options[0], optionB = options[1], optionC = options[2], optionD = options[3],
                    correctAnswer = correctChar
                )
            )
        }
        
        // Add 20 Computer Science Questions
        val abbrevs = listOf(
            "CPU" to "Central Processing Unit", "RAM" to "Random Access Memory", "ROM" to "Read Only Memory", "HTTP" to "Hypertext Transfer Protocol", "URL" to "Uniform Resource Locator",
            "HTML" to "Hypertext Markup Language", "IP" to "Internet Protocol", "LAN" to "Local Area Network", "WAN" to "Wide Area Network", "PDF" to "Portable Document Format",
            "USB" to "Universal Serial Bus", "GPU" to "Graphics Processing Unit", "SSD" to "Solid State Drive", "HDD" to "Hard Disk Drive", "GUI" to "Graphical User Interface",
            "API" to "Application Programming Interface", "DNS" to "Domain Name System", "FTP" to "File Transfer Protocol", "SQL" to "Structured Query Language", "VPN" to "Virtual Private Network"
        )
        val dummyFull = listOf("Computer Personal Unit", "Random Align Method", "Read Out Memory", "Hyper Type Text", "Universal Remote Locator")
        
        for ((idx, pair) in abbrevs.withIndex()) {
            val abbrev = pair.first
            val full = pair.second
            val options = mutableListOf(full, dummyFull[(idx) % dummyFull.size], dummyFull[(idx+1) % dummyFull.size], "Central Control Protocol")
            options.shuffle(mathRandom)
            val correctIdx = options.indexOf(full)
            val correctChar = ('A' + correctIdx).toString()
            
            moreQuestions.add(
                QuizQuestion(
                    subject = "Computer Science",
                    difficulty = "Hard",
                    questionText = "What does $abbrev stand for?",
                    optionA = options[0], optionB = options[1], optionC = options[2], optionD = options[3],
                    correctAnswer = correctChar
                )
            )
        }
        
        return moreQuestions
    }
}
