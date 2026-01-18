package cl.duoc.tecexpress.util

object Validators {
    fun isNotEmpty(value: String): Boolean {
        return value.trim().isNotEmpty()
    }
}
