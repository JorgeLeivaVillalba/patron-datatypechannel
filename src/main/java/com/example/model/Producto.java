package com.example.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "producto")
@XmlAccessorType(XmlAccessType.FIELD)
public class Producto {
    public String codigo;
    public String descripcion;

    @Override
    public String toString() {
        return "Producto{" + "codigo='" + codigo + '\'' + ", descripcion='" + descripcion + '\'' + '}';
    }
}
