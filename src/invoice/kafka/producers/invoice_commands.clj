(ns invoice.kafka.producers.invoice-commands
  (:require [invoice.kafka.producers.base :as producer]))

; TODO Add items on the command payload
(def avro-schema {:type "record"
    :name "InvoiceCommands"
    :fields [{
    :name  "origin_id",
    :type "string"
    }, {
        :name "command_id",
        :type "string"
        }, {
            :name  "expected_version",
            :type ["null", "int"]
            }, {
                :name "payload",
                :type [{
                        :type "record",
                        :name  "CreateInvoice",
                        :fields [{
                                  :name "customer",
                                  :type "string"
                                  }]
                        }]
                }]
    })

; TODO How to send the key?
(defn produce [expected-version, command-payload]
  (let [command-id (.toString (java.util.UUID/randomUUID))
        bundle     {:avro-schema avro-schema
                    :topic-name  "invoice-commands"
                    :records     [{
                                   :origin_id "origin-uuid",
                                   :command_id command-id,
                                   :expected_version expected-version,
                                   :payload command-payload
                                   }]}]
    (producer/produce bundle)
    command-id))
