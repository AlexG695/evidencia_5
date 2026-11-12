package com.utch.cafeteria;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas automatizadas del flujo minimo y del control de acceso.
 * Comprueban la correspondencia entre las reglas modeladas y la implementacion.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FlujoPedidoIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Test
    void menuRequiereAutenticacion_devuelve401SinCredenciales() throws Exception {
        mvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteConsultaMenu_ok() throws Exception {
        mvc.perform(get("/api/productos").with(httpBasic("cliente", "cliente123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").exists());
    }

    @Test
    void clienteRegistraPedido_calculaTotalEIniciaEnRecibido() throws Exception {
        String body = json.writeValueAsString(Map.of(
                "cliente", "Juan Perez",
                "items", new Object[]{ Map.of("productoId", 1, "cantidad", 2) } // 2 x 18.00 = 36.00
        ));

        mvc.perform(post("/api/pedidos")
                        .with(httpBasic("cliente", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RECIBIDO"))
                .andExpect(jsonPath("$.total").value(36.00));
    }

    @Test
    void pedidoConProductoNoDisponible_devuelve400() throws Exception {
        // El producto 5 (Ensalada) esta marcado como no disponible en el seed.
        String body = json.writeValueAsString(Map.of(
                "cliente", "Ana Lopez",
                "items", new Object[]{ Map.of("productoId", 5, "cantidad", 1) }
        ));

        mvc.perform(post("/api/pedidos")
                        .with(httpBasic("cliente", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void clienteNoPuedeCambiarEstado_devuelve403() throws Exception {
        String body = json.writeValueAsString(Map.of("estado", "EN_PREPARACION"));

        mvc.perform(patch("/api/pedidos/1/estado")
                        .with(httpBasic("cliente", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void personalAvanzaEstado_recibidoAEnPreparacion_ok() throws Exception {
        // Crea un pedido como cliente.
        String pedido = json.writeValueAsString(Map.of(
                "cliente", "Demo Estado",
                "items", new Object[]{ Map.of("productoId", 2, "cantidad", 1) }
        ));
        String respuesta = mvc.perform(post("/api/pedidos")
                        .with(httpBasic("cliente", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedido))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer id = (Integer) json.readValue(respuesta, Map.class).get("id");

        // El personal lo avanza a EN_PREPARACION.
        String cambio = json.writeValueAsString(Map.of("estado", "EN_PREPARACION"));
        mvc.perform(patch("/api/pedidos/" + id + "/estado")
                        .with(httpBasic("personal", "personal123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cambio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PREPARACION"));
    }

    @Test
    void transicionDeEstadoInvalida_devuelve400() throws Exception {
        // Crea un pedido (estado RECIBIDO) e intenta saltar directo a ENTREGADO.
        String pedido = json.writeValueAsString(Map.of(
                "cliente", "Salto Invalido",
                "items", new Object[]{ Map.of("productoId", 3, "cantidad", 1) }
        ));
        String respuesta = mvc.perform(post("/api/pedidos")
                        .with(httpBasic("cliente", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedido))
                .andReturn().getResponse().getContentAsString();
        Integer id = (Integer) json.readValue(respuesta, Map.class).get("id");

        String salto = json.writeValueAsString(Map.of("estado", "ENTREGADO"));
        mvc.perform(patch("/api/pedidos/" + id + "/estado")
                        .with(httpBasic("personal", "personal123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salto))
                .andExpect(status().isBadRequest());
    }
}
