from flask import Flask, request, jsonify
from flask_cors import CORS
from grpc_client import MyServiceClient
from google.protobuf.json_format import MessageToJson


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
    try:
        grpc_response = grpc_client.login(data.get("username"), data.get("password"))
        return jsonify({
            "success": grpc_response.success,
            "message": grpc_response.message
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/altauser", methods=["POST"])
def altaUser():
    data = request.json
    try:
        grpc_response = grpc_client.altaUser(data.get("username"), data.get("nombre"),data.get("apellido"),data.get("telefono"),data.get("email"), data.get("rol"))
        return jsonify({
            "success": grpc_response.success,
            "message": grpc_response.message
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    
@app.route("/api/userlist", methods=["GET"])
def getAllUsers():
    try:
        print("llega al endpoint")
        grpc_response = grpc_client.getAllUsers()
        # Convierte protobuf a JSON
        json_response = MessageToJson(grpc_response)
        print("post llamada a grpc")
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500



if __name__ == "__main__":
    app.run(port=5000, debug=True)
