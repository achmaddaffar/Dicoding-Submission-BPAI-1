package com.example.storyapp.ui.register

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val application: Application) :
    ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    fun registerUser(name: String, email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val isError = responseBody.error as Boolean
                        if (isError) {
                            mSnackBarText.value =
                                Event(application.getString(R.string.email_is_already_taken))
                        } else {
                            mSnackBarText.value =
                                Event(application.getString(R.string.user_created_successfully))
                        }
                    }
                } else {
                    mSnackBarText.value =
                        Event(application.getString(R.string.email_is_already_taken))
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                mSnackBarText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        mIsLoading.value = isLoading
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}