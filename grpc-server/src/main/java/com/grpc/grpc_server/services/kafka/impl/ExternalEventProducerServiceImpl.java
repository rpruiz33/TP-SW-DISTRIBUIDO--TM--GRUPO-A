package com.grpc.grpc_server.services.kafka.impl;

import com.grpc.grpc_server.entities.grpc.Event;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import com.grpc.grpc_server.producer.ExternalEventProducer;
import com.grpc.grpc_server.repositories.grpc.EventRepository;
import com.grpc.grpc_server.repositories.kafka.EventAdhesionRepository;
import com.grpc.grpc_server.repositories.kafka.ExternalEventRepository;
import com.grpc.grpc_server.services.kafka.ExternalEventProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
public class ExternalEventProducerServiceImpl implements ExternalEventProducerService {

    @Autowired
    private ExternalEventRepository externalEventRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventAdhesionRepository eventAdhesionRepository;

    @Autowired
    private ExternalEventProducer externalEventProducer;

    @Override
    public String createExternalEvent(int idExternalEvent) {
        String result;

        if (idExternalEvent > 0) {

            Event event = eventRepository.findByIdEvent(idExternalEvent);
            ExternalEvent oldExternalEvent = externalEventRepository.findByIdExternalEventMessage(idExternalEvent);
            if (event != null) {

                //Si el evento no es previo
                if (!event.getDateRegistration().isBefore(LocalDateTime.now())){
                    //si no existe
                    if (oldExternalEvent == null){
                        //Mappeamos Event->DTO->ExternalEvent
                        ExternalEvent externalEvent = ExternalEventMapper.toEntity(
                                ExternalEventMapper.toDTO(event));

                        externalEventRepository.save(externalEvent);

                        ExternalEventMapper.ExternalEventDTO dtoKafka = ExternalEventMapper.toDTO(event);

                        //Enviamos mensaje KAFKA
                        if (externalEventProducer.sendExternalEventCreated(dtoKafka)) {
                            result = "Evento Externo Generado con exito";
                        } else {
                            result = "Error enviando mensaje kafka";
                        }
                    }else{
                        result = "Ya se encuentra publicado este evento";
                    }

                }else{
                    result = "No se puede publicar un evento pasado";
                }



            } else {
                result = "No se encontro un evento con el ID ingresado";
            }
        } else {
            result = "ID enviado no valido";
        }

        return result;
    }

    @Override
    public String processCancelExternalEvent(int idEvent) {
        String result ;

        //VERIFICAR SI EL EVENTO ES EXTERNO
        ExternalEvent externalEvent = externalEventRepository.findByIdExternalEventMessage(idEvent);

        if (externalEvent != null){

            //Eliminamos relaciones
            eventAdhesionRepository.deleteByExternalEvent(externalEvent);

            //Eliminamos el evento externo
            externalEventRepository.delete(externalEvent);

            //Publicamos el mensaje KAFKA}
            ExternalEventMapper.CancelExternalEventDTO cancelExternalEventDTO = ExternalEventMapper.toDTO(externalEvent);

            if (externalEventProducer.sendExternalEventDeleted(cancelExternalEventDTO)){
                result="Evento externo eliminado";
            }else{
                result="Error enviando mensaje de kafka";
            }
        }else{
            result="No se publico este evento en externos";
        }

        return result;
    }
}
