package com.grpc.grpc_server.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.Organizacion;
import com.grpc.grpc_server.entities.SolicitudDonacion;
import com.grpc.grpc_server.repositories.OrganizacionRepository;
import com.grpc.grpc_server.repositories.SolicitudDonacionRepository;

@Service
public class TestConsumer {

    private final ObjectMapper objectMapper;
    private final SolicitudDonacionRepository solicitudRepository;
    private final OrganizacionRepository organizacionRepository;

    // Inyección por constructor
    @Autowired
    public TestConsumer(ObjectMapper objectMapper, 
                        SolicitudDonacionRepository solicitudRepository,
                        OrganizacionRepository organizacionRepository) {
        this.objectMapper = objectMapper;
        this.solicitudRepository = solicitudRepository;
        this.organizacionRepository = organizacionRepository;
    }

    @KafkaListener(topics = "test-solicitud-donacion", groupId = "grupo-unla")
    public void listen(String message) {
        try {
            System.out.println("Mensaje recibido: " + message);

            // Convertir JSON a objeto
            SolicitudDonacion solicitud = objectMapper.readValue(message, SolicitudDonacion.class);

            // Asignar relación de donaciones
            if (solicitud.getDonaciones() != null) {
                solicitud.getDonaciones().forEach(d -> d.setSolicitudDonacion(solicitud));
            }

            // Asignar organización existente
            Organizacion org = organizacionRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Organización no encontrada"));
            solicitud.setOrganizacion(org);// donde getId() devuelve el Integer/Long


            // Persistir
            solicitudRepository.save(solicitud);

            System.out.println("Solicitud guardada en la base de datos con donaciones.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
