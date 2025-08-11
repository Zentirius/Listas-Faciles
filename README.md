o que m# 🛒 Lista Fácil Nueva

**Aplicación Android moderna para gestión inteligente de listas de compras**

## ✨ Características Principales

- 🧠 **Parser Inteligente**: Detecta automáticamente productos, cantidades y unidades
- 📷 **OCR Integrado**: Escanea listas manuscritas o impresas con la cámara
- 📱 **UI Moderna**: Jetpack Compose + Material Design 3
- ✅ **Gestión Completa**: Marcar, editar y eliminar productos
- 🎯 **Alta Precisión**: 85-90% de precisión en listas reales

## 🚀 Funcionalidades

### 📝 Agregar Manual
- Escribe productos individuales o listas completas
- Detección automática de cantidades y unidades
- Soporte para construcciones como "3 kg de azúcar", "1bolsa arroz"

### 📷 Escanear con OCR
- Escanea listas escritas a mano o impresas
- Procesamiento automático con ML Kit
- Conversión directa a productos organizados

### 🎨 Interfaz Intuitiva
- Campo de texto compacto con scroll
- Botones elegantes y funcionales
- Separación clara entre funciones manuales y OCR

## 🛠️ Tecnologías

- **Kotlin** + **Jetpack Compose**
- **Google ML Kit** para OCR
- **Material Design 3**
- **Android Gradle Plugin 8.13**
- **Java 17** compatible

## 📱 Capturas

*Próximamente: Screenshots de la aplicación funcionando*

## 🧪 Testing

El proyecto incluye tests unitarios completos que validan:
- Parsing de listas complejas
- Detección de cantidades y unidades
- Casos extremos y listas reales

## 🎯 Estado del Proyecto

✅ **Completado y funcional**
- Parser robusto validado con listas reales
- OCR integrado y funcionando
- UI moderna y responsive
- Tests unitarios completos

---

**Desarrollado con ❤️ para facilitar las compras diarias**
- Modularidad y tests unitarios

## Reglas de parsing soportadas
- Separación por salto de línea, comas, puntos, "y", "o" (cuando corresponde)
- Detección de marcas, notas, cantidades, unidades
- Manejo de palabras numéricas y heurística para casos ambiguos

## Estructura inicial
- `/ui` Pantallas y componentes Compose
- `/parser` Lógica de parsing
- `/ocr` OCR y cámara
- `/data` Persistencia Room
- `/model` Modelos de datos
- `/utils` Utilidades
