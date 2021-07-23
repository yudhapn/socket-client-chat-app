package id.ypran.chatapp.domain

import id.ypran.chatapp.data.User

interface UserRepository {
    fun getCurrentUserCache(): User
}