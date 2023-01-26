package com.example.storyapp.data.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val name: String,
    val email: String,
    val password: String,
    val isLogin: Boolean
) : Parcelable
