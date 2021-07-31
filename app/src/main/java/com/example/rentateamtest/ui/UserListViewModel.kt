package com.example.rentateamtest.ui

import androidx.lifecycle.*
import com.example.rentateamtest.data.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class UserListViewModel(private val repository: UserRepository) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _isLoading.value = true
                block()
            } catch (e: Throwable) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    val users = repository.usersFromDb

    init {
        launchDataLoad { repository.tryUpdatingUsers() }
    }
}

class UserListViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            return UserListViewModel(repository) as T
        }
        throw(IllegalArgumentException("Wrong model class"))
    }
}