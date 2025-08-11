public class TestListaExtremaFinal {
    public static void main(String[] args) {
        System.out.println("🧪 TEST FINAL - LISTA EXTREMA CON MEJORAS APLICADAS");
        System.out.println("================================================================================");
        
        // Los 5 casos más críticos de la lista extrema
        String[] casosExtremos = {
            "Yogur griego 2.5.",
            "yogur griego 2.5detergente", 
            "1.6litros leche2.5detergente3.8paños",
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas",
            "4cepillos12servilletas23desodorantes"
        };
        
        String[] descripciones = {
            "Decimal con punto final",
            "Decimales pegados", 
            "Múltiples productos pegados",
            "Separación compleja con comas",
            "Cantidades sin espacios"
        };
        
        int totalMejorados = 0;
        int totalProductos = 0;
        
        System.out.println("\n🔧 APLICANDO MEJORAS PASO A PASO:\n");
        
        for (int i = 0; i < casosExtremos.length; i++) {
            String casoOriginal = casosExtremos[i];
            
            System.out.println("CASO " + (i + 1) + ": " + descripciones[i]);
            System.out.println("📝 Original: '" + casoOriginal + "'");
            
            // Aplicar mejoras paso a paso
            String casoMejorado = aplicarMejorasCompletas(casoOriginal);
            
            // Separación simple para conteo
            String[] productosAntes = separarProductos(casoOriginal);
            String[] productosDespues = separarProductos(casoMejorado);
            
            System.out.println("🔴 ANTES: " + productosAntes.length + " productos");
            mostrarProductos("  ", productosAntes);
            
            System.out.println("🟢 DESPUÉS: " + productosDespues.length + " productos");
            if (!casoMejorado.equals(casoOriginal)) {
                System.out.println("  📋 Procesado: '" + casoMejorado + "'");
            }
            mostrarProductos("  ", productosDespues);
            
            // Estadísticas
            int mejora = productosDespues.length - productosAntes.length;
            if (mejora > 0) {
                totalMejorados++;
                System.out.println("📊 ✅ MEJORA: +" + mejora + " productos detectados");
            } else if (mejora == 0) {
                System.out.println("📊 🔄 NEUTRO: Misma cantidad");
            } else {
                System.out.println("📊 ❌ REGRESIÓN: " + mejora + " productos");
            }
            
            totalProductos += productosDespues.length;
            System.out.println("\n" + "─".repeat(70) + "\n");
        }
        
        System.out.println("🎯 RESUMEN FINAL:");
        System.out.println("✅ Casos mejorados: " + totalMejorados + "/" + casosExtremos.length);
        System.out.println("✅ Productos totales detectados: " + totalProductos);
        System.out.println("✅ Tasa de éxito: " + Math.round((totalMejorados * 100.0) / casosExtremos.length) + "%");
        
        if (totalMejorados == casosExtremos.length) {
            System.out.println("\n🎉 ¡TODOS LOS CASOS EXTREMOS RESUELTOS!");
            System.out.println("🚀 Las mejoras están listas para integrar en QuantityParser.kt");
        }
    }
    
    private static String aplicarMejorasCompletas(String texto) {
        String resultado = texto;
        boolean aplicoMejoras = false;
        
        // Mejora 1: Corregir decimales con punto final
        if (resultado.matches(".*\\d+\\.\\d+\\.$")) {
            resultado = resultado.replaceAll("\\.$", "");
            System.out.println("  ✅ Corregido punto final decimal");
            aplicoMejoras = true;
        }
        
        // Mejora 2: Separar decimales pegados
        String original = resultado;
        resultado = resultado.replaceAll("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{3,})", "$1, $2");
        if (!resultado.equals(original)) {
            System.out.println("  ✅ Decimales pegados separados");
            aplicoMejoras = true;
        }
        
        // Mejora 3: Separar productos consecutivos
        original = resultado;
        resultado = resultado.replaceAll("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+)", "$1 $2, $3 $4");
        if (!resultado.equals(original)) {
            System.out.println("  ✅ Productos consecutivos separados");
            aplicoMejoras = true;
        }
        
        // Mejora 4: Separar cantidades pegadas sin decimales
        original = resultado;
        resultado = resultado.replaceAll("(\\d+)([a-zA-Záéíóúüñ]{3,})(\\d+)([a-zA-Záéíóúüñ]{3,})", "$1 $2, $3 $4");
        if (!resultado.equals(original)) {
            System.out.println("  ✅ Cantidades pegadas separadas");
            aplicoMejoras = true;
        }
        
        if (!aplicoMejoras) {
            System.out.println("  ℹ️ No requirió mejoras");
        }
        
        return resultado;
    }
    
    private static String[] separarProductos(String texto) {
        String[] productos = texto.split(",");
        // Filtrar productos válidos
        String[] productosValidos = new String[productos.length];
        int count = 0;
        for (String producto : productos) {
            String limpio = producto.trim();
            if (!limpio.isEmpty() && limpio.length() >= 3) {
                productosValidos[count++] = limpio;
            }
        }
        
        // Crear array del tamaño correcto
        String[] resultado = new String[count];
        System.arraycopy(productosValidos, 0, resultado, 0, count);
        return count > 0 ? resultado : new String[]{texto.trim()};
    }
    
    private static void mostrarProductos(String prefijo, String[] productos) {
        if (productos.length == 0 || (productos.length == 1 && productos[0].trim().isEmpty())) {
            System.out.println(prefijo + "❌ No se detectaron productos");
        } else {
            for (int i = 0; i < productos.length; i++) {
                String producto = productos[i].trim();
                if (!producto.isEmpty()) {
                    String display = producto.length() > 35 ? producto.substring(0, 35) + "..." : producto;
                    System.out.println(prefijo + (i + 1) + ". '" + display + "'");
                }
            }
        }
    }
}
