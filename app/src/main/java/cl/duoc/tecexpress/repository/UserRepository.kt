package cl.duoc.tecexpress.repository

import cl.duoc.tecexpress.data.local.UserDao
import cl.duoc.tecexpress.data.local.UserEntity

class UserRepository(private val dao: UserDao) {

    suspend fun findByUsername(username: String): UserEntity? {
        return dao.findByUsername(username)
    }

    suspend fun insert(user: UserEntity) {
        dao.insert(user)
    }
}
