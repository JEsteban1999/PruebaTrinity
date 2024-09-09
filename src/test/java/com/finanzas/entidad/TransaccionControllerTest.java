package com.finanzas.entidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.finanzas.entidad.application.controller.TransaccionController;
import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.entity.TipoTransaccion;
import com.finanzas.entidad.core.entity.Transaccion;
import com.finanzas.entidad.core.service.TransaccionService;

public class TransaccionControllerTest {

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private TransaccionController transaccionController;

    private Transaccion transaccion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setMonto(new BigDecimal(100.0));
        transaccion.setTipoTransaccion(TipoTransaccion.CONSIGNACION);
        transaccion.setFecha(LocalDateTime.now());

        // Configura las cuentas origen y destino si es necesario
        Producto cuenta = new Producto();
        cuenta.setId(1L);
        cuenta.setSaldo(new BigDecimal(1000.0));

        transaccion.setCuentaDestino(cuenta);
    }

    @Test
    void testCrearTransaccion() {
        when(transaccionService.crearTransaccion(any(Transaccion.class))).thenReturn(transaccion);
        ResponseEntity<Transaccion> response = transaccionController.crearTransaccion(transaccion);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testObtenerTransaccionPorId() {
        when(transaccionService.obtenerTransaccionPorId(1L)).thenReturn(transaccion);
        ResponseEntity<Transaccion> response = transaccionController.obtenerTransaccion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testObtenerTodasTransacciones() {
        List<Transaccion> transacciones = Arrays.asList(transaccion);
        when(transaccionService.obtenerTodasTransacciones()).thenReturn(transacciones);
        ResponseEntity<List<Transaccion>> response = transaccionController.obtenerTodasTransacciones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testActualizarTransaccion() {
        Transaccion transaccionActualizada = new Transaccion();
        transaccionActualizada.setId(1L);
        transaccionActualizada.setMonto(new BigDecimal(200.0));
        transaccionActualizada.setTipoTransaccion(TipoTransaccion.RETIRO);
        transaccionActualizada.setFecha(LocalDateTime.now());

        when(transaccionService.actualizarTransaccion(eq(1L), any(Transaccion.class))).thenReturn(transaccionActualizada);
        ResponseEntity<Transaccion> response = transaccionController.actualizarTransaccion(1L, transaccion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal(200.0), response.getBody().getMonto());
    }

    @Test
    void testEliminarTransaccion() {
        doNothing().when(transaccionService).eliminarTransaccion(1L);
        ResponseEntity<Void> response = transaccionController.eliminarTransaccion(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transaccionService, times(1)).eliminarTransaccion(1L);
    }
}
