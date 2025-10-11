package com.grpc.grpc_server.grpc;


import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.KafkaServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.OfferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.TransferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.services.kafka.impl.ExternalEventProducerServiceImpl;
import com.grpc.grpc_server.services.kafka.impl.OperationProducerServiceImpl;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class KafkaProducerGrpcService extends KafkaServiceGrpc.KafkaServiceImplBase
{

    @Autowired
    ExternalEventProducerServiceImpl externalEventProducerService;

    @Autowired
    OperationProducerServiceImpl operationProducerServiceImpl;

    @Override
    public void createOperation(MyServiceClass.OperationRequest request, StreamObserver<MyServiceClass.OperationResponse> responseObserver){

        String result = "";

        
        switch (request.getOperationType().toUpperCase()){

            case "SOLICITUD":
                RequestDTO dto = OperationMapper.toDTO(request);
                Operation operation = OperationMapper.toEntity(dto,OperationType.SOLICITUD );
                result= operationProducerServiceImpl.createAndSendOperation(operation);
            break;

            case "TRANSFERENCIA":
                TransferDTO dto2 = OperationMapper.toTransferDTO(request);
                Operation operation2 = OperationMapper.toEntity(dto2,OperationType.TRANSFERENCIA );
                result = operationProducerServiceImpl.processTransfer(operation2);
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
        MyServiceClass.OperationResponse response = MyServiceClass.OperationResponse.newBuilder()
                .setSuccess(true)
                .setMessage(result)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void createExternalEvent(MyServiceClass.ExternalEventRequest request,  StreamObserver<MyServiceClass.GenericResponse> responseObserver){
        String result;

        result = externalEventProducerService.createExternalEvent(request.getId());

        // Construir y enviar la respuesta
        MyServiceClass.GenericResponse response = MyServiceClass.GenericResponse.newBuilder()
                .setSuccess(true)
                .setMessage(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



}
