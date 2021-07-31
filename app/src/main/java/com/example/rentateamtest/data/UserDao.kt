package com.example.rentateamtest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getUsersFromDb() : LiveData<List<User>>

    @Insert
    fun insertUser(users: User)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)
}