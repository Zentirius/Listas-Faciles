package com.listafacilnueva.parser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u001cB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005H\u0002J\u0010\u0010\u000b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\u0010\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0005H\u0002J\u0010\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\u0010\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\u0010\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0005H\u0002J\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00042\u0006\u0010\u0011\u001a\u00020\u0005J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u0005H\u0002J\u0016\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\n\u001a\u00020\u0005H\u0002J\u0018\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005H\u0002\u00a8\u0006\u001d"}, d2 = {"Lcom/listafacilnueva/parser/QuantityParser;", "", "()V", "dividirEnFragmentos", "", "", "linea", "dividirPorSeparadores", "esFragmentoBasura", "", "fragmento", "esLineaBasura", "esProductoValido", "producto", "Lcom/listafacilnueva/model/Producto;", "extraerCantidadYUnidad", "Lcom/listafacilnueva/parser/QuantityParser$Extraccion;", "texto", "limpiarNumeracionCompuesta", "limpiarNumeracionLista", "normalizarNumeros", "parse", "procesarFragmento", "procesarLineaConMarca", "separarMultiplesProductos", "tieneCantidadesSeparadas", "parte1", "parte2", "Extraccion", "app_debug"})
public final class QuantityParser {
    @org.jetbrains.annotations.NotNull()
    public static final com.listafacilnueva.parser.QuantityParser INSTANCE = null;
    
    private QuantityParser() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.listafacilnueva.model.Producto> parse(@org.jetbrains.annotations.NotNull()
    java.lang.String texto) {
        return null;
    }
    
    /**
     * MEJORA SUGERIDA POR AMIGO: Detecta y separa patrones como 1.2metros cable2.bombilla grande
     * Transforma: "1.2metros cable2.bombilla grande" → "2 metros cable, bombilla grande"
     */
    private final java.lang.String limpiarNumeracionCompuesta(java.lang.String linea) {
        return null;
    }
    
    private final java.lang.String limpiarNumeracionLista(java.lang.String linea) {
        return null;
    }
    
    private final java.util.List<java.lang.String> dividirEnFragmentos(java.lang.String linea) {
        return null;
    }
    
    private final java.util.List<java.lang.String> dividirPorSeparadores(java.lang.String linea) {
        return null;
    }
    
    private final boolean tieneCantidadesSeparadas(java.lang.String parte1, java.lang.String parte2) {
        return false;
    }
    
    private final java.util.List<java.lang.String> procesarLineaConMarca(java.lang.String linea) {
        return null;
    }
    
    private final java.util.List<java.lang.String> separarMultiplesProductos(java.lang.String fragmento) {
        return null;
    }
    
    private final com.listafacilnueva.model.Producto procesarFragmento(java.lang.String fragmento) {
        return null;
    }
    
    private final com.listafacilnueva.parser.QuantityParser.Extraccion extraerCantidadYUnidad(java.lang.String texto) {
        return null;
    }
    
    private final boolean esLineaBasura(java.lang.String linea) {
        return false;
    }
    
    private final boolean esFragmentoBasura(java.lang.String fragmento) {
        return false;
    }
    
    private final boolean esProductoValido(com.listafacilnueva.model.Producto producto) {
        return false;
    }
    
    private final java.lang.String normalizarNumeros(java.lang.String texto) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B!\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\tJ\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J0\u0010\u0011\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0012J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0005H\u00d6\u0001R\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f\u00a8\u0006\u0019"}, d2 = {"Lcom/listafacilnueva/parser/QuantityParser$Extraccion;", "", "cantidad", "", "unidad", "", "nombre", "(Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;)V", "getCantidad", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getNombre", "()Ljava/lang/String;", "getUnidad", "component1", "component2", "component3", "copy", "(Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;)Lcom/listafacilnueva/parser/QuantityParser$Extraccion;", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    static final class Extraccion {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Double cantidad = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String unidad = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String nombre = null;
        
        public Extraccion(@org.jetbrains.annotations.Nullable()
        java.lang.Double cantidad, @org.jetbrains.annotations.Nullable()
        java.lang.String unidad, @org.jetbrains.annotations.NotNull()
        java.lang.String nombre) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Double getCantidad() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getUnidad() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getNombre() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Double component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.listafacilnueva.parser.QuantityParser.Extraccion copy(@org.jetbrains.annotations.Nullable()
        java.lang.Double cantidad, @org.jetbrains.annotations.Nullable()
        java.lang.String unidad, @org.jetbrains.annotations.NotNull()
        java.lang.String nombre) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}