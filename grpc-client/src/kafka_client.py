# kafka.py
from kafka import KafkaProducer
import json

producer = KafkaProducer(
    bootstrap_servers='localhost:9092',  # ajusta según tu Kafka
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def enviar_mensaje(topic, mensaje):
    producer.send(topic, mensaje)
    producer.flush()  # asegura que se envíe inmediatamente
    print(f"Mensaje enviado a Kafka ({topic}): {mensaje}")
