package com.example.hankkiadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private var orders = mutableListOf<Order>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db.collection("orders")
            .orderBy("orderNum")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val orderNum = dc.document.get("orderNum").toString()
                            val menu = dc.document.get("menu").toString()
                            val amount = dc.document.get("amount").toString()
                            orders.add(Order(orderNum, menu, amount))
                        }
                    }
                }
                upload()
            }
    }


    fun upload(){
        val mListView = listView
        val reversedOrders = orders.reversed() as MutableList<Order>
        val mAdapter = ListAdapter(this, reversedOrders)
        mListView?.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

}