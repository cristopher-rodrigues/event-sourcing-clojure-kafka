(ns invoice.kafka.consumers.invoice-command-results
  (:require [invoice.kafka.kafka :as kafka]
            [kafka-avro-confluent.v2.deserializer :as des])
  (:import
    (org.apache.kafka.clients.consumer KafkaConsumer)
    (org.apache.kafka.common.serialization ByteArrayDeserializer StringDeserializer)))

(def dess (des/->avro-deserializer (:kafka.serde/config kafka/config)))

(def topic "invoice-command-results")

; TODO: Move to a base consumer
(defn- build-consumer
  "Create the consumer instance to consume
from the provided kafka topic name"
  []
  (let [consumer-props                                      ; use kafka/config and merge deserializers
        {"bootstrap.servers", "localhost:9092"
         "group.id",          "My-Group32222"
         "key.deserializer",  StringDeserializer
         "value.deserializer", ByteArrayDeserializer
         "auto.offset.reset", "earliest"
         "enable.auto.commit", "true"}]
    (doto (KafkaConsumer. consumer-props)
      (.subscribe [topic]))))

(def consumer (build-consumer))

; TODO: compare origin_id also
(defn wait-for [command-id]
  (def result (atom nil))
  (while (nil? @result)
    (do
      (let [records (.poll consumer 100)]
        (doseq [record records]
          (let [msg (.deserialize dess topic (.value record))] ; only deserialize the command-id message verify the key
            (if (= command-id (:command-id msg)) (reset! result msg))))))
    (.commitAsync consumer))
  @result)
