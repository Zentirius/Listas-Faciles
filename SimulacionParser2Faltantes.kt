// SIMULACIÓN MANUAL DEL PARSER - TEST DE LOS 2 PRODUCTOS FALTANTES

fun main() {
    println("🎯 SIMULACIÓN: BUSCAR LOS 2 PRODUCTOS FALTANTES")
    println("Ejecutando parser manualmente en la lista completa...")
    println("=".repeat(70))
    
    val listaCompleta = """
pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
Leche sin lactosa 2.
Mantequilla 1 barata.
2 lechugas francesas preguntar...cual son...
Carne molida más barata en bandeja
Tallarines 4 en oferta.
1malla tomates
1 huevo marca yemita o el más barato

 leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos


1. 2 kg de tomates, 1 lechuga y pan (el más barato)
6 huevos, 1 litro de leche no muy cara
media docena de plátanos. 3 manzanas
500gr de carne molida 1 paquete de espaguetis
Zanahorias
1.5 litros de jugo de naranja,,,2 latas de atún.



1.1papa mediana2.sandia grande 3.zapato grande

1.salmon 1kg
2.zanaorias


cinco tomates
ocho zanahorias

seis tomates ocho papas


crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
Shampoo anticaspa 1.
Jabón líquido
3 rollos papel higiénico preguntar...cuál es mejor...
Detergente en polvo más barato en caja
Esponjas 5 en oferta.
1bolsa arroz
1 aceite marca chef o el más económico

champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines

2. 3 kg de azúcar, 1 sal y vinagre (el más barato)
8 pilas, 2 metros de cable no muy caro
media docena de velas. 4 focos
750ml de alcohol 1 paquete de algodón
Papel aluminio
2.5 metros de cinta adhesiva,,,3 tubos de pegamento.

1.2metros cable 2.bombilla grande 3.zapallo chino

1.2metros de madera 3.8 metros de cable 
2.desodorantes
3.desinfectante

siete velas
diez pilas

cinco focos ocho rollos
    """.trimIndent()

    // Simulación manual del procesamiento línea por línea
    println("📋 ANÁLISIS LÍNEA POR LÍNEA:")
    val lineas = listaCompleta.split("\n").filter { it.isNotBlank() }
    
    var conteoProductos = 0
    val productosDetectados = mutableListOf<String>()
    
    lineas.forEachIndexed { i, linea ->
        println("\n${i+1}. '$linea'")
        
        when {
            linea.contains("pastelera") -> {
                println("   ✅ pastelera")
                productosDetectados.add("pastelera")
                conteoProductos++
            }
            linea.contains("Leche sin lactosa") -> {
                println("   ✅ leche sin lactosa")
                productosDetectados.add("leche sin lactosa")
                conteoProductos++
            }
            linea.contains("Mantequilla") -> {
                println("   ✅ mantequilla")
                productosDetectados.add("mantequilla")
                conteoProductos++
            }
            linea.contains("lechugas francesas") -> {
                println("   ✅ lechugas francesas (con pregunta)")
                productosDetectados.add("lechugas francesas")
                conteoProductos++
            }
            linea.contains("Carne molida") -> {
                println("   ✅ carne molida")
                productosDetectados.add("carne molida")
                conteoProductos++
            }
            linea.contains("Tallarines") -> {
                println("   ✅ tallarines")
                productosDetectados.add("tallarines")
                conteoProductos++
            }
            linea.contains("1malla tomates") -> {
                println("   ✅ tomates (malla)")
                productosDetectados.add("tomates")
                conteoProductos++
            }
            linea.contains("1 huevo") -> {
                println("   ✅ huevo")
                productosDetectados.add("huevo")
                conteoProductos++
            }
            linea.contains("leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos") -> {
                println("   ✅ leche asadas")
                println("   ✅ sandias (6)")
                println("   ✅ tomates (8)")
                println("   ✅ zanaorias (6)")
                println("   ✅ zapatos (5)")
                productosDetectados.addAll(listOf("leche asadas", "sandias", "tomates", "zanaorias", "zapatos"))
                conteoProductos += 5
            }
            linea.contains("2 kg de tomates, 1 lechuga y pan") -> {
                println("   ✅ tomates (2 kg)")
                println("   ✅ lechuga")
                println("   ✅ pan")
                productosDetectados.addAll(listOf("tomates", "lechuga", "pan"))
                conteoProductos += 3
            }
            linea.contains("6 huevos, 1 litro de leche") -> {
                println("   ✅ huevos (6)")
                println("   ✅ leche (1 litro)")
                productosDetectados.addAll(listOf("huevos", "leche"))
                conteoProductos += 2
            }
            linea.contains("media docena de plátanos") -> {
                println("   ✅ plátanos (6)")
                productosDetectados.add("plátanos")
                conteoProductos++
            }
            linea.contains("3 manzanas") -> {
                println("   ✅ manzanas")
                productosDetectados.add("manzanas")
                conteoProductos++
            }
            linea.contains("500gr de carne molida") -> {
                println("   ✅ carne molida (500gr)")
                productosDetectados.add("carne molida")
                conteoProductos++
            }
            linea.contains("1 paquete de espaguetis") -> {
                println("   ✅ espaguetis")
                productosDetectados.add("espaguetis")
                conteoProductos++
            }
            linea.contains("Zanahorias") -> {
                println("   ✅ zanahorias")
                productosDetectados.add("zanahorias")
                conteoProductos++
            }
            linea.contains("1.5 litros de jugo de naranja") -> {
                println("   ✅ jugo de naranja")
                productosDetectados.add("jugo de naranja")
                conteoProductos++
            }
            linea.contains("2 latas de atún") -> {
                println("   ✅ atún")
                productosDetectados.add("atún")
                conteoProductos++
            }
            linea.contains("1.1papa mediana2.sandia grande 3.zapato grande") -> {
                println("   ❓ papa mediana (1.1) - ¿Detectado?")
                println("   ❓ sandia grande (2) - ¿Detectado?")
                println("   ❓ zapato grande (3) - ¿Detectado?")
                // CASO PROBLEMÁTICO - puede fallar
            }
            linea.contains("1.salmon 1kg") -> {
                println("   ✅ salmon")
                productosDetectados.add("salmon")
                conteoProductos++
            }
            linea.contains("2.zanaorias") -> {
                println("   ✅ zanaorias (2)")
                productosDetectados.add("zanaorias")
                conteoProductos++
            }
            linea.contains("cinco tomates") -> {
                println("   ✅ tomates (cinco)")
                productosDetectados.add("tomates")
                conteoProductos++
            }
            linea.contains("ocho zanahorias") -> {
                println("   ✅ zanahorias (ocho)")
                productosDetectados.add("zanahorias")
                conteoProductos++
            }
            linea.contains("seis tomates ocho papas") -> {
                println("   ✅ tomates (seis)")
                println("   ✅ papas (ocho)")
                productosDetectados.addAll(listOf("tomates", "papas"))
                conteoProductos += 2
            }
            linea.contains("crema dental") -> {
                println("   ✅ crema dental")
                productosDetectados.add("crema dental")
                conteoProductos++
            }
            linea.contains("Shampoo") -> {
                println("   ✅ shampoo")
                productosDetectados.add("shampoo")
                conteoProductos++
            }
            linea.contains("Jabón líquido") -> {
                println("   ✅ jabón líquido")
                productosDetectados.add("jabón líquido")
                conteoProductos++
            }
            linea.contains("papel higiénico preguntar") -> {
                println("   ❓ papel higiénico - ¿Detectado con pregunta?")
                // SOSPECHOSO #1
            }
            linea.contains("Detergente") -> {
                println("   ✅ detergente")
                productosDetectados.add("detergente")
                conteoProductos++
            }
            linea.contains("Esponjas") -> {
                println("   ✅ esponjas")
                productosDetectados.add("esponjas")
                conteoProductos++
            }
            linea.contains("1bolsa arroz") -> {
                println("   ✅ arroz")
                productosDetectados.add("arroz")
                conteoProductos++
            }
            linea.contains("1 aceite") -> {
                println("   ✅ aceite")
                productosDetectados.add("aceite")
                conteoProductos++
            }
            linea.contains("4cepillos,12servilletas,3 desodorantes 7 calcetines") -> {
                println("   ❓ champú suave - ¿Detectado?")
                println("   ❓ cepillos (4) - ¿Detectado?")
                println("   ❓ servilletas (12) - ¿Detectado?")
                println("   ❓ desodorantes (3) - ¿Detectado?")
                println("   ❓ calcetines (7) - ¿Detectado?")
                // CASO PROBLEMÁTICO - números pegados
            }
            linea.contains("3 kg de azúcar") -> {
                println("   ✅ azúcar")
                productosDetectados.add("azúcar")
                conteoProductos++
            }
            linea.contains("1 sal y vinagre") -> {
                println("   ✅ sal")
                println("   ✅ vinagre")
                productosDetectados.addAll(listOf("sal", "vinagre"))
                conteoProductos += 2
            }
            linea.contains("8 pilas, 2 metros de cable") -> {
                println("   ✅ pilas")
                println("   ✅ cable")
                productosDetectados.addAll(listOf("pilas", "cable"))
                conteoProductos += 2
            }
            linea.contains("media docena de velas") -> {
                println("   ✅ velas")
                productosDetectados.add("velas")
                conteoProductos++
            }
            linea.contains("4 focos") -> {
                println("   ✅ focos")
                productosDetectados.add("focos")
                conteoProductos++
            }
            linea.contains("750ml de alcohol") -> {
                println("   ✅ alcohol")
                productosDetectados.add("alcohol")
                conteoProductos++
            }
            linea.contains("1 paquete de algodón") -> {
                println("   ✅ algodón")
                productosDetectados.add("algodón")
                conteoProductos++
            }
            linea.contains("Papel aluminio") -> {
                println("   ✅ papel aluminio")
                productosDetectados.add("papel aluminio")
                conteoProductos++
            }
            linea.contains("2.5 metros de cinta adhesiva") -> {
                println("   ✅ cinta adhesiva")
                productosDetectados.add("cinta adhesiva")
                conteoProductos++
            }
            linea.contains("3 tubos de pegamento") -> {
                println("   ✅ pegamento")
                productosDetectados.add("pegamento")
                conteoProductos++
            }
            linea.contains("1.2metros cable 2.bombilla grande 3.zapallo chino") -> {
                println("   ❓ cable (1.2 metros) - ¿Detectado?")
                println("   ❓ bombilla grande - ¿Detectado?")
                println("   ❓ zapallo chino - ¿Detectado?")
                // CASO PROBLEMÁTICO - numeración vs cantidades
            }
            linea.contains("1.2metros de madera") -> {
                println("   ✅ madera")
                productosDetectados.add("madera")
                conteoProductos++
            }
            linea.contains("3.8 metros de cable") -> {
                println("   ✅ cable (3.8m)")
                productosDetectados.add("cable")
                conteoProductos++
            }
            linea.contains("2.desodorantes") -> {
                println("   ✅ desodorantes")
                productosDetectados.add("desodorantes")
                conteoProductos++
            }
            linea.contains("3.desinfectante") -> {
                println("   ✅ desinfectante")
                productosDetectados.add("desinfectante")
                conteoProductos++
            }
            linea.contains("siete velas") -> {
                println("   ✅ velas (siete)")
                productosDetectados.add("velas")
                conteoProductos++
            }
            linea.contains("diez pilas") -> {
                println("   ✅ pilas (diez)")
                productosDetectados.add("pilas")
                conteoProductos++
            }
            linea.contains("cinco focos ocho rollos") -> {
                println("   ✅ focos (cinco)")
                println("   ❓ rollos (ocho) - ¿Detectado?")
                productosDetectados.add("focos")
                conteoProductos++
                // SOSPECHOSO #2 - rollos puede fallar
            }
            else -> {
                if (linea.trim().isNotEmpty()) {
                    println("   ⚠️ Línea no procesada")
                }
            }
        }
    }
    
    println("\n" + "=".repeat(70))
    println("📊 RESUMEN DE SIMULACIÓN:")
    println("   Productos detectados en simulación: $conteoProductos")
    println("   Productos reportados por la app: 68")
    println("   Diferencia: ${68 - conteoProductos}")
    
    println("\n🎯 CANDIDATOS PRINCIPALES PARA SER LOS 2 FALTANTES:")
    println("   1. papel higiénico (con pregunta intercalada)")
    println("   2. rollos (al final de línea con múltiples productos)")
    println("   3. Algún producto de: papa/sandia/zapato (línea problemática)")
    println("   4. Algún producto de: cepillos/servilletas/calcetines (números pegados)")
    println("   5. bombilla/zapallo (numeración confusa)")
    
    println("\n💡 RECOMENDACIÓN:")
    println("   Revisar específicamente el procesamiento de:")
    println("   - '3 rollos papel higiénico preguntar...cuál es mejor...'")
    println("   - 'cinco focos ocho rollos'")
    println("   - '4cepillos,12servilletas,3 desodorantes 7 calcetines'")
    println("   - '1.1papa mediana2.sandia grande 3.zapato grande'")
    println("   - '1.2metros cable 2.bombilla grande 3.zapallo chino'")
}

// Extension para repeat
operator fun String.times(n: Int): String = this.repeat(n)
