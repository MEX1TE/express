package com.example.express

// import android.content.Context // Больше не нужен напрямую здесь
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // <-- Возвращаем импорт Glide
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

            // Возвращаем загрузку изображения с помощью Glide
            product.imageUrl?.let { url ->
                if (url.isNotBlank()) { // Добавим проверку, что URL не пустой
                    Glide.with(binding.productImage.context)
                        .load(url)
                        .placeholder(R.drawable.ic_placeholder) // Убедитесь, что ic_placeholder существует
                        .error(R.drawable.ic_placeholder)       // или используйте стандартный Android ресурс
                        .into(binding.productImage)
                    Log.d("ProductAdapter", "Loading image from URL: $url")
                } else {
                    binding.productImage.setImageResource(R.drawable.ic_placeholder)
                    Log.d("ProductAdapter", "Image URL is blank for product: ${product.name}")
                }
            } ?: run {
                binding.productImage.setImageResource(R.drawable.ic_placeholder)
                Log.d("ProductAdapter", "Image URL is null for product: ${product.name}")
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
        Log.d("ProductAdapter", "Updating products with URLs: $newProducts")
        products = newProducts
        notifyDataSetChanged()
    }
}