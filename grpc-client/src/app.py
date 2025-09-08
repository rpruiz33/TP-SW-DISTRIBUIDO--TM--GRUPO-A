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
def update_user():
    data = request.json
    user = User.query.filter_by(username=data["username"]).first()
    if not user:
        return jsonify({"success": False, "message": "Usuario no encontrado"}), 404
    
    user.name = data.get("nombre", user.name)
    user.lastName = data.get("apellido", user.lastName)
    user.phone = data.get("telefono", user.phone)
    user.email = data.get("email", user.email)
    user.role = data.get("rol", user.role)
    db.session.commit()

    return jsonify({"success": True, "message": "Usuario actualizado"}), 200

if __name__ == "__main__":
    app.run(port=5000, debug=True)
