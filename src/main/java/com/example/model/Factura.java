package com.example.model;
//Modelo de factura
public class Factura {
    public String numero;
    public double monto;

    @Override
    public String toString() {
        return "Factura{" + "numero='" + numero + '\'' + ", monto=" + monto + '}';
    }
}
