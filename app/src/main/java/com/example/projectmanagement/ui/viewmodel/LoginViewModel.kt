package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.data.model.User
import com.example.projectmanagement.datageneral.domain.usecase.user.SignInUserUseCase
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUserUseCase: SignInUserUseCase
) : ViewModel() {
    
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    
    private val _loginState = MutableLiveData<UiState<User>>()
    val loginState: LiveData<UiState<User>> = _loginState
    
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    fun login() {
        val emailValue = email.value?.trim() ?: ""
        val passwordValue = password.value ?: ""
        
        if (!isEntryValid(emailValue, passwordValue)) {
            return
        }
        
        _loginState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                // Use Supabase signin
                val result = signInUserUseCase(emailValue, passwordValue)
                result.onSuccess { authResponse ->
                    // Create a User object from the response
                    val user = User(
                        id = 0, // Local ID - Supabase uses String IDs
                        name = "", // Will be loaded from profile if needed
                        email = emailValue,
                        password = "" // Don't store password
                    )
                    _loginState.postValue(UiState.Success(user))
                }.onFailure { error ->
                    android.util.Log.e("LoginViewModel", "Login error: ${error.message}", error)
                    val errorMessage = when (error) {
                        is com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure.InvalidCredentials -> {
                            "Invalid email or password"
                        }
                        is com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure.Network -> {
                            "Network error: Please check your internet connection"
                        }
                        is com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure.NotFound -> {
                            "User not found. Please check if your account exists or sign up."
                        }
                        else -> {
                            error.message ?: "Login failed. Please try again."
                        }
                    }
                    _loginState.postValue(UiState.Error(errorMessage))
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "Login exception: ${e.message}", e)
                _loginState.postValue(UiState.Error("Error: ${e.message ?: e.toString()}"))
            }
        }
    }
    
    fun clearErrors() {
        _emailError.value = null
        _passwordError.value = null
    }
    
    fun setEmail(emailValue: String) {
        email.value = emailValue
    }
    
    fun setPassword(passwordValue: String) {
        password.value = passwordValue
    }
    
    private fun isEntryValid(emailValue: String, passwordValue: String): Boolean {
        val isValidEmail = when {
            emailValue.isEmpty() -> {
                _emailError.value = "Email is required"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> {
                _emailError.value = "Invalid email format"
                false
            }
            else -> {
                _emailError.value = null
                true
            }
        }
        
        val isValidPassword = if (passwordValue.isEmpty()) {
            _passwordError.value = "Password is required"
            false
        } else {
            _passwordError.value = null
            true
        }
        
        return isValidEmail && isValidPassword
    }
}

class LoginViewModelFactory(private val signInUserUseCase: SignInUserUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(signInUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
