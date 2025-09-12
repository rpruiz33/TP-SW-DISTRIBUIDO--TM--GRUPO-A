package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.MemberAtEvent;
import com.grpc.grpc_server.entities.User;

public class MemberAtEventMapper {


    public static UserMapper.UserDTO toUserDTO (MemberAtEvent m){
        return UserMapper.toDTO(m.getUser());
    }

    public static MyServiceClass.UserProto toUserProto (MemberAtEvent m){
        return UserMapper.toProto(toUserDTO(m));
    }
}
