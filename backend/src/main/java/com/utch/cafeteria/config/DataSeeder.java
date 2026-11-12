package com.utch.cafeteria.config;

import com.utch.cafeteria.model.Producto;
import com.utch.cafeteria.model.Rol;
import com.utch.cafeteria.model.Usuario;
import com.utch.cafeteria.repository.ProductoRepository;
import com.utch.cafeteria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Carga datos iniciales al arrancar (menu de ejemplo y usuarios de prueba).
 * Asi el prototipo queda listo para la demostracion en clase sin pasos manuales.
 *
 * Usuarios de prueba (usuario / contrasena):
 *   cliente  / cliente123   -> ROL CLIENTE
 *   personal / personal123  -> ROL PERSONAL
 *   admin    / admin123      -> ROL ADMIN
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProductoRepository productoRepository,
                      UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto("Cafe americano", new BigDecimal("18.00"), true));
            productoRepository.save(new Producto("Sandwich de jamon", new BigDecimal("35.00"), true));
            productoRepository.save(new Producto("Jugo de naranja", new BigDecimal("25.00"), true));
            productoRepository.save(new Producto("Muffin de chocolate", new BigDecimal("22.00"), true));
            // Producto no disponible: sirve para probar la regla de validacion.
            productoRepository.save(new Producto("Ensalada (agotada)", new BigDecimal("40.00"), false));
        }

        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario("cliente",
                    passwordEncoder.encode("cliente123"), Rol.CLIENTE));
            usuarioRepository.save(new Usuario("personal",
                    passwordEncoder.encode("personal123"), Rol.PERSONAL));
            usuarioRepository.save(new Usuario("admin",
                    passwordEncoder.encode("admin123"), Rol.ADMIN));
        }
    }
}
