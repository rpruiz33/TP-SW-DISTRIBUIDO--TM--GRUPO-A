import os
import sys
import grpc

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.join(CURRENT_DIR, '../proto'))  
import service_pb2
import service_pb2_grpc

class MyServiceClient:
    def __init__(self, host='localhost', port=9090):
        self.host = host
        self.port = port
        self.channel = grpc.insecure_channel(f'{self.host}:{self.port}')
        self.stub = service_pb2_grpc.MyServiceStub(self.channel)

    def my_method(self, param: str):
        """Llama al RPC MyMethod"""
        request = service_pb2.MyRequest(param=param)
        return self.stub.MyMethod(request)

    def login(self, username: str, password: str):
        """Llama al RPC Login"""
        request = service_pb2.LoginRequest(username=username, password=password)
        return self.stub.Login(request)
    
    def altaUser(self, username: str,name: str, lastName: str, phone: str, email: str, role: str):
        """Llama al RPC Alta Usuario"""
        print(lastName)
        request = service_pb2.AltaUsuarioRequest(username=username,name=name,lastName=lastName,phone=phone, email=email, role=role )
        print("Salgo")
        return self.stub.AltaUser(request)
