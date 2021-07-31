package com.example.rentateamtest.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.liveData
import com.example.rentateamtest.TestApplication
import com.example.rentateamtest.TestService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    val usersFromDb = userDao.getUsersFromDb()

    suspend fun tryUpdatingUsers() {
        //       if (isInternetAvailable()) {
        Log.d("___", "updating users")
        fetchNewUsers()
        //       }
    }

    private suspend fun fetchNewUsers() {
        val users = TestService.retrofitService.getUsers()
        withContext(Dispatchers.IO) {
            Log.d("___", "retrieved ${users.data}")
            userDao.insertAll(users.data)
        }
    }


    private fun isInternetAvailable(): Boolean {
        var result = false
        val context = TestApplication.instance.applicationContext
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }
}