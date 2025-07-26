package com.example.proyectorestaurado

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectorestaurado.CameraActivity
import com.example.proyectorestaurado.data.ShoppingItem
import com.example.proyectorestaurado.databinding.ActivityMainBinding
import com.example.proyectorestaurado.ui.ShoppingViewModel
import com.example.proyectorestaurado.utils.ParsedItem
import com.example.proyectorestaurado.utils.QuantityParser
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val quantityParser = QuantityParser()

    private val ocrResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val parsedItems = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableArrayListExtra("ocr_results_list", ParsedItem::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableArrayListExtra<ParsedItem>("ocr_results_list")
            }

            parsedItems?.let {
                if (it.isNotEmpty()) {
                    val shoppingItems = it.mapNotNull { parsed ->
                        if (parsed.name.isNotBlank()) {
                            ShoppingItem(
                                name = parsed.name,
                                quantity = parsed.quantity,
                                unit = parsed.unit,
                                brand = parsed.brand,
                                notes = parsed.notes,
                                priceRange = parsed.priceRange,
                                category = currentCategory,
                                isChecked = false
                            )
                        } else null
                    }
                    viewModel.addItems(shoppingItems)
                    Toast.makeText(this, "${shoppingItems.size} items añadidos desde el escaneo.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private val viewModel: ShoppingViewModel by viewModels()
    private val adapter = ShoppingListAdapter(
        onCheckChanged = { item, checked ->
            viewModel.updateItem(item.copy(isChecked = checked))
        },
        onItemLongClick = { item ->
            showEditItemDialog(item)
        }
    )
    private var currentCategory: String = "General"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddButton()
        setupDeleteCheckedButton()
        setupDeleteAllButton()
        setupCategorySpinner()
        setupSearch()
        setupOcrButton()
        observeItems()
        setupAds()
    }

    private fun setupOcrButton() {
        binding.fabOcr.setOnClickListener {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        ocrResultLauncher.launch(intent)
    }

    private fun setupAds() {
        try {
            MobileAds.initialize(this) {}
            // AdView removed from layout
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al inicializar anuncios: ${e.message}")
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAddButton() {
        binding.buttonAdd.setOnClickListener {
            val inputText = binding.editTextItem.text.toString()
            if (inputText.isNotBlank()) {
                val lines = inputText.split(Regex("[\n]+"))
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                val itemsToAdd = mutableListOf<ShoppingItem>()
                
                lines.forEach { line ->
                    Log.d("MainActivity", "Procesando línea: '$line'")
                    // Usar parseMultipleItems para detectar múltiples productos en una línea
                    val parsedItems = quantityParser.parseMultipleItems(line)
                    Log.d("MainActivity", "parseMultipleItems devolvió ${parsedItems.size} productos")
                    parsedItems.forEachIndexed { index, parsed ->
                        Log.d("MainActivity", "Producto ${index + 1}: '${parsed.name}' (${parsed.quantity} ${parsed.unit ?: "u"})")
                        itemsToAdd.add(
                            ShoppingItem(
                                name = parsed.name,
                                quantity = parsed.quantity,
                                unit = parsed.unit,
                                brand = parsed.brand,
                                notes = parsed.notes,
                                priceRange = parsed.priceRange,
                                category = currentCategory,
                                isChecked = false
                            )
                        )
                    }
                }

                if (itemsToAdd.isNotEmpty()) {
                    viewModel.addItems(itemsToAdd)
                    binding.editTextItem.text?.clear()
                    // Ocultar teclado
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editTextItem.windowToken, 0)
                    Toast.makeText(this, "${itemsToAdd.size} productos añadidos.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se pudo procesar la entrada.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupDeleteCheckedButton() {
        binding.buttonDeleteChecked.setOnClickListener {
            viewModel.deleteCheckedItems()
        }
    }

    private fun setupDeleteAllButton() {
        binding.buttonDeleteAll.setOnClickListener {
            showDeleteAllConfirmationDialog()
        }
    }

    private fun showDeleteAllConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar todos los artículos de la lista?")
            .setPositiveButton("Borrar Todo") { _, _ ->
                viewModel.deleteAllItems()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditItemDialog(item: ShoppingItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_item, null)
        val editTextName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextName)
        val editTextQuantity = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextQuantity)
        val editTextUnit = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextUnit)

        editTextName.setText(item.name)
        editTextQuantity.setText(item.quantity?.toString() ?: "")
        editTextUnit.setText(item.unit ?: "")

        AlertDialog.Builder(this)
            .setTitle("Editar Artículo")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = editTextName.text.toString()
                val newQuantity = editTextQuantity.text.toString().toDoubleOrNull()
                val newUnit = editTextUnit.text.toString()

                if (newName.isNotBlank()) {
                    val updatedItem = item.copy(
                        name = newName,
                        quantity = newQuantity,
                        unit = if (newUnit.isNotBlank()) newUnit else null
                    )
                    viewModel.updateItem(updatedItem)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.categories)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = categories[position]
                updateItemsList(currentCategory)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            updateItemsList(currentCategory)
        } else {
            viewModel.searchItems("%$query%").observe(this, Observer { items ->
                adapter.submitList(items)
                updateVisibility(items.isEmpty())
            })
        }
    }

    private fun observeItems() {
        updateItemsList("General")
    }

    private fun updateItemsList(category: String) {
        Log.d("MainActivity", "updateItemsList called with category: $category")
        viewModel.allItems.removeObservers(this) // Evitar observadores duplicados
        val liveData = if (category == "General") {
            Log.d("MainActivity", "Using allItems LiveData")
            viewModel.allItems
        } else {
            Log.d("MainActivity", "Using getItemsByCategory LiveData for: $category")
            viewModel.getItemsByCategory(category)
        }
        liveData.observe(this, Observer { items ->
            Log.d("MainActivity", "LiveData observer triggered with ${items?.size ?: 0} items")
            items?.forEachIndexed { index, item ->
                Log.d("MainActivity", "Item $index: ${item.id} - ${item.name} (${item.quantity} ${item.unit ?: "u"}) - Category: ${item.category}")
            }
            adapter.submitList(items)
            updateVisibility(items?.isEmpty() ?: true)
        })
    }

    private fun updateVisibility(isEmpty: Boolean) {
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}