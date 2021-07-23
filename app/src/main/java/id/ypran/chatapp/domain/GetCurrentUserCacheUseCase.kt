package id.ypran.chatapp.domain

class GetCurrentUserCacheUseCase(private val repository: UserRepository) {
    fun execute() = repository.getCurrentUserCache()
}