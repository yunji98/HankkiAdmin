package com.example.hankkiadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

//관리자 앱 메인화면
class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private var orders = mutableListOf<Order>() //변경가능한 리스트를 만들어주기 위해서
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //activity_main 파일을 참조한다

        db.collection("orders").orderBy("orderNum") //firestore의 orders 컬렉션을 orderNum이라는 필드로 정렬한다.
            .addSnapshotListener { snapshots, e -> // 실시간 변경 사항에 대한 변경 사항을 수신
                if (e != null) {
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) { //document가 변경된 만큼 for문
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> { //document가 추가가 되었을 때가
                            val orderNum = dc.document.get("orderNum").toString()
                            val menu = dc.document.get("menu").toString()
                            val amount = dc.document.get("amount").toString()
                            orders.add(Order(orderNum, menu, amount)) //orders 리스트에 데이터클래스 order를 추가
                        }
                    }
                }
                upload() //upload 함수 호출
            }
    }


    private fun upload(){
        val mListView = listView
        val reversedOrders = orders.reversed() as MutableList<Order> //최신순으로 출력하기 위해서 list reversed()해준다
        val mAdapter = ListAdapter(this, reversedOrders)
        mListView?.adapter = mAdapter
        mAdapter.notifyDataSetChanged() //어댑터에 연결된 Listview를 갱신
    }

}