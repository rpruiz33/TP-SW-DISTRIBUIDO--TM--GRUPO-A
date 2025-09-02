from flask import Flask, request, jsonify
from flask_cors import CORS
from grpc_client import MyServiceClient

app = Flask(__name__)
CORS(app)  # Permite llamadas desde React u otros dominios

# Inicializa cliente gRPC
grpc_client = MyServiceClient(host='localhost', port=9090)

@app.route("/")
def index():
    return "API Flask funcionando con gRPC!"

@app.route("/api/do_something", methods=["POST"])
def do_something():
    data = request.json
    param = data.get("param", "")
    grpc_response = grpc_client.my_method(param)
    return jsonify({"result": grpc_response.result})

@app.route("/api/login", methods=["POST"])
def login():
    data = request.json
    username = data.get("username", "")
    password = data.get("password", "")
    grpc_response = grpc_client.login(username, password)
    return jsonify({
        "success": grpc_response.success,
        "message": grpc_response.message
    })

if __name__ == "__main__":
    app.run(port=5000, debug=True)
