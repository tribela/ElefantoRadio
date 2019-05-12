package jarm.mastodon.radio.models

import androidx.room.*

@Dao
interface ApplicationDao {

    @Query("select * from applications")
    fun getAll(): List<Application>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(application: Application)

    @Delete
    fun delete(application: Application)
}