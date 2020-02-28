(ns invoice.core
  (:require [ring.util.http-response :refer [ok conflict]]
            [schema.core :as sc]
            [compojure.api.sweet :refer [api routes GET POST ANY]]
            [ring.adapter.jetty :refer [run-jetty]]
            [invoice.kafka.consumers.invoice-command-results :as invoice-command-results]
            [invoice.kafka.producers.invoice-commands :as invoice-commands-producer]))

; domain / api
(sc/defschema CreateInvoiceRequestSchema
             {:customer sc/Str
                :items [sc/Str]})

; TODO: Add items
(defn create-invoice-handler [create-invoice-req]
  (let [command-id (invoice-commands-producer/produce nil {:customer (:customer create-invoice-req)})]
      (let [result (invoice-command-results/wait-for command-id)]
            (let [snapshot (:new-snapshot (:outcome result))]
              (if (nil? snapshot) (conflict (:cause (:outcome result))) (ok snapshot))))))

(def invoice-routes
  [(POST "/invoices" []
     :body [create-invoice-req CreateInvoiceRequestSchema]
     (create-invoice-handler create-invoice-req))])

(def app (api {} (apply routes invoice-routes)))

(defn -main
  [& args]
  (run-jetty app {:port 3000}))
