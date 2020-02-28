(ns invoice.kafka.producers.base
  (:require [kafka-clj-utils.producers :as kp]
            [invoice.kafka.kafka :as kafka]))

(def producer (kp/->producer kafka/config))

(defn produce [bundle]
  (kp/publish-avro-bundle producer bundle))
