package com.grpc.grpc_server.interceptor;

import com.grpc.grpc_server.utils.JwtUtil;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Slf4j
@Component
@GlobalServerInterceptor  // Aplica el interceptor a todos los servicios gRPC
public class JwtServerInterceptor implements ServerInterceptor {
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        // Nombre completo del método gRPC invocado
        String methodName = call.getMethodDescriptor().getFullMethodName();

        // -------------------------
        // Métodos públicos (no requieren autenticación)
        // -------------------------
        if (methodName.matches("MyService/Login") || methodName.contains("KafkaService/") ) {
            return next.startCall(call, headers);
        }

        // Listener que intercepta el mensaje
        ServerCall.Listener<ReqT> listener = new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
                                    next.startCall(call, headers)) {

            @Override
            public void onMessage(ReqT message) {
                try {
                    

                    // -------------------------
                    // 🔑 Obtener token desde METADATA en lugar del request
                    // -------------------------

                    String token = null;
                    Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
                    String authHeader = headers.get(AUTHORIZATION_KEY);

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7); // sacamos "Bearer "
                    }


                    // -------------------------
                    // Validación de token
                    // -------------------------
                    if (token == null || !JwtUtil.validateToken(token)) {
                        
                        //cierra llamada
                        call.close(
                                Status.UNAUTHENTICATED.withDescription("Token inválido o ausente"), headers
                        );

                        // Retornar listener vacío para bloquear la ejecución
                        return;
                    }

                    // -------------------------
                    // Extraer datos del token
                    // -------------------------

                    String username = JwtUtil.getUsername(token);
                    String role = JwtUtil.getRole(token);

                    // -------------------------
                    // Validación de permisos según método exacto
                    // -------------------------

                    switch (methodName) {

                        // Métodos de UserService (solo PRESIDENTE)
                        case "MyService/GetAllUsers":
                        case "MyService/AltaUser":
                        case "MyService/UpdateUser":
                        case "MyService/DeleteUser":
                        case "MyService/SendEmail":

                            if (!role.equals("PRESIDENTE")) {
                                call.close(
                                        Status.PERMISSION_DENIED.withDescription("Solo PRESIDENTE puede usar UserService"),
                                        headers
                                );
                                return;
                            }
                            break;

                        // Métodos del INVENTARIO o DONATIONS (PRESIDENTE y VOCAL)
                        case "DonationService/GetAllDonations":
                        case "DonationService/AltaDonation":
                        case "DonationService/UpdateDonation":
                        case "DonationService/DeleteDonation":

                            if (role.equals("VOLUNTARIO") || role.equals("COORDINADOR")) {
                                
                                call.close(
                                        Status.PERMISSION_DENIED.withDescription("Solo PRESIDENTE y VOCAL pueden usar DonationService"),
                                        headers
                                );
                                return;
                            }
                            break;

                        // Métodos de EventService (TODOS MENOS VOCAL)
                        case "EventService/GetAllEventsWithRelations":
                        case "EventService/CreateEvent":
                        case "EventService/UpdateEvent":
                        case "EventService/DeleteEvent":

                            if (role.equals("VOCAL")) {
                                call.close(
                                        Status.PERMISSION_DENIED.withDescription("SI SOS VOCAL NO podes usar EventService"),
                                        headers
                                );
                                return;
                            }
                            break;

                        // Métodos de DonationAtEvent (PRESIDENTE-COORDINADOR)
                        case "DonationsAtEventsService/CreateDonationAtEvent":
                        case "DonationsAtEventsService/UpdateDonationAtEvent":
                        case "DonationsAtEventsService/GetAllDonationsAtEvent":
                        case "DonationService/GetActiveDonations":

                            if (!(role.equals("PRESIDENTE") || role.equals("COORDINADOR") )) {
                                call.close(
                                        Status.PERMISSION_DENIED.withDescription("PERMISO DENEGADO PARA MANEJAR DONATIONATEVENT"),
                                        headers
                                );
                                return;
                            }
                            break;

                        // Métodos de MemberatEvent (PRESIDENTE-COORDINADOR-VOLUNTARIO)
                        case "EventService/ToggleMemberToEvent":
                        case "MyService/GetActiveUsers":

                            if (role.equals("VOCAL")) {
                                call.close(
                                        Status.PERMISSION_DENIED.withDescription("PERMISO DENEGADO PARA MANEJAR DONATIONATEVENT"),
                                        headers
                                );
                                return;
                            }
                            break;

                        // Otros métodos públicos o no restringidos
                        default:
                            break;
                    }

                    // -------------------------
                    // Token válido y permisos correctos → procesar el mensaje
                    // -------------------------
                    super.onMessage(message);

                } catch (Exception e) {

                    // Error inesperado en la validación
                    call.close(
                            Status.PERMISSION_DENIED.withDescription("Error validando token"),
                            headers
                    );
                    
                }
            }
        };

        return listener;
    }
}
