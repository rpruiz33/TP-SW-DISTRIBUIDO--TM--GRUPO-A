# TP-SW-DISTRIBUIDO--TM--GRUPO-A

* Comando para levantar docker: `docker compose -f docker-compose-kafka.yml up -d` 
* Comando para actualizar stub en grpc-client: `python -m grpc_tools.protoc -I=./proto --python_out=./proto --grpc_python_out=./proto ./proto/service.proto`
* Comando para correr grpc-client: `python app.py`
