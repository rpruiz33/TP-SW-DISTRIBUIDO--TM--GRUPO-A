package com.grpc.grpc_server.grpc;


import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.KafkaServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.grpc.User;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.grpc.UserMapper;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.OfferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.TransferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.services.kafka.impl.EventAdhesionProducerServiceImpl;
import com.grpc.grpc_server.services.kafka.impl.ExternalEventProducerServiceImpl;
import com.grpc.grpc_server.services.kafka.impl.OperationProducerServiceImpl;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class KafkaProducerGrpcService extends KafkaServiceGrpc.KafkaServiceImplBase
{

    @Autowired
    ExternalEventProducerServiceImpl externalEventProducerService;

    @Autowired
    OperationProducerServiceImpl operationProducerServiceImpl;

    @Autowired
    EventAdhesionProducerServiceImpl eventAdhesionProducerServiceImpl;

    @Override
    public void createOperation(MyServiceClass.OperationRequest request, StreamObserver<MyServiceClass.GenericResponse> responseObserver){

        String result = "";


        switch (request.getOperationType().toUpperCase()){

            case "SOLICITUD":
                RequestDTO dto = OperationMapper.toDTO(request);
                Operation operation = OperationMapper.toEntity(dto,OperationType.SOLICITUD );
                result= operationProducerServiceImpl.createAndSendOperation(operation);
            break;

            case "TRANSFERENCIA":
                log.debug("Llega a transferencia");
                TransferDTO dto2 = OperationMapper.toTransferDTO(request);
                Operation operation2 = OperationMapper.toEntity(dto2,OperationType.TRANSFERENCIA );
                result = operationProducerServiceImpl.processTransfer(operation2, String.valueOf(request.getIdOrganization()));
            break;

            case "OFERTA":
                OfferDTO dto3 = OperationMapper.toOfferDTO(request);
                Operation operation3 = OperationMapper.toEntity(dto3,OperationType.OFERTA );
                result = operationProducerServiceImpl.createAndSendOperation(operation3);
            break;
            
            default:
                CancelRequestDTO dto4 = OperationMapper.toCancelRequestDTO(request);
                result = operationProducerServiceImpl.processCancelRequest(dto4);
            break;
        }
        
        // Construir y enviar la respuesta
        MyServiceClass.GenericResponse response = MyServiceClass.GenericResponse.newBuilder()
                .setSuccess(true)
                .setMessage(result)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void createExternalEvent(MyServiceClass.ExternalEventRequest request,  StreamObserver<MyServiceClass.GenericResponse> responseObserver) {

        String result = externalEventProducerService.createExternalEvent(request.getId());
        var response = MyServiceClass.GenericResponse.newBuilder();

        switch (result) {
            case "Evento Externo Generado con exito":
                response.setSuccess(true).setMessage(result);
                break;

            case "Error enviando mensaje kafka":
            case "No se encontro un evento con el ID ingresado":
            case "ID enviado no valido":
            case "Ya se encuentra publicado este evento":
            case "No se puede publicar un evento pasado":
            default:
                response.setSuccess(false).setMessage(result);
                break;
        }

        // Construir y enviar la respuesta
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createEventAdhesion(MyServiceClass.EventAdhesionRequest request,  StreamObserver<MyServiceClass.GenericResponse> responseObserver) {
        String result = eventAdhesionProducerServiceImpl.saveEventAdhesion(request.getIdExternalEvent(),request.getEmailVolunteer());
        var response = MyServiceClass.GenericResponse.newBuilder();


        switch (result) {
            case "Voluntario adherido al evento con exito":
                response.setSuccess(true).setMessage(result);
                break;

            case "ID/Email invalido":
            case "No se encontro el Evento para adherirse":
            case "Volutario ya registrado en el Evento externo":
            case "Error encontrando el Voluntario":
            case "Error enviando mensaje kafka para adherir volunttario":
            default:
                response.setSuccess(false).setMessage(result);
                break;
        }


        // Construir y enviar la respuesta
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getRequestList (MyServiceClass.RequestListRequest request, StreamObserver<MyServiceClass.OperationListResponse> responseObserver){

        // 1️⃣ Obtener entidades desde la capa service
        List<Operation> externalRequest = operationProducerServiceImpl.getRequestList(request.getIsExternal());


        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.OperationResponse> grpcExternalRequest = externalRequest.stream()
                .map(OperationMapper::toProto)
                .collect(Collectors.toList());



        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.OperationListResponse response = MyServiceClass.OperationListResponse.newBuilder()
                .addAllOperations(grpcExternalRequest)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();


    }

    @Override
    public void getExternalEventList(MyServiceClass.Empty request, StreamObserver<MyServiceClass.ExternalEventListResponse> responseObserver){
        // 1️⃣ Obtener entidades desde la capa service
        List<ExternalEvent> externalEventList = externalEventProducerService.getExternalEventList();


        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.ExternalEventResponse> grpcExternalEvents = externalEventList.stream()
                .map(ExternalEventMapper::toProto)
                .collect(Collectors.toList());



        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.ExternalEventListResponse response = MyServiceClass.ExternalEventListResponse.newBuilder()
                .addAllExternalEvents(grpcExternalEvents)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

}
