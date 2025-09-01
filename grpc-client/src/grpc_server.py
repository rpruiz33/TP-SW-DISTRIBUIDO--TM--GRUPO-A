import grpc
from concurrent import futures
import sys
import os

# Agregar proto al path
sys.path.append(os.path.join(os.path.dirname(__file__), '../proto'))

import service_pb2
import service_pb2_grpc

class MyService(service_pb2_grpc.MyServiceServicer):
    def MyMethod(self, request, context):
        return service_pb2.MyResponse(result=f"Recibido: {request.param}")

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    service_pb2_grpc.add_MyServiceServicer_to_server(MyService(), server)
    server.add_insecure_port('[::]:9090')
    server.start()
    print("gRPC server corriendo en puerto 9090...")
    server.wait_for_termination()

if __name__ == '__main__':
    serve()
