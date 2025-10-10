package com.grpc.grpc_server.services.kafka.impl;

import com.grpc.grpc_server.entities.grpc.Event;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import com.grpc.grpc_server.producer.ExternalEventProducer;
import com.grpc.grpc_server.repositories.grpc.EventRepository;
import com.grpc.grpc_server.repositories.kafka.ExternalEventRepository;
import com.grpc.grpc_server.services.kafka.ExternalEventProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ExternalEventProducerServiceImpl implements ExternalEventProducerService {

    @Autowired
    ExternalEventRepository externalEventRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ExternalEventProducer externalEventProducer;

    @Override
    public String createExternalEvent(int idExternalEvent) {
        String result;

        if (idExternalEvent > 0 ){

            Event event = eventRepository.findByIdEvent(idExternalEvent);

            if (event != null){

                //Mappeamos Event->DTO->ExternalEvent
                ExternalEvent externalEvent = ExternalEventMapper.toEntity(
                                                ExternalEventMapper.toDTO(event));

                //Enviamos mensaje KAFKA
                if (externalEventProducer.sendExternalEventCreated(externalEvent)){
                    //Guardamos en la BD
                    externalEventRepository.save(externalEvent);
                    result="Evento Externo Generado con exito";

                }else {

                    result="Error enviando mensaje kafka";
                }

            }else{
                result ="No se encontro un evento con el ID ingresado";
            }
        }else{
            result= "ID enviado no valido";
        }

        return result;
    }

    @Override
    public void processCancelExternalEvent(ExternalEventMapper.CancelExternalEventDTO dto) {

    }
}
