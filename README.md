# Sistema de pedidos para cafetería universitaria

**Evidencia 6 · Caso 4 — Arquitecturas de Software (Unidad II)**
Prototipo funcional mínimo que demuestra la correspondencia:
`DIAGRAMA → ELEMENTO MODELADO → IMPLEMENTACIÓN → PRUEBA → EVIDENCIA`

| | |
|---|---|
| **Equipo** | Equipo 4 |
| **Integrantes** | *[completar]* |
| **Arquitectura** | Por capas (cliente-servidor, API REST) |
| **Stack** | Angular 17 · Spring Boot 3.2 · Spring Security · Spring Data JPA · H2 |

---

## Estructura del repositorio

```
cafeteria-pedidos/
├── backend/        API REST (Spring Boot + H2 + Spring Security)
├── frontend/       SPA en Angular 17
├── postman/        Colección de pruebas de la API
├── docs/
│   ├── diagramas/  Diagramas UML (fuentes .mmd + imágenes .png)
│   ├── evidencias/ Capturas de ejecución (agregar aquí)
│   └── Evidencia6_Caso4_Cafeteria_Documento_Integrador.docx
├── GUIA_COMMITS.md
└── README.md
```

## Requisitos

- **Backend:** Java 17 o superior y Maven 3.8+
- **Frontend:** Node.js 18+ y Angular CLI (`npm i -g @angular/cli`)

## Cómo ejecutar

### 1) Backend (API en http://localhost:8080)
```bash
cd backend
mvn spring-boot:run
```
- Consola de la base de datos: http://localhost:8080/h2-console
  (JDBC URL: `jdbc:h2:mem:cafeteriadb`, usuario `sa`, sin contraseña)
- Pruebas automatizadas: `mvn test`

### 2) Frontend (app en http://localhost:4200)
```bash
cd frontend
npm install
npm start
```

## Usuarios de prueba

| Usuario | Contraseña | Rol | Puede |
|---|---|---|---|
| `cliente`  | `cliente123`  | CLIENTE  | Consultar menú, registrar y consultar pedidos |
| `personal` | `personal123` | PERSONAL | Consultar pedidos y avanzar su estado |
| `admin`    | `admin123`    | ADMIN    | Gestionar productos |

## Flujo funcional mínimo

Consultar menú → seleccionar → registrar pedido → **calcular total** → actualizar estado → consultar.

Estados del pedido: `RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO` (sin saltos ni retrocesos).

## Endpoints

| Método y ruta | Descripción | Rol |
|---|---|---|
| `GET /api/productos` | Consultar menú (disponibles) | Autenticado |
| `POST /api/productos` | Crear producto | ADMIN |
| `PUT /api/productos/{id}` | Actualizar producto / disponibilidad | ADMIN |
| `POST /api/pedidos` | Registrar pedido y calcular total | CLIENTE |
| `GET /api/pedidos` | Listar pedidos | Autenticado |
| `GET /api/pedidos/{id}` | Consultar un pedido | Autenticado |
| `PATCH /api/pedidos/{id}/estado` | Avanzar estado | PERSONAL |
| `GET /api/auth/me` | Identidad y rol del usuario | Autenticado |

## Pruebas (Postman)

Importar `postman/Cafeteria-Pedidos.postman_collection.json` y ejecutar con **Runner**.
Incluye pruebas del flujo, de las reglas de negocio (400) y del control de acceso (401 / 403).

## Reglas de negocio implementadas

1. Un pedido debe tener al menos un producto con cantidad válida (≥ 1).
2. No se agregan productos no disponibles → `400 Bad Request`.
3. El **total lo calcula el servidor** (el cliente no lo envía).
4. El estado solo avanza en la secuencia definida; cualquier salto → `400`.
5. Control de acceso por rol con Spring Security (HTTP Basic, BCrypt).

## Mapa con la evidencia

- **Ejercicio 1** (modelo y plan): `docs/diagramas/` + Documento integrador §1.
- **Ejercicio 2** (prototipo y pruebas): `backend/`, `frontend/`, `postman/`.
- **Ejercicio 3** (verificación): matriz final en el Documento integrador §3.1.
