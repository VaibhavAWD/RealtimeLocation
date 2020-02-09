package com.vaibhavdhunde.android.practice.realtimelocation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vaibhavdhunde.android.practice.realtimelocation.ui.explore.ExploreViewModel
import com.vaibhavdhunde.android.practice.realtimelocation.ui.explore.RemoteRepo
import java.lang.IllegalArgumentException

class MyViewModelFactory(private val repo: RemoteRepo): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(ExploreViewModel::class.java) -> ExploreViewModel(repo)
                else -> throw IllegalArgumentException("Unknown class: $modelClass")
            }
        } as T
    }
}