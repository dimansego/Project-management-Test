package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagement.data.model.User
import com.example.projectmanagement.data.repository.AuthRepository
import com.example.projectmanagement.ui.common.UiState
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
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
        
        var hasError = false
        
        if (nameValue.isEmpty()) {
            _nameError.value = "Name is required"
            hasError = true
        } else {
            _nameError.value = null
        }
        
        if (emailValue.isEmpty()) {
            _emailError.value = "Email is required"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            hasError = true
        } else {
            _emailError.value = null
        }
        
        if (passwordValue.isEmpty()) {
            _passwordError.value = "Password is required"
            hasError = true
        } else if (passwordValue.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            hasError = true
        } else {
            _passwordError.value = null
        }
        
        if (confirmPasswordValue.isEmpty()) {
            _confirmPasswordError.value = "Please confirm password"
            hasError = true
        } else if (passwordValue != confirmPasswordValue) {
            _confirmPasswordError.value = "Passwords do not match"
            hasError = true
        } else {
            _confirmPasswordError.value = null
        }
        
        if (hasError) {
            return
        }
        
        _registerState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                // Check email again before registering
                if (authRepository.isEmailExists(emailValue)) {
                    _emailError.postValue("Email already exists")
                    _registerState.postValue(UiState.Error("Email already exists"))
                    return@launch
                }
                
                val user = authRepository.register(nameValue, emailValue, passwordValue)
                _registerState.postValue(UiState.Success(user))
            } catch (e: Exception) {
                _registerState.postValue(UiState.Error("Error: ${e.message}"))
            }
        }
    }
    
    fun clearErrors() {
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
    }
}



