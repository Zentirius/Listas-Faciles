🔍 PRODUCTOS PROBLEMÁTICOS ESPECÍFICOS - ANÁLISIS DETALLADO

📋 LÍNEAS CON TEXTO PROBLEMÁTICO:

1. **"2 lechugas francesas preguntar...cual son..."**
   ➜ Producto esperado: "lechugas francesas" (cantidad: 2)
   ➜ Problema: El texto "preguntar...cual son..." puede hacer que el parser descarte la línea
   ➜ ¿Se detecta?: Verificar si aparece "lechugas" o "francesas" en la lista

2. **"3 rollos papel higiénico preguntar...cuál es mejor..."**
   ➜ Producto esperado: "papel higiénico" (cantidad: 3, tipo: rollos)
   ➜ Problema: El texto "preguntar...cuál es mejor..." puede hacer que el parser descarte la línea
   ➜ ¿Se detecta?: Verificar si aparece "papel" o "higiénico" en la lista

📋 LÍNEAS CON PRODUCTOS MÚLTIPLES PEGADOS:

3. **"champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines"**
   ➜ Productos esperados:
     - champú suave
     - cepillos (cantidad: 4)
     - servilletas (cantidad: 12)  
     - desodorantes (cantidad: 3)
     - calcetines (cantidad: 7)
   ➜ Problema: Números pegados sin espacios y múltiples productos en una línea
   ➜ ¿Se detecta?: Verificar cuántos de estos 5 productos aparecen

4. **"leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos"**
   ➜ Productos esperados:
     - leche asadas
     - sandias (cantidad: 6)
     - tomates (cantidad: 8)
     - zanaorias (cantidad: 6)
     - zapatos (cantidad: 5)
   ➜ Problema: Números pegados sin espacios
   ➜ ¿Se detecta?: Verificar cuántos de estos 5 productos aparecen

📋 LÍNEAS CON NUMERACIÓN COMPLEJA:

5. **"1.1papa mediana2.sandia grande 3.zapato grande"**
   ➜ Productos esperados:
     - papa mediana (cantidad: 1.1)
     - sandia grande (cantidad: 2)
     - zapato grande (cantidad: 3)
   ➜ Problema: Decimales pegados y numeración mixta
   ➜ ¿Se detecta?: Verificar si aparece "papa", "sandia", "zapato"

6. **"1.2metros cable 2.bombilla grande 3.zapallo chino"**
   ➜ Productos esperados:
     - cable (cantidad: 1.2, unidad: metros)
     - bombilla grande (cantidad: 2)
     - zapallo chino (cantidad: 3)
   ➜ Problema: Confusión entre cantidades decimales y numeración
   ➜ ¿Se detecta?: Verificar si aparece "cable", "bombilla", "zapallo"

7. **"1.2metros de madera 3.8 metros de cable"**
   ➜ Productos esperados:
     - madera (cantidad: 1.2, unidad: metros)
     - cable (cantidad: 3.8, unidad: metros)
   ➜ Problema: Cantidades decimales con "de" intermedio
   ➜ ¿Se detecta?: Verificar si aparece "madera" (cable ya contado)

📋 LÍNEAS CON PALABRAS NUMÉRICAS:

8. **"cinco focos ocho rollos"**
   ➜ Productos esperados:
     - focos (cantidad: cinco = 5)
     - rollos (cantidad: ocho = 8)
   ➜ Problema: Múltiples productos con cantidades en palabras
   ➜ ¿Se detecta?: Verificar si aparece "focos", "rollos"

🎯 PRODUCTOS MÁS PROBABLES PARA SER LOS 2 FALTANTES:

**CANDIDATO #1: "lechugas francesas"** 
- Línea: "2 lechugas francesas preguntar...cual son..."
- Razón: Texto problemático puede hacer que se descarte completamente

**CANDIDATO #2: "papel higiénico"**
- Línea: "3 rollos papel higiénico preguntar...cuál es mejor..."  
- Razón: Texto problemático puede hacer que se descarte completamente

**CANDIDATOS ALTERNATIVOS:**
- Alguno de los productos de "4cepillos,12servilletas,3 desodorantes 7 calcetines"
- "bombilla" o "zapallo" de "1.2metros cable 2.bombilla grande 3.zapallo chino"
- "rollos" de "cinco focos ocho rollos"

📝 PARA VERIFICAR:
Ejecuta el parser y busca específicamente estos nombres en la lista de productos detectados:
- "lechugas" o "francesas"
- "papel" o "higiénico" 
- "cepillos", "servilletas", "calcetines"
- "bombilla", "zapallo"
- "rollos" (al final)
