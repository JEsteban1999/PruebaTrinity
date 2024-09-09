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

import com.finanzas.entidad.application.controller.ClienteController;
import com.finanzas.entidad.core.entity.Cliente;
import com.finanzas.entidad.core.service.ClienteService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente cliente;
    private List<Cliente> listaClientes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Juan Perez");
        listaClientes = Arrays.asList(cliente);
    }

    @Test
    public void testCrearCliente() {
        when(clienteService.crearCliente(any(Cliente.class))).thenReturn(cliente);

        ResponseEntity<Cliente> response = clienteController.crearCliente(cliente);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cliente, response.getBody());
        verify(clienteService, times(1)).crearCliente(cliente);
    }

    @Test
    public void testActualizarCliente() {
        when(clienteService.actualizarCliente(eq(1L), any(Cliente.class))).thenReturn(cliente);

        ResponseEntity<Cliente> response = clienteController.actualizarCliente(1L, cliente);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
        verify(clienteService, times(1)).actualizarCliente(eq(1L), any(Cliente.class));
    }

    @Test
    public void testEliminarCliente() {
        doNothing().when(clienteService).eliminarCliente(1L);

        ResponseEntity<Void> response = clienteController.eliminarCliente(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clienteService, times(1)).eliminarCliente(1L);
    }

    @Test
    public void testObtenerCliente() {
        when(clienteService.obtenerCliente(1L)).thenReturn(cliente);

        ResponseEntity<Cliente> response = clienteController.obtenerCliente(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
        verify(clienteService, times(1)).obtenerCliente(1L);
    }

    @Test
    public void testObtenerTodosLosClientes() {
        when(clienteService.obtenerTodosLosClientes()).thenReturn(listaClientes);

        ResponseEntity<List<Cliente>> response = clienteController.obtenerTodosLosClientes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaClientes, response.getBody());
        verify(clienteService, times(1)).obtenerTodosLosClientes();
    }
}

