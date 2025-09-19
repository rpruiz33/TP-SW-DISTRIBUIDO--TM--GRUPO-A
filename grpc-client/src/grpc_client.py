import os
import sys
import grpc

from google.protobuf.timestamp_pb2 import Timestamp

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
        self.event_stub = service_pb2_grpc.EventServiceStub(self.channel)
        self.donation_event_stub = service_pb2_grpc.DonationsAtEventsServiceStub(self.channel)


    def login(self, username: str, password: str):
        """Llama al RPC Login"""
        request = service_pb2.LoginRequest(username=username, password=password)
        return self.user_stub.Login(request)
    
    def altaUser(self, username: str, name: str, lastName: str, phone: str, email: str, role: str):
        """Llama al RPC Alta Usuario"""
        request = service_pb2.AltaUsuarioRequest(username=username, name=name, lastName=lastName, phone=phone, email=email, role=role)
        return self.user_stub.AltaUser(request)
    
    def getAllUsers(self):
        """Llama al RPC Traer todos los Usuarios"""
        request = service_pb2.Empty()
        return self.user_stub.GetAllUsers(request)
    
    def getActiveUsers(self):
        """Llama al RPC Traer todos los Usuarios ACTIVOS"""
        request = service_pb2.Empty()
        return self.user_stub.GetActiveUsers(request)

    def updateUser(self, username: str, name: str, lastName: str, phone: str, email: str, role: str):
        request = service_pb2.UpdateUsuarioRequest(username=username, name=name, lastName=lastName, phone=phone, email=email, role=role)
        return self.user_stub.UpdateUser(request)
    
    def deleteUser(self, username: str):
         request = service_pb2.DeleteUsuarioRequest(username=username)
         return self.user_stub.DeleteUser(request)
    
    def getAllDonations(self):
         request = service_pb2.Empty()
         return self.donation_stub.GetAllDonations(request)
    
    def getActiveDonations(self):
         request = service_pb2.Empty()
         return self.donation_stub.GetActiveDonations(request)
    
    def altaDonation(self, category: str, description: str, amount: int, username:str):
        
        request = service_pb2.AltaDonationRequest(
            category=category,
            description=description,
            amount=amount,
            username=username
        )
        return self.donation_stub.AltaDonation(request)
    
    def updateDonation(self, id: int, category: str, description: str, amount: int, username: str):
        print(amount)
        request = service_pb2.UpdateDonationRequest(id=id, category=category, description=description, amount=amount, username=username)
        return self.donation_stub.UpdateDonation(request)
    
    def deleteDonation(self, id: int, username: str):
        request = service_pb2.DeleteDonationRequest(id=id, username=username)
        return self.donation_stub.DeleteDonation(request)
    
    def getAllEvents(self):
        """Llama al RPC GetAllEvents"""
        request = service_pb2.Empty()
        return self.event_stub.GetAllEventsWithRelations(request)
    
    def createEvent(self, nameEvent: str, descriptionEvent: str, dateRegistration: str):
        """Llama al RPC CreateEvent"""
        request = service_pb2.CreateEventRequest(nameEvent=nameEvent, descriptionEvent=descriptionEvent, dateRegistration=dateRegistration)
        return self.event_stub.CreateEvent(request)
    
    def updateEvent(self, id: int, nameEvent: str, descriptionEvent: str, dateRegistration: str):
        """Llama al RPC UpdateEvent"""
        request = service_pb2.UpdateEventRequest(id=id, nameEvent=nameEvent, descriptionEvent=descriptionEvent, dateRegistration=dateRegistration)
        return self.event_stub.UpdateEvent(request)
    
    def deleteEvent(self, id: int):
        """Llama al RPC DeleteEvent"""
        request = service_pb2.DeleteEventRequest(id=id)
        return self.event_stub.DeleteEvent(request)
    
    def toggleMemberToEvent(self, eventId: int, username: str, alreadyAssigned:bool):
        """Llama al RPC ToggleMemberToEvent"""
        request = service_pb2.ToggleMemberRequest(eventId=eventId, username=username,alreadyAssigned=alreadyAssigned)
        return self.event_stub.ToggleMemberToEvent(request)

    
    def createDonationAtEvent(self, idEvent: int, description:str , quantityDelivered:int, username:str):
        """Llama al RPC CreateDonationAtEvent"""
        request = service_pb2.CreateDonationAtEventRequest(idEvent=idEvent, description=description, quantityDelivered=quantityDelivered,username=username)
        return self.donation_event_stub.CreateDonationAtEvent(request)
    
    def getAllDonationsAtEvent(self, idEvent: int):
        """Llama al RPC GetAllDonationsAtEvent"""
        request = service_pb2.GetAllDonationsAtEventRequest(idEvent=idEvent)
        return self.donation_event_stub.GetAllDonationsAtEvent(request)
    



