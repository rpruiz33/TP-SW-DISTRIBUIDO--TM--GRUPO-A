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
        self.user_stub = service_pb2_grpc.MyServiceStub(self.channel)
        self.donation_stub = service_pb2_grpc.DonationServiceStub(self.channel)

    def login(self, username: str, password: str):
        """Llama al RPC Login"""
        request = service_pb2.LoginRequest(username=username, password=password)
        return self.user_stub.Login(request)
    
    def altaUser(self, username: str,name: str, lastName: str, phone: str, email: str, role: str):
        """Llama al RPC Alta Usuario"""
        request = service_pb2.AltaUsuarioRequest(username=username,name=name,lastName=lastName,phone=phone, email=email, role=role )
        return self.user_stub.AltaUser(request)
    
    def getAllUsers(self):
        """Llama al RPC Traer todos los Usuarios"""
        request = service_pb2.Empty()
        return self.user_stub.GetAllUsers(request)
        
    def updateUser(self, username, name, lastName, phone, email, role):
        request = service_pb2.UpdateUsuarioRequest(
        username=username, name=name, lastName=lastName,
        phone=phone, email=email, role=role
    )
        return self.user_stub.UpdateUser(request)
    
    def deleteUser(self, username: str):
         request = service_pb2.DeleteUsuarioRequest(username=username)
         return self.user_stub.DeleteUser(request)
    
    def getAllDonations(self):
         print("llegamos a la llamada")
         request = service_pb2.Empty()
         return self.donation_stub.GetAllDonations(request)

