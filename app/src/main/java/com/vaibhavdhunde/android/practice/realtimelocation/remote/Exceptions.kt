package com.vaibhavdhunde.android.practice.realtimelocation.remote

import java.io.IOException

class ApiException(message: String): IOException(message)
class NetworkException(message: String): IOException(message)