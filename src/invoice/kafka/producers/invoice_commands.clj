(ns invoice.kafka.producers.invoice-commands
  (:require [kafka-clj-utils.producers :as kp]
            [invoice.kafka.producers.base :as producer]))

; TODO Add items on the command payload
(def avro-schema {:type "record"
    :name "InvoiceCommands"
    :fields [{
    :name  "origin_uuid",
    :type "string"
    }, {
        :name "command_uuid",
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

(defn produce [expected-version, command-payload]
  (let [bundle     {:avro-schema avro-schema
                    :topic-name  "invoice-commands"
                    :records     [{
                                   :origin_uuid "origin-uuid",
                                   :command_uuid "command-uuid",
                                   :expected_version expected-version,
                                   :payload command-payload
                                   }]}]
    (producer/produce bundle)))
