import android.content.Context

object HeightPreferences {
    private const val PREFS_NAME = "Height_Prefs"
    private const val KEY_HEIGHT = "height"

    fun saveHeight(context: Context, height: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_HEIGHT, height).apply()
    }

    fun getHeight(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_HEIGHT, 0f)
    }
}
