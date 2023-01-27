package com.example.storyapp.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.R
import com.example.storyapp.data.local.UserModel
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.Event
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val pref: UserPreference,
    private val application: Application
) :
    ViewModel() {

    private val mIsAnimate = MutableLiveData<Event<Boolean>>()
    val isAnimate: LiveData<Event<Boolean>> = mIsAnimate

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mLoginError = MutableLiveData<Boolean>()
    val loginError: LiveData<Boolean> = mLoginError

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    init {
        mIsAnimate.value = Event(false)
    }

    fun login(email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        mLoginError.value = responseBody.error as Boolean
                        val loginResult = responseBody.loginResult as LoginResult
                        val name = loginResult.name as String
                        val token = loginResult.token as String
                        viewModelScope.launch {
                            pref.saveUser(UserModel(name, email, token))
                        }
                    }
                } else {
                    mLoginError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                mSnackBarText.value = Event(application.getString(R.string.failed_to_connect))
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        mIsLoading.value = isLoading
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}