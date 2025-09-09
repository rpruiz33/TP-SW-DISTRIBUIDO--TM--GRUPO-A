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
            "message": grpc_response.message,
            "role_name": grpc_response.role_name
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
        grpc_response = grpc_client.getAllUsers()
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/deleteuser/<string:username>", methods=["DELETE"])
def deleteUser(username):
    print("Llego al backend con:", username)  # <-- log para test
    try:
        response = grpc_client.deleteUser(username)
        return jsonify({"message": f"Usuario {username} eliminado", "response": str(response)})
    except Exception as e:
        print("Error en delete_user:", e)
        return jsonify({"message": "Error eliminando usuario", "error": str(e)}), 500

@app.route("/api/updateuser", methods=["PUT"])
def updateUser():
    data = request.json
    try:
        response = grpc_client.updateUser(data.get("username"), data.get("nombre"),data.get("apellido"),data.get("telefono"),data.get("email"), data.get("rol"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        print("Error en update user:", e)
        return jsonify({"message": "Error modificando usuario", "error": str(e)}), 500


@app.route("/api/donationlist", methods=["GET"])
def getAllDonations():
    try:
        grpc_response = grpc_client.getAllDonations()
        json_response = MessageToJson(grpc_response)

        print("salimos de la llamada grpc")
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(port=5000, debug=True)
