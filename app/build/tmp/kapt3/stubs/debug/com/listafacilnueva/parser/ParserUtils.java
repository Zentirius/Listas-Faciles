package com.listafacilnueva.parser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0005J\u000e\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0005J\u000e\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u0005R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u001d\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0007\u00a8\u0006\u0016"}, d2 = {"Lcom/listafacilnueva/parser/ParserUtils;", "", "()V", "marcasConocidas", "", "", "getMarcasConocidas", "()Ljava/util/Set;", "palabrasIrrelevantes", "getPalabrasIrrelevantes", "palabrasNumericas", "", "getPalabrasNumericas", "()Ljava/util/Map;", "unidades", "getUnidades", "esNumero", "", "s", "esUnidadConocida", "unidad", "normalizarUnidad", "app_debug"})
public final class ParserUtils {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.Object> palabrasNumericas = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> unidades = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> palabrasIrrelevantes = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> marcasConocidas = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.listafacilnueva.parser.ParserUtils INSTANCE = null;
    
    private ParserUtils() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> getPalabrasNumericas() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getUnidades() {
        return null;
    }
    
    public final boolean esUnidadConocida(@org.jetbrains.annotations.NotNull()
    java.lang.String unidad) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getPalabrasIrrelevantes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String normalizarUnidad(@org.jetbrains.annotations.NotNull()
    java.lang.String unidad) {
        return null;
    }
    
    public final boolean esNumero(@org.jetbrains.annotations.NotNull()
    java.lang.String s) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getMarcasConocidas() {
        return null;
    }
}