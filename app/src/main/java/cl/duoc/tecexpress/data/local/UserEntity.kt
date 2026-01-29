package cl.duoc.tecexpress.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)] // Para asegurar que los nombres de usuario no se repitan
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val isAdmin: Boolean = false,
    val profileImageUri: String? = null
    //imagen de perfil, null por default, se puede cambiar y enlazar con el id de usuario
)
