# kafka.py
from kafka import KafkaProducer, KafkaConsumer
import json
import threading

# Configuración de Kafka
KAFKA_BROKER = 'localhost:9092'

# ---------------------------
# PRODUCER
# ---------------------------
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BROKER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def enviar_mensaje(topic, mensaje):
    """
    Envía un mensaje JSON a un topic de Kafka
    """
    producer.send(topic, mensaje)
    producer.flush()
    print(f"Mensaje enviado a {topic}: {mensaje}")

# ---------------------------
# CONSUMER (en segundo plano)
# ---------------------------
consumers = {}

def consumir_topic(topic, callback):
    """
    Inicia un consumer de Kafka en un thread separado
    """
    if topic in consumers:
        print(f"Consumer para {topic} ya iniciado")
        return

    def run_consumer():
        consumer = KafkaConsumer(
            topic,
            bootstrap_servers=KAFKA_BROKER,
            auto_offset_reset='earliest',
            group_id=f"group-{topic}",
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )
        print(f"Consumer escuchando en {topic}")
        for message in consumer:
            callback(message.value)

    t = threading.Thread(target=run_consumer, daemon=True)
    t.start()
    consumers[topic] = t
