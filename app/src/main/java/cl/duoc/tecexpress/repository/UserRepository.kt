package cl.duoc.tecexpress.repository

import cl.duoc.tecexpress.data.local.UserDao
import cl.duoc.tecexpress.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun findByUsername(username: String): UserEntity? {
        return dao.findByUsername(username)
    }

    suspend fun findById(id: Long): UserEntity? {
        return dao.findById(id)
    }

    suspend fun insert(user: UserEntity) {
        dao.insert(user)
    }

    suspend fun update(user: UserEntity) {
        dao.update(user)
    }
}
