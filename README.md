# apiRestFacturacion

## Descripción
FacturacionApp es la version 2.0 de una aplicación Spring Boot diseñada para gestionar facturas.
Permite a los usuarios crear, ver y eliminar facturas, convertirlas a PDF, así como enviar por correo electrónico.
La aplicación utiliza React para el front-end e integra una base de datos MySQL para la persistencia de datos.

## Características
- Crear, ver y eliminar facturas
- Generar archivos PDF de facturas
- Crear, ver, modificar y eliminar clientes
- Crear, ver, modificar y eliminar productos
- Enviar facturas por correo electrónico con archivos PDF adjuntos
- Validación de formularios y manejo de errores
- Búsqueda de productos con autocompletado
- Buscar facturas por rango de fechas
- Generar PDF de facturas por rango de fechas

## Tecnologías
- Java 17
- Spring Boot 3.3.7
- Spring Data JPA
- React
- MySQL
- Maven
- itextPDF (para generación de PDF)
- Spring Boot Starter Mail (para funcionalidad de correo electrónico)

## Requisitos previos
- Java 17 o superior
- Maven
- MySQL
- Node.js y npm (para el frontend)

## Configuración
1. Clona el repositorio:
    ```sh
    git clone https://github.com/MrxSteve/apiRestFacturacion.git
    cd backend
    ```

2. Configura la base de datos:
    - Actualiza el archivo `application.properties` con tus credenciales de MySQL.
    - Si tienes docker puedes usar el siguiente comando para levantar el contenedor de MySQL
    - Nos ubicamos en la raiz del proyecto y ejecutamos el siguiente comando
        ```sh
        docker-compose up
        ```
    - Para verificar que el contenedor de MySQL se encuentra en ejecución ejecutamos el siguiente comando
        ```sh
        docker ps
        ```
      - Las credenciales por defecto son:
        - Usuario: root
        - Contraseña: password
        - Base de datos: facturacion_mysql

3. Construye el proyecto backend:
    ```sh
    mvn clean install
    ```

4. Ejecuta la aplicación backend:
    ```sh
    mvn spring-boot:run
    ```
   - O levantar la aplicacion desde el IDE de tu preferencia

5. Configura y ejecuta el frontend:
    ```sh
    cd frontend
    npm install
    npm run dev
    ```

## Uso
- Una vez levantadas las aplicaciones frontend y backend, abre `http://localhost:5170` en tu navegador para acceder a la aplicación.
- O desde el puerto que te de el frontend

## Visualización de Endpoints
- Abre Swagger en `http://localhost:8080/swagger-ui.html` para visualizar y probar todos los endpoints disponibles.


## Endpoints
### Clientes
- **Guardar cliente**: `POST /api/clientes/save`
- **Obtener cliente por ID**: `GET /api/clientes/find-one/{id}`
- **Listar todos los clientes**: `GET /api/clientes/find-all`
- **Actualizar cliente**: `PUT /api/clientes/update/{id}`
- **Eliminar cliente**: `DELETE /api/clientes/delete/{id}`
- **Buscar clientes por nombre**: `GET /api/clientes/find-by-nombre/{nombre}`

### Facturas
- **Guardar factura**: `POST /api/facturas/save`
- **Obtener factura por ID**: `GET /api/facturas/{id}`
- **Listar todas las facturas**: `GET /api/facturas/all`
- **Obtener facturas por rango de fechas**: `GET /api/facturas/by-date-range`
- **Exportar facturas por rango de fechas a PDF**: `GET /api/facturas/by-date-range/pdf`
- **Eliminar factura**: `DELETE /api/facturas/delete/{id}`
- **Generar PDF de una factura**: `GET /api/facturas/{id}/pdf`
- **Obtener facturas por cliente**: `GET /api/facturas/cliente/{clienteId}`

### Productos
- **Guardar producto**: `POST /api/productos/save`
- **Obtener producto por ID**: `GET /api/productos/find-one/{id}`
- **Listar todos los productos**: `GET /api/productos/find-all`
- **Actualizar producto**: `PUT /api/productos/update/{id}`
- **Eliminar producto**: `DELETE /api/productos/delete/{id}`
- **Buscar productos por nombre**: `GET /api/productos/find-by-nombre/{nombre}`
- **Subir foto de producto**: `POST /api/productos/upload-photo/{id}`

## Licencia
Este proyecto está licenciado bajo la Licencia MIT.