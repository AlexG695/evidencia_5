package com.utch.cafeteria.exception;

/**
 * Se lanza cuando no existe el recurso solicitado (ej. producto o pedido inexistente).
 * Se traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
