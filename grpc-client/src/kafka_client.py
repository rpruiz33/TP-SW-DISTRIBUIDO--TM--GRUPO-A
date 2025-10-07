from kafka import KafkaProducer
import json

# Configuración del producer apuntando al listener del host
producer = KafkaProducer(
    bootstrap_servers='localhost:29092',  # <--- puerto host
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def enviar_operacion_kafka(mensaje):
    """
    Envía un mensaje al tópico Kafka 'operaciones'.
    Recibe un diccionario y devuelve un dict con status.
    """
    try:
        producer.send('operaciones', mensaje)
        producer.flush()
        return {"success": True, "message": "Mensaje enviado a Kafka"}
    except Exception as e:
        return {"success": False, "message": f"No se pudo enviar a Kafka: {str(e)}"}


def enviar_mensaje(topic, mensaje):
    producer.send(topic, mensaje)
    producer.flush()  # asegura que se envíe inmediatamente
    print(f"Mensaje enviado a Kafka ({topic}): {mensaje}")

# 1️⃣ Publicar eventos solidarios
def publicar_evento(evento):
    """
    Publica un evento en el topic '/eventossolidarios'.
    evento: dict con keys ['id_org', 'id_evento', 'nombre', 'descripcion', 'fecha_hora']
    """
    try:
        producer.send('eventossolidarios', evento)
        producer.flush()
        return {"success": True, "message": "Evento publicado"}
    except Exception as e:
        return {"success": False, "message": f"No se pudo publicar evento: {str(e)}"}

# 2️⃣ Baja de evento
def baja_evento(evento):
    """
    Publica un mensaje de baja de evento en el topic '/baja-evento-solidario'.
    evento: dict con keys ['id_org', 'id_evento']
    """
    try:
        producer.send('baja-evento-solidario', evento)
        producer.flush()
        return {"success": True, "message": "Baja de evento enviada"}
    except Exception as e:
        return {"success": False, "message": f"No se pudo enviar baja de evento: {str(e)}"}

# 3️⃣ Adhesión a evento
def adhesionar_evento(id_organizador, adhesor):
    """
    Publica un mensaje de adhesión en el topic '/adhesion-evento/id-organizador'.
    id_organizador: int o str
    adhesor: dict con keys ['id_evento', 'voluntario', 'id_org', 'id_voluntario', 'nombre', 'apellido', 'telefono', 'email']
    """
    try:
        topic = f"adhesion-evento-{id_organizador}"
        producer.send(topic, adhesor)
        producer.flush()
        return {"success": True, "message": "Adhesión enviada"}
    except Exception as e:
        return {"success": False, "message": f"No se pudo enviar adhesión: {str(e)}"}
