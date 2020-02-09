@file:Suppress("DEPRECATION", "unused")

package com.vaibhavdhunde.android.practice.realtimelocation.util

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import dmax.dialog.SpotsDialog
import org.jetbrains.anko.indeterminateProgressDialog

private lateinit var progressDialog: ProgressDialog

fun AppCompatActivity.showProgress(message: String) {
    progressDialog = indeterminateProgressDialog(message)
}

fun AppCompatActivity.showProgress(message: Int) {
    progressDialog = indeterminateProgressDialog(message)
}

fun hideProgress() {
    if (::progressDialog.isInitialized && progressDialog.isShowing) {
        progressDialog.dismiss()
    }
}

private lateinit var spotsDialog: AlertDialog

fun AppCompatActivity.showSpotsDialog(message: String) {
    spotsDialog = SpotsDialog.Builder().setContext(this).build()
    spotsDialog.setCancelable(false)
    spotsDialog.setMessage(message)
    spotsDialog.show()
}

fun AppCompatActivity.showSpotsDialog(message: Int) {
    spotsDialog = SpotsDialog.Builder().setContext(this).build()
    spotsDialog.setCancelable(false)
    spotsDialog.setMessage(getString(message))
    spotsDialog.show()
}

fun hideSpotsDialog() {
    if (::spotsDialog.isInitialized && spotsDialog.isShowing) {
        spotsDialog.dismiss()
    }
}

fun AppCompatActivity.closeSoftKeyboard() {
    val view = currentFocus
    view?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>): T {
    return ViewModelProviders.of(this).get(viewModelClass)
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(
    viewModelClass: Class<T>,
    factory: MyViewModelFactory
): T {
    return ViewModelProviders.of(this, factory).get(viewModelClass)
}