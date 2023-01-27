package com.example.storyapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.R
import com.example.storyapp.data.local.UserModel
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryViewModel(
    private val pref: UserPreference, private val application: Application
) :
    ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mListStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = mListStory

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    private fun showLoading(isLoading: Boolean) {
        mIsLoading.value = isLoading
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getAllStory(token: String) {
        showLoading(true)
        val client = ApiConfig.getApiService()
            .getAllStory(token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        mListStory.value = responseBody.listStory as List<ListStoryItem>
                    }
                } else {
                    mSnackBarText.value = Event(application.getString(R.string.failed_to_connect))
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                mSnackBarText.value = Event(application.getString(R.string.failed_to_connect))
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "ListStoryViewModel"
    }
}