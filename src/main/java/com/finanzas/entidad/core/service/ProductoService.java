package com.finanzas.entidad.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finanzas.entidad.core.entity.Cliente;
import com.finanzas.entidad.core.entity.EstadoProducto;
import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.entity.TipoProducto;
import com.finanzas.entidad.infrastructure.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteService clienteService;

    public Producto crearProducto(Producto producto) {
        // Cliente existente
        Cliente cliente = clienteService.obtenerCliente(producto.getCliente().getId());
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe.");
        }
        // Generar número de cuenta
        String numeroCuenta = generarNumeroCuenta(producto.getTipoProducto());
        producto.setNumeroCuenta(numeroCuenta);
        producto.setSaldo(BigDecimal.ZERO);
        // Establecer estado predeterminado
        if (producto.getTipoProducto() == TipoProducto.CUENTA_AHORROS) {
            producto.setEstado(EstadoProducto.ACTIVA);
        }
        // Verificar saldo inicial de la cuenta de ahorro
        if (producto.getTipoProducto() == TipoProducto.CUENTA_AHORROS && producto.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo de una cuenta de ahorros no puede ser menor a cero.");
        }
        // Establecer fechas de creacion y modificacion
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaModificacion(LocalDateTime.now());
        // Guardar producto
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto detallesProducto) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + id));
        
        // Actualizar atributos permitidos
        producto.setEstado(detallesProducto.getEstado());
    
        // Establecer la fecha de modificación
        producto.setFechaModificacion(LocalDateTime.now());
    
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + id));
    
        // Verificar si el saldo es cero
        if (producto.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("No se puede eliminar una cuenta que tiene saldo.");
        }
    
        productoRepository.delete(producto);
    }
    
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + id));
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }
    
    public String generarNumeroCuenta(TipoProducto tipoProducto) {
        String prefijo = tipoProducto == TipoProducto.CUENTA_AHORROS ? "53" : "33";
        String sufijo = String.format("%08d", new Random().nextInt(99999999));
        return prefijo + sufijo;
    }

    public Producto actualizarSaldo(Long productoId, BigDecimal nuevoSaldo) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + productoId));
    
        // Actualizar el saldo y la fecha de modificación
        producto.setSaldo(nuevoSaldo);
        producto.setFechaModificacion(LocalDateTime.now());
    
        return productoRepository.save(producto);
    }
    
}
