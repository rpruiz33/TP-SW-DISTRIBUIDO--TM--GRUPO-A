# kafka_client.py
from kafka import KafkaProducer
import json

# ✅ Configuración única de Kafka Producer
producer = KafkaProducer(
    bootstrap_servers='localhost:29092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# 1️⃣ Solicitar donaciones
def solicitar_donaciones(data):
    """
    Publica una solicitud de donaciones en el topic '/solicitud-donaciones'.
    data: {
        "id_organizacion_solicitante": int,
        "id_solicitud": int,
        "donaciones": [{"categoria": str, "descripcion": str}]
    }
    """
    topic = "solicitud-donaciones"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Solicitud publicada en {topic}"}


# 2️⃣ Transferir donaciones
def transferir_donaciones(id_org_solicitante, data):
    """
    Publica en el topic '/transferencia-donaciones/{id-org-solicitante}'.
    data: {
        "id_solicitud": int,
        "id_organizacion_donante": int,
        "donaciones": [{"categoria": str, "descripcion": str, "cantidad": "2kg"}]
    }
    """
    topic = f"transferencia-donaciones{id_org_solicitante}"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Transferencia publicada en {topic}"}


# 3️⃣ Ofrecer donaciones
def ofrecer_donaciones(data):
    """
    Publica una oferta de donaciones en '/oferta-donaciones'.
    data: {
        "id_oferta": int,
        "id_organizacion_donante": int,
        "donaciones": [{"categoria": str, "descripcion": str, "cantidad": "2kg"}]
    }
    """
    topic = "oferta-donaciones"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Oferta publicada en {topic}"}


# 4️⃣ Baja de solicitud
def baja_solicitud_donaciones(data):
    """
    Publica la baja de una solicitud en '/baja-solicitud-donaciones'.
    data: {
        "id_organizacion_solicitante": int,
        "id_solicitud": int
    }
    """
    topic = "baja-solicitud-donaciones"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Baja de solicitud publicada en {topic}"}


# 5️⃣ Publicar evento solidario
def publicar_evento(data):
    """
    Publica un evento en '/eventos-solidarios'.
    data: {
        "id_organizacion": int,
        "id_evento": int,
        "nombre": str,
        "descripcion": str,
        "fecha_hora": str
    }
    """
    topic = "eventossolidarios"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Evento publicado en {topic}"}


# 6️⃣ Baja evento
def baja_evento(data):
    """
    Publica una baja de evento en '/baja-evento-solidario'.
    data: {
        "id_organizacion": int,
        "id_evento": int
    }
    """
    topic = "baja-evento-solidario"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Baja de evento publicada en {topic}"}


# 7️⃣ Adhesión a evento
def adhesion_evento(id_organizador, data):
    """
    Publica una adhesión en '/adhesion-evento/{id-organizador}'.
    data: {
        "id_evento": int,
        "voluntario": str,
        "id_organizacion": int,
        "id_voluntario": int,
        "nombre": str,
        "apellido": str,
        "telefono": str,
        "email": str
    }
    """
    topic = f"adhesion-evento{id_organizador}"
    producer.send(topic, data)
    producer.flush()
    return {"success": True, "message": f"Adhesión publicada en {topic}"}
