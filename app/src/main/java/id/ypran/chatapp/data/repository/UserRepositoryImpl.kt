package id.ypran.chatapp.data.repository

import id.ypran.chatapp.data.User
import id.ypran.chatapp.domain.UserRepository

class UserRepositoryImpl(private val currentUser: User) : UserRepository {
    override fun getCurrentUserCache(): User = currentUser
}