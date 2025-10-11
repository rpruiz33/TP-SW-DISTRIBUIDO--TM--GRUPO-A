package com.grpc.grpc_server.services.kafka.impl;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import com.grpc.grpc_server.repositories.kafka.EventAdhesionRepository;
import com.grpc.grpc_server.repositories.kafka.ExternalEventRepository;
import com.grpc.grpc_server.services.kafka.ExternalEventConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
public class ExternalEventConsumerServiceImpl implements ExternalEventConsumerService {

    @Autowired
    private ExternalEventRepository externalEventRepository;

    @Autowired
    private EventAdhesionRepository eventAdhesionRepository;

    @Override
    public ExternalEvent getExternalEventWithAdhesions(int id) {
       return externalEventRepository.findByIdExternalEventMessageWithAdhesions(id);
    }

    @Override
    public void saveExternalEvent(ExternalEvent e) {

        log.info(e.toString());
        //VALIDACIONES DE DATOS (IDs mayores a 1, Strings no vacios, fechas posteriores a las de hoy, no se puede
        // publicar un evento que ya haya pasado, no tiene sentido)
        if (e.getIdOrganization()<1 ||
                e.getIdExternalEventMessage()<1 ||
                e.getNameExternalEvent().isEmpty() ||
                e.getDescription().isEmpty() ||
                e.getDateAndTime().isBefore(LocalDateTime.now()))
        {

            log.warn("ERROR: Datos incorrectos");

        }else{

            //LOGICA DE GUARDADO
            externalEventRepository.save(e);
            //MENSAJE DE SUCCESS
            log.info("Evento externo guardado con éxito");
        }


    }

    @Override
    public void processCancelExternalEvent(ExternalEventMapper.CancelExternalEventDTO dto) {

        int idEvento= Integer.parseInt(dto.getIdEvento());
        int idOrganizacion=Integer.parseInt(dto.getIdOrganizacion());

        //VALIDAMOS QUE LOS DATOS NO VENGAN MAL Y QUE EL EVENTO EXISTA
        if (idEvento<1 || idOrganizacion<1 ){
            log.warn("Datos incorrectos");
        }else{
            ExternalEvent e = externalEventRepository.findByIdExternalEventMessage(idEvento);

            if (e != null){

                //ELIMINAR RELACIONES
                eventAdhesionRepository.deleteByExternalEvent(e);

                //BAJA FISICA
                externalEventRepository.delete(e);
                log.info("Evento dado de baja con exito");

            }else{
                log.warn("ERROR: No se encontro el evento");
            }

        }


    }



}
