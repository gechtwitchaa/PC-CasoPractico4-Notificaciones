# PC-CasoPractico4-Notificaciones
https://github.com/gechtwitchaa/PC-CasoPractico4-Notificaciones.git
# Sistema de Notificaciones en Tiempo Real

## Integrantes
- **Chahla Bouchotroch**  
- **Alberto Gonzalez Olmedo**  

## Descripción
Este proyecto implementa un sistema de notificaciones en tiempo real para usuarios de una aplicación web usando Spring WebFlux, MongoDB y Thymeleaf.  
Las notificaciones se muestran automáticamente mediante Server-Sent Events (SSE) sin necesidad de recargar la página.  

Cada notificación contiene los siguientes atributos:  
- **id** (String, generado automáticamente por MongoDB)  
- **usuario** (String)  
- **mensaje** (String)  
- **tipo** (INFO, ALERTA, URGENTE)  
- **fecha** (Date)  
- **leido** (Boolean)  

## Funcionalidades
- **Registrar** nuevas notificaciones y guardarlas en MongoDB.  
- **Visualizar** notificaciones en tiempo real usando SSE.  
- **Marcar** notificaciones como leídas.  
- **Filtrar** notificaciones por tipo.  
- **Eliminar** notificaciones.  

## Estructura de archivos
- `model/Notificacion.java` → Define la entidad Notificacion.  
- `repository/NotificacionRepository.java` → Repositorio reactivo para MongoDB.  
- `service/NotificacionService.java` → Lógica para agregar, filtrar, marcar leídas y eliminar notificaciones.  
- `controller/NotificacionController.java` → Endpoints WebFlux para SSE y CRUD.  
- `templates/notificaciones.html` → Vista Thymeleaf mostrando notificaciones en tiempo real.  
- `application.properties` → Configuración de MongoDB y parámetros de la aplicación.  

