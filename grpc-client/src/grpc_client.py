from flask import Flask, request, jsonify
import sys
import os
import grpc

# Agregar proto al path
sys.path.append(os.path.join(os.path.dirname(__file__), '../proto'))

import service_pb2
import service_pb2_grpc

app = Flask(__name__)

channel = grpc.insecure_channel('localhost:9090')
stub = service_pb2_grpc.MyServiceStub(channel)

@app.route('/')
def index():
    return "¡Flask funciona!"

@app.route('/api/do_something', methods=['POST'])
def do_something():
    data = request.json
    param = data.get('param', '')
    grpc_request = service_pb2.MyRequest(param=param)
    grpc_response = stub.MyMethod(grpc_request)
    return jsonify({'result': grpc_response.result})

@app.route('/api/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username', 'aa')
    password = data.get('password', '1234')
    grpc_request = service_pb2.LoginRequest(username=username, password=password)
    grpc_response = stub.Login(grpc_request)
    return jsonify({'success': grpc_response.success, 'message': grpc_response.message})

if __name__ == '__main__':
    app.run(port=5000, debug=True)