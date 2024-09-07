package com.finanzas.entidad.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finanzas.entidad.core.entity.Cliente;
import com.finanzas.entidad.core.entity.Producto;
import java.util.List;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCliente(Cliente cliente);
    Producto findByNumeroCuenta(String numeroCuenta);
}
