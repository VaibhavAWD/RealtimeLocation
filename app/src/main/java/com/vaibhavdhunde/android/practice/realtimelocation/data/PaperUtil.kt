package com.vaibhavdhunde.android.practice.realtimelocation.data

import io.paperdb.Paper

object PaperUtil {

    private const val KEY_USER_UID = "uid"

    fun setUserUid(uid: String) {
        Paper.book().write(KEY_USER_UID, uid)
    }

    fun getUserUid(): String? {
        return Paper.book().read(KEY_USER_UID, null)
    }
}