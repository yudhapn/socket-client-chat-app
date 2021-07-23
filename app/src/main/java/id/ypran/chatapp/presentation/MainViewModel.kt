package id.ypran.chatapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ypran.chatapp.data.User
import id.ypran.chatapp.domain.GetCurrentUserCacheUseCase

class MainViewModel(private val getCurrentUserCacheUseCase: GetCurrentUserCacheUseCase) :
    ViewModel() {
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    init {
        _currentUser.postValue(getCurrentUserCacheUseCase.execute())
    }
}