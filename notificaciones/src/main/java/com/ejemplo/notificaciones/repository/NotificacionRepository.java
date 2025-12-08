package com.ejemplo.notificaciones.repository;

import com.ejemplo.notificaciones.model.Notificacion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface NotificacionRepository extends ReactiveMongoRepository<Notificacion, String> {
    Flux<Notificacion> findByUsuarioOrderByFechaDesc(String usuario);

    Flux<Notificacion> findByUsuarioAndTipoOrderByFechaDesc(String usuario, String tipo);
}
