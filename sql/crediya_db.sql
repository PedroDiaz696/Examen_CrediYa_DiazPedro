-- =====================================================
-- Sistema de Cobros de Cartera "CrediYa"
-- Script de creación de la base de datos crediya_db
--
-- ATENCIÓN: borra y vuelve a crear la base de datos,
-- por lo que elimina los datos que tenga.
-- =====================================================

DROP DATABASE IF EXISTS crediya_db;
CREATE DATABASE crediya_db;
USE crediya_db;

CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    documento VARCHAR(30) NOT NULL UNIQUE,
    rol VARCHAR(30) NOT NULL,
    correo VARCHAR(80) NOT NULL UNIQUE,
    salario DECIMAL(10,2) NOT NULL,

    CHECK (rol IN ('ADMINISTRADOR', 'ASESOR', 'COBRADOR')),
    CHECK (salario > 0)
);

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    documento VARCHAR(30) NOT NULL UNIQUE,
    correo VARCHAR(80) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL
);

-- saldo_pendiente se agregó al script base: guarda lo que falta por pagar.
CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    empleado_id INT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    interes DECIMAL(5,2) NOT NULL,
    cuotas INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    saldo_pendiente DECIMAL(12,2) NOT NULL,

    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id),
    CHECK (monto > 0),
    CHECK (interes BETWEEN 0 AND 100),
    CHECK (cuotas > 0),
    CHECK (estado IN ('PENDIENTE', 'PAGADO', 'VENCIDO')),
    CHECK (saldo_pendiente >= 0)
);

CREATE TABLE pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id INT NOT NULL,
    fecha_pago DATE NOT NULL,
    monto DECIMAL(12,2) NOT NULL,

    FOREIGN KEY (prestamo_id) REFERENCES prestamos(id),
    CHECK (monto > 0)
);
