from flask import Flask, request, jsonify
from flask_cors import CORS
from grpc_client import MyServiceClient
from google.protobuf.json_format import MessageToJson
from flask_sqlalchemy import SQLAlchemy
from flask import Flask, request, jsonify
from kafka_client import (
    solicitar_donaciones,
    transferir_donaciones,
    ofrecer_donaciones,
    baja_solicitud_donaciones,
    publicar_evento,
    baja_evento,
    adhesion_evento
)


app = Flask(__name__)
CORS(app)  # Permite llamadas desde React u otros dominios
# Configuración de la DB MySQL


# Inicializa cliente gRPC
grpc_client = MyServiceClient(host='localhost', port=9090)

# GLOBAL: almacena el token 
current_user = {
    "token": None,
}

# ============================
# WRAPPER PARA LLAMADAS GRPC
# ============================
def grpc_call_with_token(stub_method, *args, **kwargs):
    """
    Llama un método gRPC agregando el token del usuario actual en metadata.
    Esto evita repetir la metadata con token en todas las rutas.
    """
    metadata = []

    
    if current_user["token"]:
        metadata.append(("authorization", f"Bearer {current_user['token']}"))



    # Pasamos metadata como argumento a la llamada gRPC
    return stub_method(*args, metadata=metadata, **kwargs)


# ============================
# RUTAS PÚBLICAS
# ============================
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

        if grpc_response.success:
            # Guardamos token
            current_user["token"] = grpc_response.token

        return jsonify({
            "success": grpc_response.success,
            "message": grpc_response.message,
            "role_name": grpc_response.role_name
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ============================
# RUTAS PROTEGIDAS (usa wrapper)
# ============================
@app.route("/api/altauser", methods=["POST"])
def altaUser():
    data = request.json
    try:
        grpc_response = grpc_call_with_token(grpc_client.altaUser,
            data.get("username"),
            data.get("nombre"),
            data.get("apellido"),
            data.get("telefono"),
            data.get("email"),
            data.get("rol")
            )
        
        return jsonify({
            "success": grpc_response.success,
            "message": grpc_response.message
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/userlist", methods=["GET"])
def getAllUsers():
    try:
        grpc_response = grpc_call_with_token(grpc_client.getAllUsers)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/activeuserlist", methods=["GET"])
def getActiveUsers():
    try:
        grpc_response = grpc_call_with_token(grpc_client.getActiveUsers)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/deleteuser/<string:username>", methods=["PUT"])
def deleteUser(username):
    try:
        response = grpc_call_with_token(grpc_client.deleteUser, username)
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
        response = grpc_call_with_token(
            grpc_client.updateUser,
            data.get("username"),
            data.get("nombre"),
            data.get("apellido"),
            data.get("telefono"),
            data.get("email"),
            data.get("rol"),
            data.get("oldUsername"),
            data.get("oldEmail")
        )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error modificando usuario", "error": str(e)}), 500

# ============================
# RUTAS DE DONACIONES
# ============================
@app.route("/api/donationlist", methods=["GET"])
def getAllDonations():
    try:
        grpc_response = grpc_call_with_token(grpc_client.getAllDonations)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/activedonationlist", methods=["GET"])
def getActiveDonations():
    try:
        grpc_response = grpc_call_with_token(grpc_client.getActiveDonations)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/api/updatedonation", methods=["PUT"])
def updateDonation():
    data = request.json
    try:
        response = grpc_call_with_token(
            grpc_client.updateDonation,
            data.get("id"),
            data.get("category"),
            data.get("description"),
            data.get("amount"),
            data.get("username")
        )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error modificando donacion", "error": str(e)}), 500

@app.route("/api/deletedonation", methods=["PUT"])
def deleteDonation():
    data = request.json
    try:
        response = grpc_call_with_token(grpc_client.deleteDonation, data.get("id"), data.get("username"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error eliminando donacion", "error": str(e)}), 500

# ============================
# RUTAS DE EVENTOS
# ============================
@app.route("/api/eventlist", methods=["GET"])
def getAllEvents():
    try:
        grpc_response = grpc_call_with_token(grpc_client.getAllEvents)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/createevent", methods=["POST"])
def createEvent():
    data = request.json
    try:
        grpc_response = grpc_call_with_token(
            grpc_client.createEvent,
            data.get("nameEvent"),
            data.get("descriptionEvent"),
            data.get("dateRegistration")
        )
        return jsonify({
            "success": grpc_response.success,
            "message": grpc_response.message
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/updateevent", methods=["PUT"])
def updateEvent():
    data = request.json
    try:
        response = grpc_call_with_token(
            grpc_client.updateEvent,
            data.get("id"),
            data.get("nameEvent"),
            data.get("descriptionEvent"),
            data.get("dateRegistration")
        )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error modificando evento", "error": str(e)}), 500

@app.route("/api/deleteevent/<int:id>", methods=["DELETE"])
def deleteEvent(id):
    try:
        response = grpc_call_with_token(grpc_client.deleteEvent, id)
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error eliminando evento", "error": str(e)}), 500

# ============================
# RUTAS DE MEMBER/DONATIONAT EVENT
# ============================
@app.route("/api/togglemember", methods=["PUT"])
def toggleMember():
    data = request.json
    try:
        response = grpc_call_with_token(
            grpc_client.toggleMemberToEvent,
            data.get("eventId"),
            data.get("username"),
            data.get("alreadyAssigned")
        )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error asignando miembro", "error": str(e)}), 500

@app.route("/api/createdonationatevent", methods=["POST"])
def createDonationAtEvent():
    data = request.json
    try:
        response = grpc_call_with_token(
            grpc_client.createDonationAtEvent,
            data.get("idEvent"),
            data.get("description"),
            data.get("quantityDelivered"),
            data.get("username")
        )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error registrando donacion en evento", "error": str(e)}), 500

@app.route("/api/updatedonationatevent", methods=["POST"])
def updateDonationAtEvent():
    data = request.json
    try:
        response = grpc_call_with_token(
            grpc_client.updateDonationAtEvent,
            data.get("idEvent"),
            data.get("description"),
            data.get("quantityDelivered"),
            data.get("username")
        )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error update donation", "error": str(e)}), 500

@app.route("/api/assigneddonationlist/<int:id>", methods=["GET"])
def getAllDonationsAtEvent(id):
    try:
        grpc_response = grpc_call_with_token(grpc_client.getAllDonationsAtEvent, id)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ---------------------------
# RUTAS KAFKA
# ---------------------------

#TRAER SOLICITUDES 
@app.route("/api/requestlist", methods=["GET"])
def getRequestList():
    is_external = request.args.get("isExternal", default="false").lower() == "true"
    try:
        grpc_response = grpc_call_with_token(grpc_client.getRequestList, is_external)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500

#SOLICITAR DONACIONES
@app.route("/api/requestdonation", methods=["POST"])
def api_solicitar_donaciones():
    
    data = request.json
    try:
        response = grpc_call_with_token(grpc_client.requestDonation, data.get("idOperationMessage"),data.get("operationType"),data.get("donations") )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error generando solicitud", "error": str(e)}), 500


#TRANSFERIR DONACIONES
@app.route("/api/transferdonations", methods=["POST"])
def api_transferir_donaciones():
    data = request.json
    try:
        response = grpc_call_with_token(
            grpc_client.transferDonations, 
            data.get("idOperationMessage"),
            data.get("operationType"),
            data.get("donations"),
            data.get("idOrganization") 
        )
        
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error generando transferencia", "error": str(e)}), 500



# 3️⃣ Ofrecer donaciones
@app.route("/api/ofrecer-donaciones", methods=["POST"])
def api_ofrecer_donaciones():
    data = request.get_json()
    return jsonify(ofrecer_donaciones(data))


#DAR DE BAJA SOLICITUD
@app.route("/api/deleterequest", methods=["POST"])
def deleteRequest():
    data = request.json
    try:
        response = grpc_call_with_token(grpc_client.deleteRequest, data.get("idOperationMessage"),data.get("operationType") )
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error generando solicitud", "error": str(e)}), 500

#TRAER EVENTOS EXTERNOS
@app.route("/api/externalevents", methods=["GET"])
def getExternalEventList():
    try:
        grpc_response = grpc_call_with_token(grpc_client.getExternalEventList)
        json_response = MessageToJson(grpc_response)
        return json_response
    except Exception as e:
        return jsonify({"error": str(e)}), 500


#PUBLICAR EVENTO
@app.route("/api/publishevent/<int:id>", methods=["POST"])
def publishEvent(id):
    try:
        response = grpc_call_with_token(grpc_client.publishEvent, id)
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error publicando evento", "error": str(e)}), 500


#ADHESION A EVENTO
@app.route("/api/eventadhesion", methods=["POST"])
def eventAdhesion():
    data = request.json
    print("api")
    try:
        response = grpc_call_with_token(grpc_client.eventAdhesion, data.get("idExternalEvent"),data.get("emailVolunteer"))
        return jsonify({
            "success": response.success,
            "message": response.message
        })
    except Exception as e:
        return jsonify({"message": "Error generando adhesion", "error": str(e)}), 500


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
    
