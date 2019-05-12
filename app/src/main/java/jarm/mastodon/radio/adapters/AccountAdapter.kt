package jarm.mastodon.radio.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import jarm.mastodon.radio.R
import jarm.mastodon.radio.models.Account

data class AccountAdapter(
    private val context: Context,
    private val accounts: List<Account>
): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.list_account, null)

        val item = accounts[position]
        val acctView = view.findViewById<TextView>(R.id.acct)

        acctView.text = item.acct

        return view
    }

    override fun getItem(position: Int): Any = accounts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = accounts.size

}