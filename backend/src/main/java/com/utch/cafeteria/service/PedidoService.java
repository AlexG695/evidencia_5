package com.utch.cafeteria.service;

import com.utch.cafeteria.dto.CrearPedidoRequest;
import com.utch.cafeteria.dto.ItemPedidoRequest;
import com.utch.cafeteria.exception.ReglaNegocioException;
import com.utch.cafeteria.exception.RecursoNoEncontradoException;
import com.utch.cafeteria.model.DetallePedido;
import com.utch.cafeteria.model.EstadoPedido;
import com.utch.cafeteria.model.Pedido;
import com.utch.cafeteria.model.Producto;
import com.utch.cafeteria.repository.PedidoRepository;
import com.utch.cafeteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica de negocio de pedidos: aqui viven las reglas del Caso 4.
 * Implementa el flujo minimo: registrar pedido -> calcular total -> actualizar estado -> consultar.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Registra un pedido aplicando las reglas del caso:
     *  - El pedido debe tener al menos un producto (validado tambien por el DTO).
     *  - No se agregan productos que no existen o que no estan disponibles.
     *  - El total se calcula en el servidor (suma de subtotales).
     *  - Todo pedido nuevo inicia en estado RECIBIDO.
     */
    @Transactional
    public Pedido crearPedido(CrearPedidoRequest req) {
        Pedido pedido = new Pedido(req.cliente());

        for (ItemPedidoRequest item : req.items()) {
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "El producto " + item.productoId() + " no existe"));

            // Regla: no se agregan productos no disponibles.
            if (!producto.isDisponible()) {
                throw new ReglaNegocioException(
                        "El producto '" + producto.getNombre() + "' no esta disponible");
            }

            pedido.agregarDetalle(new DetallePedido(producto, item.cantidad()));
        }

        // Regla: calcular total.
        pedido.recalcularTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido obtener(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    /**
     * Avanza el estado del pedido respetando la maquina de estados:
     * RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO.
     * Rechaza saltos o retrocesos de estado.
     */
    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido destino) {
        Pedido pedido = obtener(id);
        EstadoPedido actual = pedido.getEstado();

        if (!actual.puedeTransicionarA(destino)) {
            throw new ReglaNegocioException(
                    "Transicion de estado invalida: " + actual + " -> " + destino
                    + ". Desde " + actual + " solo se permite avanzar a " + actual.siguiente() + ".");
        }

        pedido.setEstado(destino);
        return pedidoRepository.save(pedido);
    }
}
