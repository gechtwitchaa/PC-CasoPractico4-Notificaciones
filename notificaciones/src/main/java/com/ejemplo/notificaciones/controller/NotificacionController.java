package com.ejemplo.notificaciones.controller;

import com.ejemplo.notificaciones.model.Notificacion;
import com.ejemplo.notificaciones.service.NotificacionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.codec.ServerSentEvent;

import java.time.Instant;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    // SSE: flujo de Server-Sent Events para un usuario
    @GetMapping(value = "/stream/{usuario}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Notificacion>> streamPorUsuario(@PathVariable String usuario) {
        return service.getNotificacionesEnTiempoReal(usuario)
                .map(not -> ServerSentEvent.<Notificacion>builder()
                        .id(not.getId())
                        .event("notificacion")
                        .data(not)
                        .build());
    }

    @PostMapping
    public Mono<Notificacion> crear(@RequestBody Notificacion n) {
        if (n.getFecha() == null) n.setFecha(Instant.now());
        n.setLeido(false);
        return service.addNotificacion(n);
    }

    @PutMapping("/leer/{id}")
    public Mono<Notificacion> marcarLeido(@PathVariable String id) {
        return service.marcarLeido(id);
    }

    @GetMapping("/usuario/{usuario}")
    public Flux<Notificacion> porUsuario(@PathVariable String usuario) {
        return service.filtrarPorTipo(usuario, null)
                .switchIfEmpty(service.getNotificacionesEnTiempoReal(usuario).take(0));
    }

    @GetMapping("/filtrar/{usuario}/{tipo}")
    public Flux<Notificacion> filtrarPorTipo(@PathVariable String usuario, @PathVariable String tipo) {
        return service.filtrarPorTipo(usuario, tipo);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminar(@PathVariable String id) {
        return service.eliminarNotificacion(id);
    }
}

