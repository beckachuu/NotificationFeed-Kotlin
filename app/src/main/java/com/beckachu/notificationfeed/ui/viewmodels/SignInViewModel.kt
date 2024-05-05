package com.beckachu.notificationfeed.ui.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.beckachu.notificationfeed.data.SharedPrefsManager
import com.beckachu.notificationfeed.data.SharedPrefsManager.getBool
import com.beckachu.notificationfeed.data.SharedPrefsManager.getString
import com.beckachu.notificationfeed.data.SharedPrefsManager.putBool
import com.beckachu.notificationfeed.data.SharedPrefsManager.putString
import com.beckachu.notificationfeed.ui.sign_in.SignInResult
import com.beckachu.notificationfeed.ui.sign_in.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val sharedPrefs: SharedPreferences =
        application.applicationContext.getSharedPreferences(
            SharedPrefsManager.DEFAULT_NAME,
            Context.MODE_PRIVATE
        )

    private val _state = MutableStateFlow(loadSignInState())
    val state = _state.asStateFlow()

    private fun loadSignInState(): SignInState {
        val isSignInSuccessful = getBool(sharedPrefs, SharedPrefsManager.SIGN_IN_SUCCESSFUL, false)
        val signInError = getString(sharedPrefs, SharedPrefsManager.SIGN_IN_ERROR, null)
        return SignInState(isSignInSuccessful, signInError)
    }

    private fun saveSignInState(state: SignInState) {
        putBool(sharedPrefs, SharedPrefsManager.SIGN_IN_SUCCESSFUL, state.isSignInSuccessful)
        putString(sharedPrefs, SharedPrefsManager.SIGN_IN_ERROR, state.signInError)
        _state.value = loadSignInState()
    }

    fun onSignInResult(result: SignInResult) {
        val newState = SignInState(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        )
        _state.value = newState
        saveSignInState(newState)

        putString(sharedPrefs, SharedPrefsManager.USER_ID, result.data?.userId)
    }

    fun resetState() {
        val newState = SignInState()
        _state.value = newState
        saveSignInState(newState)

        putString(sharedPrefs, SharedPrefsManager.USER_ID, null)
    }
}
