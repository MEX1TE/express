package com.example.express

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.express.databinding.ItemCartBinding
import com.example.express.model.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.cartItemName.text = cartItem.product.name
            binding.cartItemQuantity.text = "x${cartItem.quantity}"
            binding.cartItemPrice.text = "${String.format("%.2f", cartItem.product.price * cartItem.quantity)} ₽"
            Log.d("CartAdapter", "Binding item: ${cartItem.product.name}, quantity: ${cartItem.quantity}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount() = cartItems.size

    fun updateCart(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
        Log.d("CartAdapter", "Cart updated with ${newCartItems.size} items")
    }
}