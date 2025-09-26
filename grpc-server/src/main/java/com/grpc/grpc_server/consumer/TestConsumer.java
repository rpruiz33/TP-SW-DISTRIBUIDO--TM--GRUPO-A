package com.grpc.grpc_server.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TestConsumer {

    @KafkaListener(topics = "test-solicitud-donacion", groupId = "grupo-unla")
    public void listen(String message) {
        System.out.println("Mensaje recibido: " + message);
    }

    /* JSON PARA MANDAR DESDE KAFBAT --> se deberia imprimir por consola
    {
        "idOrganizacion": "ONG123",
        "idSolicitud": "SOL001",
        "donaciones": [
            { "categoria": "ALIMENTOS", "descripcion": "Puré de tomates" }
        ]
    }
    */
}
