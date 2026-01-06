package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.datageneral.model.User
import com.example.projectmanagement.datageneral.domain.usecase.user.SignUpUserUseCase
import com.example.projectmanagement.datageneral.domain.usecase.user.exception.UserAuthFailure
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val signUpUserUseCase: SignUpUserUseCase
) : ViewModel() {
    
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    
    private val _registerState = MutableLiveData<UiState<User>>()
    val registerState: LiveData<UiState<User>> = _registerState
    
    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError
    
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError
    
    fun register() {
        val nameValue = name.value?.trim() ?: ""
        val emailValue = email.value?.trim() ?: ""
        val passwordValue = password.value ?: ""
        val confirmPasswordValue = confirmPassword.value ?: ""
        
        if (!isEntryValid(nameValue, emailValue, passwordValue, confirmPasswordValue)) {
            return
        }
        
        _registerState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                // Use Supabase signup
                val result = signUpUserUseCase(emailValue, passwordValue, nameValue)
                result.onSuccess { authResponse ->
                    android.util.Log.d("RegisterViewModel", "Registration successful, authResponse: $authResponse")
                    // Create a User object from the response - always navigate on success
                    // Even if profile creation failed, auth account is created
                    val user = User(
                        id = 0, // Local ID - Supabase uses String IDs
                        name = nameValue,
                        email = emailValue,
                        password = "" // Don't store password
                    )
                    _registerState.postValue(UiState.Success(user))
                }.onFailure { error ->
                    android.util.Log.e("RegisterViewModel", "Registration failed: ${error.message}", error)
                    // Log the actual error for debugging
                    android.util.Log.e("RegisterViewModel", "Registration error: ${error.message}", error)
                    
                    // Show more specific error message
                    val errorMessage = when (error) {
                        is UserAuthFailure.Network -> {
                            "Network error: Please check your internet connection and Supabase configuration. Make sure SUPABASE_URL and SUPABASE_ANON_KEY are set in local.properties"
                        }
                        is UserAuthFailure.Validation -> {
                            error.message ?: "Invalid input"
                        }
                        is UserAuthFailure.InvalidCredentials -> {
                            "Invalid credentials"
                        }
                        is UserAuthFailure.NotFound -> {
                            "User not found. Please check Supabase configuration."
                        }
                        else -> {
                            error.message ?: error.toString()
                        }
                    }
                    
                    _registerState.postValue(UiState.Error(errorMessage))
                    
                    if (error.message?.contains("already exists", ignoreCase = true) == true ||
                        error.message?.contains("already registered", ignoreCase = true) == true) {
                        _emailError.postValue("Email already exists")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RegisterViewModel", "Registration exception: ${e.message}", e)
                _registerState.postValue(UiState.Error("Error: ${e.message ?: e.toString()}"))
            }
        }
    }
    
    fun clearErrors() {
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
    }
    
    fun setName(nameValue: String) {
        name.value = nameValue
    }
    
    fun setEmail(emailValue: String) {
        email.value = emailValue
    }
    
    fun setPassword(passwordValue: String) {
        password.value = passwordValue
    }
    
    fun setConfirmPassword(confirmPasswordValue: String) {
        confirmPassword.value = confirmPasswordValue
    }
    
    private fun isEntryValid(nameValue: String, emailValue: String, passwordValue: String, confirmPasswordValue: String): Boolean {
        val isValidName = if (nameValue.isEmpty()) {
            _nameError.value = "Name is required"
            false
        } else {
            _nameError.value = null
            true
        }
        
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
        
        val isValidPassword = when {
            passwordValue.isEmpty() -> {
                _passwordError.value = "Password is required"
                false
            }
            passwordValue.length < 6 -> {
                _passwordError.value = "Password must be at least 6 characters"
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
        
        val isValidConfirmPassword = when {
            confirmPasswordValue.isEmpty() -> {
                _confirmPasswordError.value = "Please confirm password"
                false
            }
            passwordValue != confirmPasswordValue -> {
                _confirmPasswordError.value = "Passwords do not match"
                false
            }
            else -> {
                _confirmPasswordError.value = null
                true
            }
        }
        
        return isValidName && isValidEmail && isValidPassword && isValidConfirmPassword
    }
}

class RegisterViewModelFactory(private val signUpUserUseCase: SignUpUserUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(signUpUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
