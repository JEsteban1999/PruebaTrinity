package com.finanzas.entidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.finanzas.entidad.core.entity.Cliente;
import com.finanzas.entidad.core.entity.Producto;
import com.finanzas.entidad.core.service.ClienteService;
import com.finanzas.entidad.infrastructure.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        cliente.setProductos(Collections.emptyList());
    }

    @Test
    void crearCliente_CuandoEsMayorDeEdad_DeberiaGuardarCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente clienteGuardado = clienteService.crearCliente(cliente);

        assertNotNull(clienteGuardado);
        assertEquals(cliente.getId(), clienteGuardado.getId());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void crearCliente_CuandoNoEsMayorDeEdad_DeberiaLanzarExcepcion() {
        cliente.setFechaNacimiento(LocalDate.now().minusYears(17)); // Menor de edad

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.crearCliente(cliente);
        });

        assertEquals("El cliente debe ser mayor de edad.", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_CuandoClienteExiste_DeberiaActualizarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setFechaNacimiento(LocalDate.of(1995, 1, 1)); // Mayor de edad

        Cliente result = clienteService.actualizarCliente(1L, clienteActualizado);

        assertNotNull(result);
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_CuandoNoEsMayorDeEdad_DeberiaLanzarExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setFechaNacimiento(LocalDate.now().minusYears(17)); // Menor de edad

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.actualizarCliente(1L, clienteActualizado);
        });

        assertEquals("El cliente debe ser mayor de edad.", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void eliminarCliente_CuandoTieneProductosAsociados_DeberiaLanzarExcepcion() {
        cliente.setProductos(Collections.singletonList(new Producto()));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            clienteService.eliminarCliente(1L);
        });

        assertEquals("No se puede eliminar un cliente con productos asociados", exception.getMessage());
        verify(clienteRepository, never()).deleteById(1L);
    }

    @Test
    void eliminarCliente_CuandoNoTieneProductosAsociados_DeberiaEliminarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.eliminarCliente(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void obtenerCliente_CuandoExiste_DeberiaRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente result = clienteService.obtenerCliente(1L);

        assertNotNull(result);
        assertEquals(cliente.getId(), result.getId());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerCliente_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            clienteService.obtenerCliente(1L);
        });

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerTodosLosClientes_DeberiaRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(cliente));

        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();

        assertFalse(clientes.isEmpty());
        assertEquals(1, clientes.size());
        verify(clienteRepository, times(1)).findAll();
    }
}

