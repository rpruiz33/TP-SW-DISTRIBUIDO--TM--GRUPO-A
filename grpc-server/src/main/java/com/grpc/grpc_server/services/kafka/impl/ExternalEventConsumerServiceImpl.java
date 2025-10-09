package com.grpc.grpc_server.services.kafka.impl;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.repositories.kafka.ExternalEventRepository;
import com.grpc.grpc_server.services.kafka.ExternalEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
public class ExternalEventConsumerServiceImpl implements ExternalEventConsumer {

    @Autowired
    ExternalEventRepository externalEventRepository;


    @Override
    public void saveExternalEvent(ExternalEvent e) {

        //VALIDACIONES DE DATOS (IDs mayores a 1, Strings no vacios, fechas posteriores a las de hoy, no se puede
        // publicar un evento que ya haya pasado, no tiene sentido)
        if (e.getIdOrganization()<1 || e.getIdExternalEventMessage()<1 || e.getNameExternalEvent().isEmpty()
            || e.getDescription().isEmpty() || e.getDateAndTime().isBefore(LocalDateTime.now())){

            //LOGICA DE GUARDADO

            externalEventRepository.save(e);
            //MENSAJE DE SUCCESS
            log.info("Evento externo guardado con éxito");

        }else{
            log.info("Datos incorrectos");
        }


    }
}
