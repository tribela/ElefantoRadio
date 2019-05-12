package jarm.mastodon.radio.models

import androidx.room.*

@Dao
interface AccountDao {

    @Query("select * from accounts")
    fun getAll(): List<Account>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(account: Account)

    @Delete
    fun delete(account: Account)
}