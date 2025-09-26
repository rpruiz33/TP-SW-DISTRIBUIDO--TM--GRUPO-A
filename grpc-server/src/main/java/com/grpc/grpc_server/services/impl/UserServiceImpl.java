package com.grpc.grpc_server.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.grpc.grpc_server.utils.EmailUtils;
import com.grpc.grpc_server.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.MyServiceClass.AltaUsuarioRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteUsuarioRequest;
import com.grpc.grpc_server.MyServiceClass.LoginRequest;
import com.grpc.grpc_server.MyServiceClass.UpdateUsuarioRequest;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.entities.MemberAtEvent;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.repositories.MemberAtEventRepository;
import com.grpc.grpc_server.repositories.RoleRepository;
import com.grpc.grpc_server.repositories.UserRepository;
import com.grpc.grpc_server.services.UserService;
import com.grpc.grpc_server.utils.PasswordUtils;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberAtEventRepository memberAtEventRepository;

    @Autowired
    private EmailUtils emailUtils;

    //Libreria que permite la encriptacion de contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    ///---------------------------------------------------------------------------------------------------------------------///
    public List<User> getAllUsers (){

        return  userRepository.findAll();

    }

    public List<User> getActiveUsers (){

        return  userRepository.findByActivate(true);

    }

    ///LOGIN DE USUARIO
    public String login(LoginRequest request) {

        User user =userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername()).orElse(null);
        String result;


        if (user!=null){

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                result= JwtUtil.generateToken(user.getUsername(),user.getRole().getNameRole().toUpperCase(), "Usuario Valido"); // ✅ éxito → devuelve token
            } else {
                result= JwtUtil.generateToken(user.getUsername(),user.getRole().getNameRole(),"Credenciales invalidas"); // ✅ usuario encontrado pero contraseña incorrecta
            }

        }else {
            result= JwtUtil.generateToken("NOTFOUND","NOTFOUND","Usuario no encontrado");
        }

        return result;

    }

    ///ALTA USUARIO
    public String altaUser(AltaUsuarioRequest request) {

        String result = "Usuario creado con éxito";

        //Verificar si existe un user registrado con el mail o username solicitado
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {

            result = "Email o username ya registrado";
        
        //Verificar que todos los campos estén completos
        } else if (request.getEmail().isEmpty() || request.getUsername().isEmpty() ||
                request.getName().isEmpty() || request.getLastName().isEmpty() ||
                request.getRole().isEmpty()) {

            result = "Datos incompletos";

        } else {

            Role role = roleRepository.findByNameRole(request.getRole());

            //Verificar que el rol exista, es decir, que se válido
            if (role == null) {

                result = "Rol inválido";

            } else {

                
                User newUser = UserMapper.toEntity(request, role); //Crear un user con el request
                String passPlana = PasswordUtils.generateRandomPassword(); //Generamos la contraseña 
                newUser.setPassword(PasswordUtils.encryptPassword(passPlana)); //Seteamos en el nuevo user
                userRepository.save(newUser);//Guardado en la base de datos

                //Envío de mail con contraseña
                try {
                    emailUtils.sendEmail(

                        newUser.getEmail(),
                        "Registro exitoso",
                        "Hola " + newUser.getName() + ",\n\nTu usuario fue creado exitosamente.\n" +
                        "Tu contraseña es: " + passPlana + "\n\n."
                    );

                } catch (Exception e) {

                    result = "Usuario creado pero error al enviar email";
                }
            }
        }

        return result;
    }


    ///MODIFICAR USER
    public String updateUser(UpdateUsuarioRequest request) {

        log.debug("ENTRAMOS AL IMP");

        // Buscar al usuario original por oldEmail y oldUsername
        User user = userRepository.findByEmailAndUsername(request.getOldEmail(), request.getOldUsername()).orElse(null);

        if (user == null) {
            log.debug("Usuario no encontrado ");
            return "Usuario no encontrado";
        }



        // Validar campos obligatorios
        if (request.getName().isEmpty() || request.getLastName().isEmpty() ||
                request.getEmail().isEmpty() || request.getRole().isEmpty()) {
            return "Datos incompletos";
        }

        // Validar si el nuevo email está siendo utilizado por otro usuario
        if (!request.getEmail().equals(user.getEmail())) { // solo si se cambia
            User existingByEmail = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (existingByEmail != null) {
                return "Email ya está siendo utilizado";
            }
        }

        // Validar si el nuevo username está siendo utilizado por otro usuario
        if (!request.getUsername().equals(user.getUsername())) { // solo si se cambia
            User existingByUsername = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (existingByUsername != null) {
                return "Username ya está siendo utilizado";
            }
        }

        // Validar rol
        Role rol = roleRepository.findByNameRole(request.getRole());
        if (rol == null) {
            return "Rol inválido";
        }

        // Actualizar campos
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(rol);

        userRepository.save(user);
        log.debug("Usuario actualizado correctamente");

        return "Usuario modificado con éxito";
    }

    ///ELIMINAR USER
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