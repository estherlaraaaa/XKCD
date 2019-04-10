package com.munozkessler.xkcd

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.munozkessler.xkcd.Models.Coins


import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import kotlinx.android.synthetic.main.item_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    companion object {
        var mCoinsList = mutableListOf<Coins>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        doAsync {
            for(i in 1..100){
                val result = URL("https://apicoin.herokuapp.com/").readText()
                val coins = JSONObject(result)
                var arraypost = Coins(coins["name"].toString(), coins["country"].toString(), coins["values"].toString(), coins["values_us"].toString(),
                    coins["year"].toString(), coins["review"].toString(), coins["isAvailable"].toString(), coins["img"].toString())
                mCoinsList.add(arraypost)
            }

            uiThread {
                setupRecyclerView(item_list)
            }
        }




    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, mCoinsList, twoPane)
    }



    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        private val values: List<Coins>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Coins
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.name)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.country)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.value)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.value_us)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.year)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.review)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.isAvailable)
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.img)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.name)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.country)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.value)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.value_us)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.year)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.review)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.isAvailable)
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.img)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.name.text = item.name
            holder.country.text = item.country
            holder.value.text = item.value
            holder.value_us.text = item.value_us
            holder.year.text = item.year
            holder.review.text = item.review
            holder.isAvailable.text = item.isAvailable

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.item_list_content_TextView_name
            val country: TextView = view.item_list_content_TextView_country
            val value: TextView = view.item_list_content_TextView_value
            val value_us: TextView = view.item_list_content_TextView_value_us
            val year: TextView = view.item_list_content_TextView_year
            val review: TextView = view.item_list_content_TextView_review
            val isAvailable: TextView = view.item_list_content_TextView_isAvailable
            val img: ImageView = view.item_list_content_ImageView_img

        }
    }
}
