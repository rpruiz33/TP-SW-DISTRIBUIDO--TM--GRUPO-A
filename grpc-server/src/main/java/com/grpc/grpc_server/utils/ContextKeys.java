package com.grpc.grpc_server.utils;

import io.grpc.Context;


public class ContextKeys {

    public static final Context.Key<String> USERNAME = Context.key("username");
    public static final Context.Key<String> ROLE = Context.key("role");

}
