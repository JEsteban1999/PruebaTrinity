package com.finanzas.entidad.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.entity.Transaccion;
import com.finanzas.entidad.infrastructure.repository.ProductoRepository;
import com.finanzas.entidad.infrastructure.repository.TransaccionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public Transaccion crearTransaccion(Transaccion transaccion) {
        Producto producto;
        switch (transaccion.getTipoTransaccion()) {
            case CONSIGNACION:
                producto = productoRepository.findById(transaccion.getCuentaDestino().getId())
                        .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
                producto.setSaldo(producto.getSaldo().add(transaccion.getMonto()));
                productoRepository.save(producto);
                break;
            case RETIRO:
                producto = productoRepository.findById(transaccion.getCuentaOrigen().getId())
                        .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
                if (producto.getSaldo().compareTo(transaccion.getMonto()) < 0) {
                    throw new RuntimeException("Saldo insuficiente");
                }
                producto.setSaldo(producto.getSaldo().subtract(transaccion.getMonto()));
                productoRepository.save(producto);
                break;
            case TRANSFERENCIA:
                procesarTransferencia(transaccion);
                break;
            default:
                throw new RuntimeException("Tipo de transacción no soportado");
        }
        transaccion.setFecha(LocalDateTime.now());
        return transaccionRepository.save(transaccion);
    }

    private void procesarTransferencia(Transaccion transaccion) {
        Producto cuentaOrigen = productoRepository.findById(transaccion.getCuentaOrigen().getId())
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
        Producto cuentaDestino = productoRepository.findById(transaccion.getCuentaDestino().getId())
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (cuentaOrigen.getSaldo().compareTo(transaccion.getMonto()) < 0) {
            throw new RuntimeException("Saldo insuficiente en la cuenta origen");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(transaccion.getMonto()));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(transaccion.getMonto()));

        productoRepository.save(cuentaOrigen);
        productoRepository.save(cuentaDestino);
    }

    public Transaccion obtenerTransaccionPorId(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
    }

    public List<Transaccion> obtenerTodasTransacciones() {
        return transaccionRepository.findAll();
    }

    public Transaccion actualizarTransaccion(Long id, Transaccion transaccionActualizada) {
        Transaccion transaccionExistente = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
        
        // Solo se pueden actualizar ciertos campos (según las reglas de negocio)
        transaccionExistente.setMonto(transaccionActualizada.getMonto());
        transaccionExistente.setFecha(LocalDateTime.now()); // Actualizar la fecha a la nueva transacción
    
        return transaccionRepository.save(transaccionExistente);
    }
    
    public void eliminarTransaccion(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
        transaccionRepository.delete(transaccion);
    }
    
}
