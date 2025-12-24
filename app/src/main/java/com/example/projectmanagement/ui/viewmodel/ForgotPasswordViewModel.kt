package com.example.projectmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _resetLinkSent = MutableLiveData<Boolean>()
    val resetLinkSent: LiveData<Boolean> = _resetLinkSent
    
    fun sendResetLink() {
        val emailValue = email.value?.trim() ?: ""
        
        if (emailValue.isEmpty()) {
            _emailError.value = "Email is required"
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            return
        } else {
            _emailError.value = null
        }
        
        // Fake: just show success message
        _resetLinkSent.value = true
    }
    
    fun clearErrors() {
        _emailError.value = null
    }
}



