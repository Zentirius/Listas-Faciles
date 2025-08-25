package com.listafacilnueva

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.listafacilnueva.ui.MainScreen
import com.listafacilnueva.ui.theme.ListaFacilNuevaTheme

class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar logging para debug
        Log.d(TAG, "🚀 Iniciando MainActivity")
        
        enableEdgeToEdge()
        Log.d(TAG, "✅ UI configurada correctamente")
        
        setContent {
            ListaFacilNuevaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        
        Log.d(TAG, "✅ MainActivity iniciada correctamente")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "📱 MainActivity resumida")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "⏸️ MainActivity pausada")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🔚 MainActivity destruida")
    }
}
