package com.example.express

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.express.databinding.ItemProductBinding
import com.example.express.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = "${product.price} ₽"
            binding.productDescription.text = product.description ?: "Без описания"

            // Загрузка изображения с помощью Glide
            product.imageUrl?.let { url ->
                Glide.with(binding.productImage.context)
                    .load(url)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(binding.productImage)
            } ?: run {
                binding.productImage.setImageResource(R.drawable.ic_placeholder)
            }

            binding.addToCartButton.setOnClickListener {
                onAddToCart(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        Log.d("ProductAdapter", "Updating products: $newProducts")
        products = newProducts
        notifyDataSetChanged()
    }
}