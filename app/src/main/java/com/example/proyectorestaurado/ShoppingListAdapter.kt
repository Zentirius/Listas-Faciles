package com.example.proyectorestaurado

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectorestaurado.databinding.ItemShoppingBinding
import com.example.proyectorestaurado.data.PriceRange
import com.example.proyectorestaurado.data.ShoppingItem
import kotlin.math.min

class ShoppingListAdapter(
    val onCheckChanged: (ShoppingItem, Boolean) -> Unit,
    val onItemLongClick: (ShoppingItem) -> Unit
) : ListAdapter<ShoppingItem, ShoppingListAdapter.ShoppingViewHolder>(DiffCallback()) {
    
    init {
        Log.d("ShoppingAdapter", "Inicializando ShoppingListAdapter")
    }
    
    override fun submitList(list: List<ShoppingItem>?) {
        try {
            Log.d("ShoppingAdapter", "Recibiendo nueva lista: ${list?.size ?: 0} items")
            if (list != null) {
                for (i in list.indices.take(min(5, list.size))) {
                    Log.d("ShoppingAdapter", "Item $i: ${list[i].id} - ${list[i].name}")
                }
                if (list.size > 5) {
                    Log.d("ShoppingAdapter", "... y ${list.size - 5} items más")
                }
            } else {
                Log.d("ShoppingAdapter", "Lista nula recibida")
            }
            super.submitList(list)
            Log.d("ShoppingAdapter", "Lista enviada al adaptador base")
        } catch (e: Exception) {
            Log.e("ShoppingAdapter", "Error en submitList: ${e.message}")
            Log.e("ShoppingAdapter", "StackTrace: ${e.stackTraceToString()}")
            // Intentar enviar la lista de todos modos
            super.submitList(list)
        }
    }
    
    class ShoppingViewHolder(private val binding: ItemShoppingBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingItem, onCheckChanged: (ShoppingItem, Boolean) -> Unit, onItemLongClick: (ShoppingItem) -> Unit) {
            binding.apply {
                // 1. Configurar nombre y cantidad
                val quantityText = item.quantity?.let { qty ->
                    if (qty % 1.0 == 0.0) "(${qty.toInt()} ${item.unit ?: ""})".trim() else "($qty ${item.unit ?: ""})".trim()
                } ?: ""
                textItemName.text = "${item.name} $quantityText".trim()

                // 2. Configurar marca (visible solo si existe)
                textBrand.visibility = if (!item.brand.isNullOrBlank()) {
                    textBrand.text = item.brand
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // 3. Configurar notas (visible solo si existen)
                textNotes.visibility = if (!item.notes.isNullOrBlank()) {
                    textNotes.text = item.notes
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // 4. Configurar el indicador de rango de precios (icono y color)
                val priceRangeIcon = when (item.priceRange) {
                    PriceRange.ECONOMY -> R.drawable.ic_price_economy
                    PriceRange.NORMAL -> R.drawable.ic_price_normal
                    PriceRange.PREMIUM -> R.drawable.ic_price_premium
                }
                iconPriceRange.setImageResource(priceRangeIcon)

                val priceRangeColor = when (item.priceRange) {
                    PriceRange.ECONOMY -> R.color.price_economy
                    PriceRange.NORMAL -> R.color.price_normal
                    PriceRange.PREMIUM -> R.color.price_premium
                }
                iconPriceRange.setColorFilter(androidx.core.content.ContextCompat.getColor(root.context, priceRangeColor))

                // 5. Configurar estado del checkbox y listeners
                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = item.isChecked
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onCheckChanged(item, isChecked)
                }

                // 6. Configurar listener del botón de edición
                buttonEdit.setOnClickListener {
                    onItemLongClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        try {
            Log.d("ShoppingAdapter", "Creando nuevo ViewHolder")
            val inflater = LayoutInflater.from(parent.context)
            Log.d("ShoppingAdapter", "Inflater obtenido correctamente")
            
            val binding = ItemShoppingBinding.inflate(inflater, parent, false)
            Log.d("ShoppingAdapter", "Binding inflado correctamente")
            
            val viewHolder = ShoppingViewHolder(binding)
            Log.d("ShoppingAdapter", "ViewHolder creado correctamente")
            return viewHolder
        } catch (e: Exception) {
            Log.e("ShoppingAdapter", "Error en onCreateViewHolder: ${e.message}")
            Log.e("ShoppingAdapter", "StackTrace: ${e.stackTraceToString()}")
            // Intentar crear un ViewHolder de emergencia
            val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ShoppingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        try {
            Log.d("ShoppingAdapter", "Vinculando ViewHolder en posición $position")
            val item = getItem(position)
            Log.d("ShoppingAdapter", "Item obtenido: ${item.id} - ${item.name}")
            holder.bind(item, onCheckChanged, onItemLongClick)
            Log.d("ShoppingAdapter", "ViewHolder vinculado correctamente en posición $position")
        } catch (e: Exception) {
            Log.e("ShoppingAdapter", "Error en onBindViewHolder para posición $position: ${e.message}")
            Log.e("ShoppingAdapter", "StackTrace: ${e.stackTraceToString()}")
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem == newItem
        }
    }
}
