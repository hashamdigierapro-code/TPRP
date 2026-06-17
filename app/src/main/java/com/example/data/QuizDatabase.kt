package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- entities ---

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // "A", "B", "C", "D"
    val difficulty: String, // "Easy", "Medium", "Hard", "Asian"
    val isCustom: Boolean = false,
    val isDraft: Boolean = false,
    val isDeleted: Boolean = false, // Soft delete for Garbage Collector UI!
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val username: String = "Challenger Neo",
    val totalXP: Int = 100,
    val level: Int = 1,
    val correctAnswersCount: Int = 0,
    val wrongAnswersCount: Int = 0,
    val quizzesCompleted: Int = 0
)

@Entity(tableName = "history_logs")
data class HistoryLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val difficulty: String,
    val score: Int,
    val totalQuestions: Int,
    val xpGained: Int,
    val isDeleted: Boolean = false, // Soft delete for garbage collector
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard_players")
data class LeaderboardPlayer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val xp: Int,
    val countryCode: String, // "US", "AE", "MY", "SG", "IN", "EG" etc
    val avatarEmoji: String,
    val isUser: Boolean = false
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val avatarEmoji: String = "🌙",
    val primaryCountry: String = "Malaysia",
    val selectedStyle: String = "Arabic Lantern",
    val isDarkTheme: Boolean = true,
    val totalXP: Int = 100,
    val level: Int = 1,
    val correctAnswersCount: Int = 0,
    val wrongAnswersCount: Int = 0,
    val quizzesCompleted: Int = 0,
    val totalTries: Int = 0,
    val failedQuizzesCount: Int = 0,
    val succeededQuizzesCount: Int = 0,
    val currentStreak: Int = 0,
    val lastPlayedDate: Long = 0L,
    val activeQuizQuestionsJson: String? = null, // serialized question IDs split by comma
    val activeQuizSubject: String? = null,
    val activeQuizDifficulty: String? = null,
    val activeQuizCurrentIndex: Int = 0,
    val activeQuizScore: Int = 0,
    val activeQuizTimerRemaining: Int = 0,
    val isCurrentActive: Boolean = false
)

// --- DAOs ---

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles ORDER BY id DESC")
    fun getAllProfilesFlow(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE isCurrentActive = 1 LIMIT 1")
    fun getActiveProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE isCurrentActive = 1 LIMIT 1")
    suspend fun getActiveProfileDirect(): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile): Long

    @Query("UPDATE user_profiles SET isCurrentActive = 0")
    suspend fun deactivateAllProfiles()

    @Query("UPDATE user_profiles SET isCurrentActive = 1 WHERE id = :id")
    suspend fun activateProfile(id: Int)

    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Int)
}

@Dao
interface QuizDao {
    // Standard fetchers
    @Query("SELECT * FROM quiz_questions WHERE isDeleted = 0 AND isDraft = 0 ORDER BY createdAt DESC")
    fun getAllActiveQuestions(): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE subject = :subject AND difficulty = :difficulty AND isDeleted = 0 AND isDraft = 0")
    suspend fun getQuestionsForQuiz(subject: String, difficulty: String): List<QuizQuestion>

    @Query("SELECT DISTINCT subject FROM quiz_questions WHERE isDeleted = 0 AND isDraft = 0")
    fun getUniqueSubjects(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestion>)

    // Soft delete/restore (Trash manager)
    @Query("UPDATE quiz_questions SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteQuestion(id: Int)

    @Query("UPDATE quiz_questions SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreDeletedQuestion(id: Int)

    @Query("SELECT * FROM quiz_questions WHERE isDeleted = 1")
    fun getDeletedQuestions(): Flow<List<QuizQuestion>>

    @Query("DELETE FROM quiz_questions WHERE id = :id")
    suspend fun permanentlyDeleteQuestion(id: Int)

    @Query("DELETE FROM quiz_questions WHERE isDeleted = 1")
    suspend fun clearTrashBinQuestions()
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsDirect(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStats)
}

@Dao
interface HistoryLogDao {
    @Query("SELECT * FROM history_logs WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<HistoryLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HistoryLog)

    @Query("UPDATE history_logs SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteLog(id: Int)

    @Query("UPDATE history_logs SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreDeletedLog(id: Int)

    @Query("SELECT * FROM history_logs WHERE isDeleted = 1")
    fun getDeletedLogs(): Flow<List<HistoryLog>>

    @Query("DELETE FROM history_logs WHERE id = :id")
    suspend fun permanentlyDeleteLog(id: Int)

    @Query("DELETE FROM history_logs WHERE isDeleted = 1")
    suspend fun clearTrashBinLogs()
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_players ORDER BY xp DESC")
    fun getLeaderboard(): Flow<List<LeaderboardPlayer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: LeaderboardPlayer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<LeaderboardPlayer>)

    @Query("UPDATE leaderboard_players SET xp = :xp WHERE id = :id")
    suspend fun updatePlayerXp(id: Int, xp: Int)
}

// --- Database Configuration ---

@Database(
    entities = [QuizQuestion::class, UserStats::class, HistoryLog::class, LeaderboardPlayer::class, UserProfile::class],
    version = 3,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun historyLogDao(): HistoryLogDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quizmaster_pro_db2"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
