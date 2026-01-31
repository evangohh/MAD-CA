import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: ScoreEntity)

    @Query("SELECT MAX(score) FROM scores WHERE userId = :userId")
    suspend fun getPersonalBest(userId: Long): Int?

    // Each user's best score (for "against other users")
    @Query("""
        SELECT u.username AS username, MAX(s.score) AS bestScore
        FROM users u
        LEFT JOIN scores s ON s.userId = u.userId
        GROUP BY u.userId
        ORDER BY bestScore DESC
    """)
    suspend fun getAllUsersBest(): List<UserBestRow>
}

data class UserBestRow(
    val username: String,
    val bestScore: Int?
)
