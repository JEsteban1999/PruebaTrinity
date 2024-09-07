// package com.finanzas.entidad.core.entity;

// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "transacciones")
// public class Transaccion {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Enumerated(EnumType.STRING)
//     private TipoTransaccion tipoTransaccion;

//     @ManyToOne
//     @JoinColumn(name = "producto_id", nullable = false)
//     private Producto producto;
// }
