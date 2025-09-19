package com.grpc.grpc_server.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.entities.MemberAtEvent;
import com.grpc.grpc_server.repositories.MemberAtEventRepository;
import com.grpc.grpc_server.services.UserService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grpc.grpc_server.MyServiceClass.*;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.repositories.RoleRepository;
import com.grpc.grpc_server.repositories.UserRepository;
import com.grpc.grpc_server.util.PasswordUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberAtEventRepository memberAtEventRepository;

    @Autowired
    private EmailService emailService;

    //Libreria que permite la encriptacion de contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    ///---------------------------------------------------------------------------------------------------------------------
    public List<User> getAllUsers (){

        return  userRepository.findAll();

    }

    public List<User> getActiveUsers (){

        return  userRepository.findByActivate(true);

    }


    public String login(LoginRequest request) {
        return userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername())
                .map(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return user.getRole().getNameRole(); // ✅ éxito → devuelve rol
                    } else {
                        return ""; // ✅ usuario encontrado pero contraseña incorrecta
                    }
                })
                .orElse("User not found"); // ✅ no encontró usuario
    }


    public boolean altaUser(AltaUsuarioRequest request) {

        boolean result;

        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            result=false;
        } else {
            if (request.getEmail().isEmpty() || request.getUsername().isEmpty() ||
                request.getName().isEmpty() || request.getLastName().isEmpty() ||
                request.getRole().isEmpty()) {

                result=false;
            } else {
                Role rol = roleRepository.findByNameRole(request.getRole());

                if (rol == null) {

                    result=false;
                }

                User newUser = UserMapper.toEntity(request, rol);

                String passPlana = PasswordUtils.generateRandomPassword();
                newUser.setPassword(PasswordUtils.encryptPassword(passPlana));

                userRepository.save(newUser);
                result=true;

                /* 🚀 Enviar mail con la contraseña
                try {
                    emailService.sendEmail(
                        newUser.getEmail(),
                        "Registro exitoso",
                        "Hola " + newUser.getName() + ",\n\nTu usuario fue creado exitosamente.\n" +
                        "Tu contraseña es: " + passPlana + "\n\n."
                    );
                    result=true;
                } catch (Exception e) {
                    result=false;
                }
                */

            }
        }

        return  result;
    }
    

    public boolean updateUser(UpdateUsuarioRequest request) {

        boolean result;
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null) {
            result =false;
        } else {

            user.setName(request.getName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());

            Role rol = roleRepository.findByNameRole(request.getRole());

            if (rol == null) {
                result =false;
            }

            user.setRole(rol);

            userRepository.save(user);
            result =true;
        }

        return  result;
    }


    @Transactional
    public String deleteUser(DeleteUsuarioRequest request) {
        String result;

        if (request == null || request.getUsername() == null || request.getUsername().isEmpty()) {
            return "Error datos inválidos";
        }

        User u = userRepository.findByUsername(request.getUsername()).orElse(null);


        if (u != null && u.getActivate()) {
            // 🔹 Desactivar usuario
            u.setActivate(false);

            // 🔹 Remover de todos los eventos futuros
            LocalDate hoy = LocalDate.now();
            List<MemberAtEvent> futurasAsociaciones = new ArrayList<>();

            for (MemberAtEvent mae : u.getEvents()) {
                Event e = mae.getEvent();
                if (e != null && e.getDateRegistration() != null &&
                        e.getDateRegistration().isAfter(hoy.atStartOfDay())) {

                    futurasAsociaciones.add(mae);
                }
            }

            for (MemberAtEvent mae : futurasAsociaciones) {
                Event e = mae.getEvent();

                // Eliminar de la tabla intermedia
                memberAtEventRepository.delete(mae);

                // Actualizar listas en memoria
                u.getEvents().remove(mae);
                if (e != null) {
                    e.getMembers().remove(mae);
                }
            }

            userRepository.save(u);
            return "Usuario dado de baja";

        } else if (u != null && !u.getActivate()) {

            u.setActivate(true);
            userRepository.save(u);
            result = "Usuario dado de alta";
        } else {
            result = "Error usuario no encontrado";
        }

        return result;
    }




}
