package com.grpc.grpc_server.services.kafka.impl;

import com.grpc.grpc_server.entities.kafka.EventAdhesion;
import com.grpc.grpc_server.repositories.kafka.EventAdhesionRepository;
import com.grpc.grpc_server.services.kafka.EventAdhesionConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventAdhesionConsumerServiceImpl implements EventAdhesionConsumerService {


    @Autowired
    private EventAdhesionRepository eventAdhesionRepository;

    @Override
    public void saveEventAdhesion(EventAdhesion eventAdhesion) {

        //VALIDACIONES
        //ACTUALIZACION DE RELACIONES
        //GUARDAR EN BD
        //MENSAJE DE SUCCESS
        if (eventAdhesion.getEmailVolunteer().isEmpty() ||
            eventAdhesion.getNameVolunteer().isEmpty() ||
            eventAdhesion.getLastNameVolunteer().isEmpty() ||
            eventAdhesion.getPhoneVolunteer().isEmpty() ||
            eventAdhesion.getIdVolunteer() < 1 ||
            eventAdhesion.getExternalEvent()==null){

            log.warn("ERROR: Datos de la adhesion incorrectos");

        }else {
            //Guardamos la adhesion en la bd, no es necesario actualizar la relacion en memoria
            eventAdhesionRepository.save(eventAdhesion);

            log.info("Adhesion a evento guardada con exito");

        }




    }
}
