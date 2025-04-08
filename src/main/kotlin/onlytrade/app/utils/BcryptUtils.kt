package onlytrade.app.utils

import org.mindrot.jbcrypt.BCrypt

object BcryptUtils {

    // Hashing the password
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    // Verifying the password
    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}