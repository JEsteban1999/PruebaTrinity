package com.finanzas.entidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.entity.TipoTransaccion;
import com.finanzas.entidad.core.entity.Transaccion;
import com.finanzas.entidad.core.service.TransaccionService;
import com.finanzas.entidad.infrastructure.repository.ProductoRepository;
import com.finanzas.entidad.infrastructure.repository.TransaccionRepository;

public class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearTransaccionConsignacion() {
        // Datos de prueba
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.CONSIGNACION);
        transaccion.setMonto(BigDecimal.valueOf(1000));
        Producto productoDestino = new Producto();
        productoDestino.setId(1L);
        productoDestino.setSaldo(BigDecimal.valueOf(5000));

        transaccion.setCuentaDestino(productoDestino);

        // Mockear comportamiento
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);

        // Ejecutar el método
        Transaccion resultado = transaccionService.crearTransaccion(transaccion);

        // Verificar interacciones y resultados
        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(6000), productoDestino.getSaldo()); // Verificar que el saldo se actualizó
        verify(productoRepository).save(productoDestino);
        verify(transaccionRepository).save(transaccion);
    }

    @Test
    void testCrearTransaccionRetiroConSaldoInsuficiente() {
        // Datos de prueba
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.RETIRO);
        transaccion.setMonto(BigDecimal.valueOf(1000));
        Producto productoOrigen = new Producto();
        productoOrigen.setId(1L);
        productoOrigen.setSaldo(BigDecimal.valueOf(500));

        transaccion.setCuentaOrigen(productoOrigen);

        // Mockear comportamiento
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoOrigen));

        // Verificar que lanza excepción por saldo insuficiente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });

        assertEquals("Saldo insuficiente", exception.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearTransaccionTransferencia() {
        // Datos de prueba
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.TRANSFERENCIA);
        transaccion.setMonto(BigDecimal.valueOf(1000));

        Producto cuentaOrigen = new Producto();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(BigDecimal.valueOf(5000));

        Producto cuentaDestino = new Producto();
        cuentaDestino.setId(2L);
        cuentaDestino.setSaldo(BigDecimal.valueOf(3000));

        // Asignar las cuentas a la transacción
        transaccion.setCuentaOrigen(cuentaOrigen);
        transaccion.setCuentaDestino(cuentaDestino);

        // Mockear comportamiento
        when(productoRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);

        // Ejecutar el método
        Transaccion resultado = transaccionService.crearTransaccion(transaccion);

        // Verificar interacciones y resultados
        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(4000), cuentaOrigen.getSaldo()); // Verificar saldo cuenta origen
        assertEquals(BigDecimal.valueOf(4000), cuentaDestino.getSaldo()); // Verificar saldo cuenta destino
        verify(productoRepository).save(cuentaOrigen);
        verify(productoRepository).save(cuentaDestino);
        verify(transaccionRepository).save(transaccion);
    }

    @Test
    void testObtenerTransaccionPorId() {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);

        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));

        Transaccion resultado = transaccionService.obtenerTransaccionPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(transaccionRepository).findById(1L);
    }

    @Test
    void testEliminarTransaccion() {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);

        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccion));

        transaccionService.eliminarTransaccion(1L);

        verify(transaccionRepository).delete(transaccion);
    }
}
