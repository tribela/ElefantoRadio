package jarm.mastodon.radio.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
class Application(
    @PrimaryKey val domain: String,
    @ColumnInfo(name="application_id") val applicationId: String,
    @ColumnInfo(name="application_secret") val applicationSecret: String
)
