package io.agora.android_reference_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Sample list of items
        val itemList = listOf(
            ListItem("GET STARTED", "header"),
            ListItem("Basic Implementation"),

            ListItem("DEVELOP", "header"),
            ListItem("Source Authentication"),
            ListItem("Call Quality"),
        )

        // Set up the adapter with the list of items and click listener
        val adapter = ItemListAdapter(itemList, object : ItemListAdapter.ItemClickListener {
            override fun onItemClick(item: ListItem) {
                val message = "Clicked on ${item.title}"
                launchActivity(get_started_sdk_activity::class.java)
                //Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun launchActivity(activityClass: Class<*>) {
        // Launch the corresponding activity when an item is clicked
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
