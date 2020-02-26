(ns invoice.kafka.producers.base
  (:require [kafka-clj-utils.producers :as kp]))

(def config {:kafka.serde/config {:schema-registry/base-url "http://localhost:8081"}
             :kafka/config       {:bootstrap.servers "localhost:9092"}})

(def producer (kp/->producer config))

(defn produce [bundle]
  (kp/publish-avro-bundle producer bundle))
