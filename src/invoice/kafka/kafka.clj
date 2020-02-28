(ns invoice.kafka.kafka)

(def config {:kafka.serde/config {:schema-registry/base-url "http://localhost:8081"}
             :kafka/config       {:bootstrap.servers "localhost:9092"}})
