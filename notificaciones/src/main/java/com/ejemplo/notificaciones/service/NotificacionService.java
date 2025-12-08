package com.ejemplo.notificaciones.service;

import com.ejemplo.notificaciones.model.Notificacion;
import com.ejemplo.notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class NotificacionService {

    private final NotificacionRepository repo;

    // Un sink para broadcast de todas las notificaciones nuevas
    private final Sinks.Many<Notificacion> sink;

    public NotificacionService(NotificacionRepository repo) {
        this.repo = repo;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    // Flujo infinito para un usuario (filtrado + replay de las ya persistidas + nuevas)
    public Flux<Notificacion> getNotificacionesEnTiempoReal(String usuario) {
        // Primero emitimos las existentes (desde la BD), luego concatenamos con el stream vivo filtrado por usuario
        Flux<Notificacion> existentes = repo.findByUsuarioOrderByFechaDesc(usuario);
        Flux<Notificacion> nuevas = sink.asFlux()
                .filter(n -> n.getUsuario().equals(usuario));
        return Flux.concat(existentes, nuevas);
    }

    public Mono<Notificacion> addNotificacion(Notificacion n) {
        // guarda y luego emite al sink
        if (n.getFecha() == null) {
            n.setFecha(java.time.Instant.now());
        }
        return repo.save(n)
                .doOnNext(saved -> sink.tryEmitNext(saved));
    }

    public Mono<Notificacion> marcarLeido(String id) {
        return repo.findById(id)
                .flatMap(n -> {
                    n.setLeido(true);
                    return repo.save(n);
                });
    }

    public Flux<Notificacion> filtrarPorTipo(String usuario, String tipo) {
        return repo.findByUsuarioAndTipoOrderByFechaDesc(usuario, tipo);
    }

    public Mono<Void> eliminarNotificacion(String id) {
        return repo.deleteById(id);
    }
}
