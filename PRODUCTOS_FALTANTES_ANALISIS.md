🔍 ANÁLISIS DE PRODUCTOS FALTANTES - LISTA COMPLETA DEL USUARIO

📋 PRODUCTOS QUE EL PARSER NO ESTÁ DETECTANDO:

❌ PRODUCTOS CRÍTICOS NO DETECTADOS:

1. **lechugas francesas** (de: "2 lechugas francesas preguntar...cual son...")
   ➜ PROBLEMA: El texto "preguntar...cual son..." confunde al parser y lo hace descartar la línea
   ➜ SOLUCIÓN: Mejorar la función que limpia texto problemático

2. **leche asadas** (de: "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos")
   ➜ PROBLEMA: Línea muy compleja con múltiples productos pegados
   ➜ SOLUCIÓN: Mejorar separación de productos múltiples

3. **sandias** (de: "6sandias,8tomates,6 zanaorias 5 zapatos")
   ➜ PROBLEMA: Números pegados directamente a palabras sin espacios
   ➜ SOLUCIÓN: Regex para separar "6sandias" → "6 sandias"

4. **zanaorias** (de: "6sandias,8tomates,6 zanaorias 5 zapatos")
   ➜ PROBLEMA: Mismo problema que sandias
   ➜ SOLUCIÓN: Separar "6 zanaorias"

5. **zapatos** (de: "6sandias,8tomates,6 zanaorias 5 zapatos")
   ➜ PROBLEMA: Número pegado "5 zapatos"
   ➜ SOLUCIÓN: Separar correctamente

6. **papa** (de: "1.1papa mediana2.sandia grande 3.zapato grande")
   ➜ PROBLEMA: Decimales pegados a palabras "1.1papa"
   ➜ SOLUCIÓN: Regex para separar "1.1papa" → "1.1 papa"

7. **sandia** (de: "1.1papa mediana2.sandia grande 3.zapato grande")
   ➜ PROBLEMA: Múltiples productos pegados sin separadores
   ➜ SOLUCIÓN: Separar "mediana2.sandia" → "mediana, 2.sandia"

8. **zapato** (de: "1.1papa mediana2.sandia grande 3.zapato grande")
   ➜ PROBLEMA: Numeración de lista vs cantidad
   ➜ SOLUCIÓN: Detectar "3.zapato" como "3 zapato" no como numeración

9. **papel higiénico** (de: "3 rollos papel higiénico preguntar...cuál es mejor...")
   ➜ PROBLEMA: Texto problemático "preguntar...cuál es mejor..." 
   ➜ SOLUCIÓN: Limpiar antes de descartar

10. **cepillos** (de: "4cepillos,12servilletas,3 desodorantes 7 calcetines")
    ➜ PROBLEMA: Números pegados sin espacios
    ➜ SOLUCIÓN: Separar "4cepillos" → "4 cepillos"

11. **servilletas** (de: "4cepillos,12servilletas,3 desodorantes 7 calcetines")
    ➜ PROBLEMA: Mismo problema, múltiples productos pegados
    ➜ SOLUCIÓN: Separar "12servilletas" → "12 servilletas"

12. **calcetines** (de: "4cepillos,12servilletas,3 desodorantes 7 calcetines")
    ➜ PROBLEMA: "7 calcetines" al final de línea compleja
    ➜ SOLUCIÓN: Mejorar separación de productos múltiples

13. **cable** (de: "1.2metros cable 2.bombilla grande 3.zapallo chino")
    ➜ PROBLEMA: Confusión entre numeración y cantidades decimales
    ➜ SOLUCIÓN: Detectar "1.2metros cable" como cantidad+unidad+producto

14. **bombilla** (de: "1.2metros cable 2.bombilla grande 3.zapallo chino")
    ➜ PROBLEMA: "2.bombilla" podría ser numeración o cantidad
    ➜ SOLUCIÓN: Contexto indica numeración, extraer correctamente

15. **zapallo chino** (de: "1.2metros cable 2.bombilla grande 3.zapallo chino")
    ➜ PROBLEMA: "3.zapallo" en contexto de numeración
    ➜ SOLUCIÓN: Extraer como "zapallo chino"

16. **madera** (de: "1.2metros de madera 3.8 metros de cable")
    ➜ PROBLEMA: "1.2metros de madera" no se separa correctamente
    ➜ SOLUCIÓN: Reconocer patrón cantidad+unidad+de+producto

17. **rollos** (de: "cinco focos ocho rollos")
    ➜ PROBLEMA: Múltiples productos en una línea con palabras numéricas
    ➜ SOLUCIÓN: Separar "ocho rollos" después de "cinco focos"

📊 RESUMEN DE PROBLEMAS PRINCIPALES:

1. **Números pegados a palabras** (6sandias, 4cepillos, 12servilletas, 1.1papa)
2. **Texto problemático** (preguntar...cual son..., preguntar...cuál es mejor...)
3. **Productos múltiples en una línea** sin separadores claros
4. **Confusión numeración vs cantidades** (1.2metros vs 2.bombilla)
5. **Palabras numéricas** no separadas correctamente (cinco focos ocho rollos)

🎯 PRIORIDADES DE CORRECCIÓN:

ALTA PRIORIDAD:
- Separar números pegados a palabras
- Limpiar texto problemático antes de descartar
- Mejorar separación de productos múltiples

MEDIA PRIORIDAD:  
- Distinguir numeración vs cantidades decimales
- Separar productos con palabras numéricas

TOTAL PRODUCTOS FALTANTES: ~17 productos críticos
OBJETIVO: Llegar a 70+ productos detectados
