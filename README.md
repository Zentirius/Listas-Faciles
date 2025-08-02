# Lista Fácil Nueva

Proyecto Android moderno, limpio y modular para gestionar listas de compras con parser avanzado, OCR y cámara.

## Características principales
- UI Jetpack Compose
- Parser robusto para listas desordenadas
- OCR con CameraX + ML Kit
- Persistencia local con Room
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
