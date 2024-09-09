package com.finanzas.entidad;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.finanzas.entidad.application.controller.ProductoController;
import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.entity.TipoProducto;
import com.finanzas.entidad.core.service.ProductoService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private Producto producto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        producto = new Producto(); // Inicializa el objeto Producto
        producto.setId(1L);
        producto.setTipoProducto(TipoProducto.CUENTA_CORRIENTE);
        producto.setSaldo(new BigDecimal(1000.0));
    }

    @Test
    void testCrearProducto() {
        when(productoService.crearProducto(any(Producto.class))).thenReturn(producto);
        Producto nuevoProducto = productoController.crearProducto(producto).getBody();
        assertNotNull(nuevoProducto);
        assertEquals(1L, nuevoProducto.getId());
        assertEquals(HttpStatus.CREATED, productoController.crearProducto(producto).getStatusCode());
    }

    @Test
    void testObtenerProductoPorId() {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(producto);
        ResponseEntity<Producto> response = productoController.obtenerProductoPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testObtenerTodosLosProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.obtenerTodosLosProductos()).thenReturn(productos);
        ResponseEntity<List<Producto>> response = productoController.obtenerTodosLosProductos();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testActualizarProducto() {
        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setTipoProducto(TipoProducto.CUENTA_CORRIENTE);
        productoActualizado.setSaldo(new BigDecimal(2000.0));
        
        when(productoService.actualizarProducto(eq(1L), any(Producto.class))).thenReturn(productoActualizado);
        ResponseEntity<Producto> response = productoController.actualizarProducto(1L, producto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TipoProducto.CUENTA_CORRIENTE, response.getBody().getTipoProducto());
    }

    @Test
    void testEliminarProducto() {
        doNothing().when(productoService).eliminarProducto(1L);
        ResponseEntity<Void> response = productoController.eliminarProducto(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productoService, times(1)).eliminarProducto(1L);
    }
}


