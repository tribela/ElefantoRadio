package jarm.mastodon.radio.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import jarm.mastodon.radio.R
import jarm.mastodon.radio.adapters.AccountAdapter
import jarm.mastodon.radio.models.Account

class AccountsActivity: AppCompatActivity() {

    private var accounts: List<Account>? = null
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        title = "Accounts"

        accounts = listOf(
            Account("jarm@qdon.space", "token", "qdon.space"),
            Account("tribela@witches.live", "token", "witches.live")
        )

        val listView = findViewById<ListView>(R.id.account_list)
        accountAdapter = AccountAdapter(this, accounts!!);
        listView.adapter = accountAdapter
    }
}