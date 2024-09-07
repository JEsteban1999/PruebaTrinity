package com.finanzas.entidad.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finanzas.entidad.core.entity.Cliente;
import com.finanzas.entidad.infrastructure.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente crearCliente(Cliente cliente) {
        if (!esMayorDeEdad(cliente.getFechaNacimiento())) {
            throw new IllegalArgumentException("El cliente debe ser mayor de edad.");
        }
        cliente.setFechaCreacion(LocalDateTime.now());
        cliente.setFechaModificacion(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Long id, Cliente clienteActualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        // Actualizar campos
        cliente.setTipoIdentificacion(clienteActualizado.getTipoIdentificacion());
        cliente.setNumeroIdentificacion(clienteActualizado.getNumeroIdentificacion());
        cliente.setNombres(clienteActualizado.getNombres());
        cliente.setApellidos(clienteActualizado.getApellidos());
        cliente.setCorreo(clienteActualizado.getCorreo());
        cliente.setFechaNacimiento(clienteActualizado.getFechaNacimiento());
        cliente.setFechaModificacion(LocalDateTime.now());
        if (!esMayorDeEdad(cliente.getFechaNacimiento())) {
            throw new IllegalArgumentException("El cliente debe ser mayor de edad.");
        }
        return clienteRepository.save(cliente);
    }

    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        if (!cliente.getProductos().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un cliente con productos asociados");
        }
        clienteRepository.deleteById(id);
    }

    public Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    public boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        return Period.between(fechaNacimiento, hoy).getYears() >= 18;
    }
}
