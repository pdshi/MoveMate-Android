package com.stevennt.movemate.ui.auth

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.stevennt.movemate.data.MoveMateRepo

class AuthViewModel(private val repository: MoveMateRepo) : ViewModel()  {
    fun login(email: String, pass: String) = repository.login(email, pass)
    fun register(email: String, pass: String, key: String) = repository.register(email, pass)
    fun loginWithFirebase(authToken: String) = repository.loginWithFirebase(authToken)
}