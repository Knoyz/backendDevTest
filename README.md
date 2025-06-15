# MyApp - Similar Products Service

## Descripción General

**MyApp** es una solución de backend desarrollada en Java y Spring Boot que actúa como middleware entre clientes y un servicio externo de productos. Expone una API REST reactiva para consultar productos similares y sus detalles, integrando tecnologías modernas como Kafka, Redis y WebClient para lograr alta disponibilidad, escalabilidad y eficiencia en el manejo de datos y eventos.

---

## Arquitectura y Diseño

### Arquitectura General

- **Hexagonal (Ports & Adapters):**  
  El proyecto sigue el patrón de arquitectura hexagonal (también conocido como Ports and Adapters), separando la lógica de negocio de las dependencias externas (REST, Kafka, Redis, WebClient). Esto facilita la mantenibilidad, escalabilidad y testabilidad del sistema.

- **API First:**  
  El diseño y la documentación de la API se realizan antes de la implementación, garantizando contratos claros y consistentes entre equipos y servicios.

- **Reactive Programming con WebFlux:**  
  Se utiliza Spring WebFlux y Project Reactor para construir una API completamente reactiva y no bloqueante, ideal para escenarios de alta concurrencia y baja latencia.

### Componentes Principales

- **API REST:**  
  Expone endpoints para consultar productos similares y sus detalles.
- **Adaptadores de Entrada:**  
  - REST Controller (`ProductsController`)
  - Kafka Consumer (`KafkaConsumerAdapter`)
- **Adaptadores de Salida:**  
  - **WebClient** para comunicación con servicios REST externos (microservicios).
  - Redis para cacheo reactivo.
  - Kafka Producer/Consumer para eventos de cambios en productos.
  - **gRPC:** El proyecto está preparado para soportar comunicación gRPC en el futuro (clases `.proto` y dependencias incluidas), aunque actualmente no se utiliza ya que el microservicio externo solo expone HTTP. Esta preparación permite una migración sencilla a gRPC cuando el ecosistema lo permita.
- **Dominio:**  
  - Modelos de negocio (`ProductDetails`, `ProductDetailsChangedEvent`)
  - Puertos (`SimilarProductsUseCase`, `EventConsumerPort`, etc.)
  - Servicios de aplicación (`SimilarProductsUseCaseService`)
- **Infraestructura:**  
  - Configuración de Kafka, Redis, WebClient, gRPC, etc.
  - Utilidades de serialización/deserialización (`JsonUtils`)

### Patrones y Buenas Prácticas

- **Inyección de Dependencias:**  
  Uso de Spring DI y anotaciones como `@Service`, `@Component`, `@Configuration`.
- **DTOs y Mappers:**  
  Separación entre modelos de dominio y DTOs para desacoplar la lógica interna de la representación externa.
- **Manejo Centralizado de Errores:**  
  `GlobalExceptionHandler` para respuestas consistentes ante errores.
- **Testing:**  
  Pruebas unitarias y de integración con Mockito, JUnit 5 y MockWebServer.
- **Documentación:**  
  Integración con Swagger/OpenAPI para documentación automática de la API.

---

## Estructura del Proyecto

### Descripción de las capas

- **application/service/**  
  Implementa la lógica de negocio y orquesta los casos de uso principales.

- **domain/model/**  
  Define los modelos de dominio y entidades principales.

- **domain/events/**  
  Define los eventos de dominio relevantes para la aplicación.

- **domain/port/in/**  
  Define los puertos de entrada (interfaces de casos de uso y consumidores de eventos).

- **domain/port/out/**  
  Define los puertos de salida (interfaces para cache, servicios externos, publicación de eventos).

- **infrastructure/adapters/in/**  
  Adaptadores de entrada: exponen la API REST y consumen eventos de Kafka.

- **infrastructure/adapters/out/**  
  Adaptadores de salida: interactúan con servicios externos (REST, Redis).

- **infrastructure/config/**  
  Configuración de beans, clientes y dependencias externas.

- **infrastructure/dto/**  
  Objetos de transferencia de datos para la comunicación entre capas.

- **infrastructure/exceptions/**  
  Manejo centralizado de errores y excepciones.

- **infrastructure/mapper/**  
  Conversión entre modelos de dominio, DTOs y eventos.

- **infrastructure/utils/**  
  Utilidades generales (por ejemplo, serialización JSON).

- **proto/**  
  Contratos gRPC preparados para futura integración.

---

## Tecnologías Seleccionadas

| Tecnología         | Motivo de Selección                                                                 |
|--------------------|------------------------------------------------------------------------------------|
| **Java 21**        | Últimas características del lenguaje y soporte a largo plazo.                      |
| **Spring Boot 3**  | Framework robusto, productivo y ampliamente adoptado en la industria.              |
| **Spring WebFlux** | Programación reactiva, ideal para alta concurrencia y eficiencia de recursos.      |
| **API First**      | Contratos claros y mantenibles desde el inicio del desarrollo.                     |
| **Hexagonal**      | Arquitectura desacoplada, mantenible y testeable.                                  |
| **Kafka**          | Mensajería distribuida, desacoplamiento de servicios y procesamiento de eventos.    |
| **Redis**          | Cache distribuido, rápido y reactivo para mejorar el rendimiento de consultas.      |
| **WebClient**      | Cliente HTTP reactivo para comunicación entre microservicios.                      |
| **gRPC**           | Preparado para comunicación eficiente y tipada en el futuro.                       |
| **Project Reactor**| Soporte para flujos reactivos y no bloqueantes.                                    |
| **Lombok**         | Reducción de boilerplate en modelos y servicios.                                   |
| **JUnit 5/Mockito**| Testing moderno, flexible y fácil de mantener.                                     |
| **Swagger/OpenAPI**| Documentación interactiva y estandarizada de la API REST.                          |
| **Docker Compose** | Orquestación sencilla de servicios para desarrollo y pruebas locales.               |

> **Nota sobre la comunicación entre microservicios:**  
> Actualmente, la comunicación entre microservicios se realiza mediante **WebClient** (HTTP REST) ya que el servicio externo solo expone endpoints HTTP. Sin embargo, el proyecto ya incluye las clases `.proto` y dependencias necesarias para migrar a **gRPC** en el futuro, permitiendo una transición sencilla cuando el ecosistema lo permita.

---

## Pasos para levantar el proyecto

### 1. Clonar el repositorio

```bash
git clone <REPO_URL>
cd MyApp
```

### 2. Levantar dependencias externas con Docker Compose

Asegúrate de tener Docker y Docker Compose instalados.

```bash
docker-compose up -d
```

Esto levantará los servicios de Redis, Kafka y Zookeeper necesarios para la aplicación.

### 3. Construir el proyecto

```bash
./mvnw clean package
```

### 4. Ejecutar la aplicación
Puedes correr la aplicación localmente:


```bash
./mvnw spring-boot:run
```

O bien, construir y ejecutar el contenedor Docker:

```bash 
docker build -t myapp .
docker run --rm -p 5000:5000 --network="host" myapp
```

### 5. Acceder a la API y documentación

API REST: http://localhost:5000
Swagger UI: http://localhost:5000/swagger-ui.html


