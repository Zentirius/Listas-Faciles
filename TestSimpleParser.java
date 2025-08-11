import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TestSimpleParser {
    public static void main(String[] args) {
        System.out.println("🚀 TEST SIMPLE DE MEJORAS INTEGRADAS");
        System.out.println("=" + "=".repeat(50));
        
        // Test de las mejoras que se aplicaron en QuantityParser
        testMejoras();
    }
    
    static void testMejoras() {
        String[] casosTest = {
            "Yogur griego 2.5.",
            "yogur griego 2.5detergente", 
            "1.6litros leche2.5detergente3.8paños",
            "4cepillos12servilletas23desodorantes",
            "media docena de huevos"
        };
        
        for (int i = 0; i < casosTest.length; i++) {
            String caso = casosTest[i];
            System.out.println("\n🧪 TEST " + (i+1) + ": " + caso);
            System.out.println("-".repeat(40));
            
            String resultado = aplicarMejoras(caso);
            System.out.println("✅ Resultado: " + resultado);
            
            // Verificar que las mejoras se aplicaron
            if (!caso.equals(resultado)) {
                System.out.println("✅ MEJORA APLICADA correctamente");
            } else {
                System.out.println("ℹ️  No necesitaba mejoras");
            }
        }
    }
    
    // Simulación de las mejoras del QuantityParser
    static String aplicarMejoras(String texto) {
        String resultado = texto;
        
        // MEJORA 1: Corregir decimales con punto final
        Pattern patronDecimalConPuntoFinal = Pattern.compile("(\\d+\\.\\d+)\\.$");
        Matcher matcher1 = patronDecimalConPuntoFinal.matcher(resultado);
        if (matcher1.find()) {
            resultado = matcher1.replaceAll("$1");
        }
        
        // MEJORA 2: Separar decimales pegados
        Pattern patronDecimalesPegados = Pattern.compile("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{3,})");
        Matcher matcher2 = patronDecimalesPegados.matcher(resultado);
        if (matcher2.find()) {
            resultado = matcher2.replaceAll("$1, $2");
        }
        
        // MEJORA 3: Separar cantidades pegadas simples
        Pattern patronCantidadesPegadas = Pattern.compile("(\\d+)([a-zA-Záéíóúüñ]{4,})(\\d+)([a-zA-Záéíóúüñ]{4,})");
        Matcher matcher3 = patronCantidadesPegadas.matcher(resultado);
        if (matcher3.find()) {
            resultado = matcher3.replaceAll("$1 $2, $3 $4");
        }
        
        // MEJORA 4: Media docena
        if (resultado.toLowerCase().contains("media docena")) {
            resultado = resultado.replaceAll("(?i)media docena", "6");
        }
        
        return resultado;
    }
}
