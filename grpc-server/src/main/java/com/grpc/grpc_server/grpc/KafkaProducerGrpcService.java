package com.grpc.grpc_server.grpc;


import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.OperationServiceGrpc;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.services.kafka.impl.OperationProducerServiceImpl;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class KafkaProducerGrpcService extends OperationServiceGrpc.OperationServiceImplBase
{

    @Autowired
    OperationProducerServiceImpl operationProducerServiceImpl;

    @Override
    public void createOperation(MyServiceClass.OperationRequest request, StreamObserver<MyServiceClass.OperationResponse> responseObserver){

        String result ="error";
        switch (request.getOperationType().toUpperCase()){

            case "SOLICITUD":{
                RequestDTO dto = OperationMapper.toDTO(request);
                Operation operation = OperationMapper.toEntity(dto,OperationType.SOLICITUD );
                result= operationProducerServiceImpl.createAndSendOperation(operation);
            }


            case "TRANSEFERENCIA":
        }

        // Construir y enviar la respuesta
        MyServiceClass.OperationResponse response = MyServiceClass.OperationResponse.newBuilder()
                .setSuccess(true)
                .setMessage(result)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }



}
