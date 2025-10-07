/*package com.grpc.grpc_server.producer;

import org.springframework.stereotype.Component;

import com.grpc.grpc_server.repositories.OperationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestOperationProducer {

    private final OperationProducer operationProducer;
    private final OperationRepository operationRepository;

    private  int contador = 1;

   /*@PostConstruct
    public void startProducing() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                // Crear Operation
                Operation op = Operation.builder()
                        .idOperationMessage(contador)
                        .idOrganization(1)
                        .operationType(OperationType.OFERTA) // o el que quieras
                        .activate(true)
                        .dateRegistration(LocalDateTime.now())
                        .dateModification(LocalDateTime.now())
                        .build();

       
                OperationDonation d1 = OperationDonation.builder()
                        .category(Category.ALIMENTO)
                        .description("Donación de alimentos #" + contador)
                        .quantity(10 + contador)
                        .operation(op)
                        .build();

                OperationDonation d2 = OperationDonation.builder()
                        .category(Category.ROPA)
                        .description("Donación de ropa #" + contador)
                        .quantity(5 + contador)
                        .operation(op)
                        .build();

                op.setOperationDonations(Arrays.asList(d1, d2));

                // Guardar en DB
                operationRepository.save(op);

                // Enviar a Kafka
                operationProducer.sendOperationCreated(op);

                log.info("Mensaje de prueba #{} enviado y guardado en DB", contador);
                contador++;

            } catch (Exception e) {
                log.error("Error al producir el mensaje", e);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
}
*/