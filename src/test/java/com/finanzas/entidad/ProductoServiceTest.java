package com.finanzas.entidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.finanzas.entidad.core.entity.Cliente;
import com.finanzas.entidad.core.entity.EstadoProducto;
import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.entity.TipoProducto;
import com.finanzas.entidad.core.service.ClienteService;
import com.finanzas.entidad.core.service.ProductoService;
import com.finanzas.entidad.infrastructure.repository.ProductoRepository;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Cliente cliente;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Inicializar objetos de prueba
        cliente = new Cliente();
        cliente.setId(1L);

        producto = new Producto();
        producto.setCliente(cliente);
        producto.setTipoProducto(TipoProducto.CUENTA_AHORROS);
        producto.setSaldo(BigDecimal.ZERO);
        producto.setNumeroCuenta("5300000001");
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaModificacion(LocalDateTime.now());
        producto.setEstado(EstadoProducto.ACTIVA);
    }

    @Test
    public void testCrearProducto_ClienteExistente() {
        // Mockear comportamiento del clienteService y productoRepository
        when(clienteService.obtenerCliente(anyLong())).thenReturn(cliente);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Ejecutar el método
        Producto productoCreado = productoService.crearProducto(producto);
        productoCreado.setNumeroCuenta("5300000001");

        // Verificaciones
        assertNotNull(productoCreado);
        assertEquals("5300000001", productoCreado.getNumeroCuenta());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    public void testCrearProducto_ClienteNoExistente() {
        when(clienteService.obtenerCliente(anyLong())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crearProducto(producto);
        });

        assertEquals("El cliente no existe.", exception.getMessage());
    }

    @Test
    public void testEliminarProducto_ConSaldo() {
        producto.setSaldo(new BigDecimal("100"));

        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.eliminarProducto(1L);
        });

        assertEquals("No se puede eliminar una cuenta que tiene saldo.", exception.getMessage());
    }

    @Test
    public void testEliminarProducto_SinSaldo() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).delete(producto);
    }

    @Test
    public void testObtenerProductoPorId_ProductoExistente() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        Producto productoEncontrado = productoService.obtenerProductoPorId(1L);

        assertNotNull(productoEncontrado);
        assertEquals(producto.getNumeroCuenta(), productoEncontrado.getNumeroCuenta());
    }

    @Test
    public void testObtenerProductoPorId_ProductoNoExistente() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productoService.obtenerProductoPorId(1L);
        });

        assertEquals("Producto no encontrado con id 1", exception.getMessage());
    }
}

