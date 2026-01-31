package np.ict.mad.whackamole

import android.content.Context

class HighScorePrefs(context: Context) {
    private val prefs = context.getSharedPreferences("wack_a_mole_prefs", Context.MODE_PRIVATE)

    fun getHighScore(): Int = prefs.getInt("high_score", 0)

    fun setHighScore(value: Int) {
        prefs.edit().putInt("high_score", value).apply()
    }
}
