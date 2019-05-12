package jarm.mastodon.radio.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
class Account(
    @PrimaryKey
    val acct: String,
    @ColumnInfo(name = "access_token")
    val accessToken: String,
    @ForeignKey(entity = Application::class, parentColumns= ["domain"], childColumns = ["domain"])
    val domain: String
)