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
        grpc_response = grpc_client.altaUser(data.get("username"), data.get("nombre"), data.get("apellido"), data.get("telefono"), data.get("email"), data.get("rol"))
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

@app.route("/api/deleteuser/<string:username>", methods=["PUT"])
def deleteUser(username):
    try:
        response = grpc_client.deleteUser(username)
        return jsonify({            
            "success": response.success,
            "message": response.message
            })
    except Exception as e:
        return jsonify({"message": "Error eliminando usuario", "error": str(e)}), 500

@app.route("/api/updateuser", methods=["PUT"])
def updateUser():
    data = request.json
    try:
        response = grpc_client.updateUser(data.get("username"), data.get("nombre"), data.get("apellido"), data.get("telefono"), data.get("email"), data.get("rol"))
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
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    
@app.route("/api/updatedonation", methods=["PUT"])
def updateDonation():
    data = request.json
    try:
        response = grpc_client.updateDonation(data.get("id"), data.get("category"), data.get("description"), data.get("amount"))
        print(response)
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        print("Error en update donation:", e)
        return jsonify({"message": "Error modificando donacion", "error": str(e)}), 500

@app.route("/api/eventlist", methods=["GET"])
def getAllEvents():
    try:
        grpc_response = grpc_client.getAllEvents()
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/createevent", methods=["POST"])
def createEvent():
    data = request.json
    try:
        grpc_response = grpc_client.createEvent(data.get("nameEvent"), data.get("descriptionEvent"), data.get("dateRegistration"))
        return jsonify({
            "success": grpc_response.success,
            "message": grpc_response.message,
            "eventId": grpc_response.eventId
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/updateevent", methods=["PUT"])
def updateEvent():
    data = request.json
    try:
        response = grpc_client.updateEvent(data.get("id"), data.get("nameEvent"), data.get("descriptionEvent"), data.get("dateRegistration"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        print("Error en update event:", e)
        return jsonify({"message": "Error modificando evento", "error": str(e)}), 500

@app.route("/api/deleteevent/<int:id>", methods=["DELETE"])
def deleteEvent(id):
    try:
        response = grpc_client.deleteEvent(id)
        return jsonify({"message": f"Evento {id} eliminado", "response": str(response)})
    except Exception as e:
        print("Error en delete_event:", e)
        return jsonify({"message": "Error eliminando evento", "error": str(e)}), 500

@app.route("/api/assignmember", methods=["POST"])
def assignMember():
    data = request.json
    try:
        response = grpc_client.assignMemberToEvent(data.get("eventId"), data.get("username"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        print("Error en assign member:", e)
        return jsonify({"message": "Error asignando miembro", "error": str(e)}), 500

@app.route("/api/removemember", methods=["POST"])
def removeMember():
    data = request.json
    try:
        response = grpc_client.removeMemberFromEvent(data.get("eventId"), data.get("username"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        print("Error en remove member:", e)
        return jsonify({"message": "Error quitando miembro", "error": str(e)}), 500

@app.route("/api/registerdelivery", methods=["POST"])
def registerDelivery():
    data = request.json
    try:
        response = grpc_client.registerDelivery(data.get("donationId"), data.get("eventId"), data.get("quantity"), data.get("registeredBy"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        print("Error en register delivery:", e)
        return jsonify({"message": "Error registrando entrega", "error": str(e)}), 500

if __name__ == "__main__":
    app.run(port=5000, debug=True)