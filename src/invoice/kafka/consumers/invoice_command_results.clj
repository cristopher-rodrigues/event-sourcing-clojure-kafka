(ns invoice.kafka.consumers.invoice-command-results
  (:require [invoice.kafka.kafka :as kafka]
            [kafka-avro-confluent.v2.deserializer :as des])
  (:import
    (org.apache.kafka.clients.consumer KafkaConsumer)
    (org.apache.kafka.common.serialization ByteArrayDeserializer StringDeserializer)))

(def dess (des/->avro-deserializer (:kafka.serde/config kafka/config)))

(def topic "invoice-commands")

(defn- build-consumer
  "Create the consumer instance to consume
from the provided kafka topic name"
  []
  (let [consumer-props                                      ; use kafka/config and merge deserializers
        {"bootstrap.servers", "localhost:9092"
         "group.id",          "My-Group3"
         "key.deserializer",  StringDeserializer
         "value.deserializer", ByteArrayDeserializer
         "auto.offset.reset", "earliest"
         "enable.auto.commit", "true"}]
    (doto (KafkaConsumer. consumer-props)
      (.subscribe [topic]))))

(def consumer (build-consumer))

(defn wait-for [command-uuid]
  (while true
    (let [records (.poll consumer 100)]
      (doseq [record records]
        (println "Sending on value"
                 (str "Value: " (.value record)))
        (println (.deserialize dess topic (.value record)))))
    (.commitAsync consumer)))
