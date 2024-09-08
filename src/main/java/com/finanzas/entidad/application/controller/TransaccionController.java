package com.finanzas.entidad.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.finanzas.entidad.core.entity.Transaccion;
import com.finanzas.entidad.core.service.TransaccionService;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @PostMapping
    public ResponseEntity<Transaccion> crearTransaccion(@RequestBody Transaccion transaccion) {
        Transaccion nuevaTransaccion = transaccionService.crearTransaccion(transaccion);
        return new ResponseEntity<>(nuevaTransaccion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerTransaccion(@PathVariable Long id) {
        Transaccion transaccion = transaccionService.obtenerTransaccionPorId(id);
        return new ResponseEntity<>(transaccion, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Transaccion>> obtenerTodasTransacciones() {
        List<Transaccion> transacciones = transaccionService.obtenerTodasTransacciones();
        return new ResponseEntity<>(transacciones, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaccion> actualizarTransaccion(@PathVariable Long id, @RequestBody Transaccion transaccion) {
        Transaccion transaccionActualizada = transaccionService.actualizarTransaccion(id, transaccion);
        return new ResponseEntity<>(transaccionActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long id) {
        transaccionService.eliminarTransaccion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

