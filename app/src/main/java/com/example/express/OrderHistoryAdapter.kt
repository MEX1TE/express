package com.example.express

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.express.model.OrderHistoryEntry
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderHistoryAdapter(private var orders: List<OrderHistoryEntry>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    // Для форматирования даты из строки ISO 8601 в более читаемый вид
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC") // Если сервер отдает в UTC
    }
    private val outputDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<OrderHistoryEntry>) {
        orders = newOrders
        notifyDataSetChanged() // Для простоты, в реальном приложении лучше использовать DiffUtil
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewOrderId: TextView = itemView.findViewById(R.id.textViewOrderId)
        private val textViewOrderDate: TextView = itemView.findViewById(R.id.textViewOrderDate)
        private val textViewOrderTotal: TextView = itemView.findViewById(R.id.textViewOrderTotal)
        private val textViewOrderAddress: TextView = itemView.findViewById(R.id.textViewOrderAddress)
        private val textViewOrderItemsCount: TextView = itemView.findViewById(R.id.textViewOrderItemsCount)

        fun bind(order: OrderHistoryEntry) {
            textViewOrderId.text = order.id.toString()
            try {
                val date = inputDateFormat.parse(order.createdAt)
                textViewOrderDate.text = date?.let { outputDateFormat.format(it) } ?: order.createdAt
            } catch (e: Exception) {
                textViewOrderDate.text = order.createdAt // Показать как есть, если парсинг не удался
            }
            textViewOrderTotal.text = String.format(Locale.getDefault(), "%.2f руб.", order.total)
            textViewOrderAddress.text = order.address
            textViewOrderItemsCount.text = "Товаров: ${order.items.size}"
        }
    }
} 