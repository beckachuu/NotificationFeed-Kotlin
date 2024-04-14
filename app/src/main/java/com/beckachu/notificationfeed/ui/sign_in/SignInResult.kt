package com.beckachu.notificationfeed.ui.sign_in

data class SignInResult(
    val data: com.beckachu.notificationfeed.ui.sign_in.UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)