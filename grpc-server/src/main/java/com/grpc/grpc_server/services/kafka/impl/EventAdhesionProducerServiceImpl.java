package com.grpc.grpc_server.services.kafka.impl;

import com.grpc.grpc_server.entities.grpc.User;
import com.grpc.grpc_server.entities.kafka.EventAdhesion;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.EventAdhesionMapper;
import com.grpc.grpc_server.producer.EventAdhesionProducer;
import com.grpc.grpc_server.repositories.grpc.UserRepository;
import com.grpc.grpc_server.repositories.kafka.EventAdhesionRepository;
import com.grpc.grpc_server.repositories.kafka.ExternalEventRepository;
import com.grpc.grpc_server.services.kafka.EventAdhesionConsumerService;
import com.grpc.grpc_server.services.kafka.EventAdhesionProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EventAdhesionProducerServiceImpl implements EventAdhesionProducerService {


    @Autowired
    private EventAdhesionRepository eventAdhesionRepository;

    @Autowired
    private EventAdhesionProducer eventAdhesionProducer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExternalEventRepository externalEventRepository;

    @Override
    public String saveEventAdhesion(int idEvent, String emailVolunteer) {
        String result;
        //PRODUCIR UN MENSAJE ASIGNANDO UN VOLUNTARIO INTERNO A UN EVENTO EXTERNO

        if (idEvent >0 && !emailVolunteer.isEmpty() ){
            ExternalEvent externalEvent = externalEventRepository.findByIdExternalEventMessageWithAdhesions(idEvent);

            if (externalEvent != null) {

                boolean isRegistered = false;

                for (EventAdhesion adhesion : externalEvent.getAdhesions()) {
                    //SI  esta registrado flag
                    if (adhesion.getEmailVolunteer().equals(emailVolunteer)) {
                        isRegistered = true;
                        break;
                    }
                }

                if(!isRegistered) {

                  Optional<User> user = userRepository.findByEmailOrUsername(emailVolunteer,emailVolunteer);

                  //Si esta presente, asignamos
                  if(!user.isEmpty()) {

                    //Mapeamos de User-EventAdhesionDTO-EventAdhesion
                      EventAdhesionMapper.EventAdhesionDTO eventAdhesionDTO = EventAdhesionMapper.toDTO(user.get(),idEvent);

                      //Guardamos en la BD con la relacion
                      eventAdhesionRepository.save(EventAdhesionMapper.toEntity(eventAdhesionDTO,externalEvent));

                      //Producimos mensaje
                      if (eventAdhesionProducer.sendEventAdhhesion(eventAdhesionDTO,externalEvent.getIdOrganization())){

                          result = "Voluntario adherido al evento con exito";
                      } else {
                          result = "Error enviando mensaje kafka para adherir volunttario";
                      }


                  }else{
                      result ="Error encontrando el Voluntario";
                  }
                }else {
                    result="Volutario ya registrado en el Evento externo";
                }

            }else{
                result="No se encontro el Evento para adherirse";
            }
        }else{
            result ="ID/Email invalido";
        }

        return result;
    }
}
