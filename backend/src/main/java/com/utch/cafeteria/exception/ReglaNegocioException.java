package com.utch.cafeteria.exception;

/**
 * Se lanza cuando una regla funcional del caso se viola
 * (ej. agregar un producto no disponible, o una transicion de estado invalida).
 * Se traduce a HTTP 400.
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
