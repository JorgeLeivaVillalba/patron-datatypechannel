package com.example.route;

import com.example.model.Factura;
import com.example.model.Producto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class DatatypeChannelRoute extends RouteBuilder {

    @Override
    public void configure() {

        // RUTA: FACTURAS JSON
        from("direct:jsonFacturaChannel")
            .routeId("jsonFacturaRoute")
            .doTry()
                .unmarshal().json(Factura.class) //Transforma el JSON a un objeto Factura
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Factura factura = exchange.getIn().getBody(Factura.class);
                        if (factura.numero == null || factura.numero.isBlank()) {
                            throw new IllegalArgumentException("Factura invalida: numero vacio.");
                        }
                        if (factura.monto <= 0) {
                            throw new IllegalArgumentException("Factura invalida: monto debe ser mayor a 0.");
                        }
                    }
                })
                .log("Factura valida recibida: ${body}")
            .doCatch(Exception.class)
                .log("JSON RECHAZADO - Factura: ${exception.message}")
            .end();

        // RUTA: PRODUCTOS XML
        from("direct:xmlProductoChannel")
            .routeId("xmlProductoRoute")
            .doTry()
                .unmarshal().jaxb(Producto.class.getPackage().getName()) //transforma el XML a un objeto Producto

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Producto producto = exchange.getIn().getBody(Producto.class);
                        if (producto.codigo == null || producto.codigo.isBlank()) {
                            throw new IllegalArgumentException("Producto invalido: codigo vacio.");
                        }
                        if (producto.descripcion == null || producto.descripcion.isBlank()) {
                            throw new IllegalArgumentException("Producto invalido: descripcion vacia.");
                        }
                    }
                })
                .log("Producto valido recibido: ${body}")
            .doCatch(Exception.class)
                .log("XML RECHAZADO - Producto: ${exception.message}")
            .end();

        // GENERADOR DE MENSAJES
        from("timer://messageTimer?period=5000") // cada 5 segundos
            .routeId("messageGenerator")
            .process(exchange -> {
                // Usa Math.random() para decidir aleatoriamente si enviar mensajes válidos o inválidos
                boolean enviarValidos = Math.random() < 0.5; // 50% de probabilidad
                if (enviarValidos) { // Mensajes válidos
                    exchange.getContext().createProducerTemplate().sendBody("direct:jsonFacturaChannel",
                        """
                        {"numero": "F100", "monto": 1000.0}
                        """
                    );
                    exchange.getContext().createProducerTemplate().sendBody("direct:xmlProductoChannel",
                        """
                        <producto><codigo>P123</codigo><descripcion>Mouse</descripcion></producto>
                        """
                    );
                } else { // Mensajes inválidos
                    exchange.getContext().createProducerTemplate().sendBody("direct:jsonFacturaChannel",
                        """
                        {"monto": -50}
                        """
                    );
                    exchange.getContext().createProducerTemplate().sendBody("direct:xmlProductoChannel",
                        """
                        <producto><codigo></codigo></producto>
                        """
                    );
                }
            });
    }
}
