package com.example.hankkiadmin

import android.content.Context
import android.graphics.Color.rgb
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.order_list.view.*

//주문 데이터 클래스
data class  Order(val orderNum : String, val menu : String, val amount : String)

// MainActivity에 있는 ListView inflate해주는 Adapter
class ListAdapter : BaseAdapter {
    private val db = FirebaseFirestore.getInstance()
    private val ctx: Context?
    private var order = mutableListOf<Order>()

    constructor(_ctx: Context?, _order: MutableList<Order>) {
        ctx = _ctx
        order = _order
    }

    override fun getCount(): Int {
        return order.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        val inflater = LayoutInflater.from(ctx)
        view = inflater.inflate(R.layout.order_list, parent, false)  // list 하나에 order_list로 구성되게 해준다

        val m = order[position]

        view.orderNumView.text = m.orderNum
        view.menuView.text = m.menu
        view.amountView.text = "X" + m.amount

        // 음식 제조 완료 여부를 확인할 수 있는 변수
        var finish = false

        // orders 컬렉션을 가져와서 menu 이름 비교해서 finish가 true면 버튼 비활성화, 배경색 바꿈
        db.collection("orders")
            .whereEqualTo("menu", m.menu)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    finish = document.get("finish").toString().toBoolean()
                }
                if(finish){
                    //버튼 비활성화
                    view.finishBtn.isEnabled = false
                    //버튼 색바꾸기

                    view.finishBtn.setBackgroundResource(R.drawable.button2)
                    // 리스트 뷰 색깔 회색으로 만들기
                    parent!!.getChildAt(position).setBackgroundColor(rgb(185,190,191))

                }
            }
            .addOnFailureListener {
            }


        // 완료 버튼을 누르면 finish를 true로 바꿔주는 코드
        view.finishBtn.setOnClickListener{
            db.collection("orders")
                .whereEqualTo("orderNum", Integer.parseInt(m.orderNum))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.get("menu")!!.equals(m.menu)) {
                            document.reference.update("finish", true)
                        }
                    }
                }
        }

        return view
    }

}