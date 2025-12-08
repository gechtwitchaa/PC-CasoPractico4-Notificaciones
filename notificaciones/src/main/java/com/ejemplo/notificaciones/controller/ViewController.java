package com.ejemplo.notificaciones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/notificaciones/{usuario}")
    public String notificaciones(@PathVariable String usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "notificaciones";
    }
}
