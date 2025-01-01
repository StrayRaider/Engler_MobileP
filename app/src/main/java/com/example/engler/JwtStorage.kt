import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class JwtStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "jwt_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Save JWT token securely.
     */
    fun saveJwtToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    /**
     * Retrieve the saved JWT token.
     */
    fun getJwtToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    /**
     * Clear the saved JWT token.
     */
    fun clearJwtToken() {
        val editor = sharedPreferences.edit()
        editor.remove("jwt_token")
        editor.apply()
    }
}
