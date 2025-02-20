default: setup

run:
	docker exec -it invoice-commands-handler-stream-app /bin/sh -c "?"

app:
	docker exec -it invoice-commands-handler-stream-app bash

up:
	docker-compose up --abort-on-container-exit

down:
	docker-compose down -v

create-invoice-request:
	curl -v -X POST \
	  http://localhost:3000/invoices \
	  -H 'Content-Type: application/json' \
	  -d '{"customer": "fake customer", "items": ["fake item"]}'

topics-list:
	docker exec -it invoice-commands-handler-stream-tools /bin/sh \
	  -c "/opt/confluent-5.3.1/bin/kafka-topics --list --bootstrap-server broker:29092"

tests:
	docker start invoice-commands-handler-stream-tests && \
		docker exec -it invoice-commands-handler-stream-tests /bin/sh -c "cd app && ?"

consume-invoice-commands:
	docker exec -it invoice-commands-handler-stream-tools /opt/confluent-5.3.1/bin/kafka-avro-console-consumer --topic invoice-commands  \
            --bootstrap-server broker:29092 \
            --property schema.registry.url=http://kafka-schema-registry:8081 \
            --property print.key=true \
            --key-deserializer=org.apache.kafka.common.serialization.StringDeserializer \
            --from-beginning
