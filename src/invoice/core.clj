(ns invoice.core
  (:require [ring.util.http-response :refer [ok]]
            [schema.core :as sc]
            [compojure.api.sweet :refer [api routes GET POST ANY]]
            [ring.adapter.jetty :refer [run-jetty]]
            [invoice.kafka.producers.invoice-commands :as invoice-commands-producer]))

; domain / api
(sc/defschema CreateInvoiceRequestSchema
             {:customer sc/Str
                :items [sc/Str]})

(defn create-invoice-handler [create-invoice-req]
  (invoice-commands-producer/produce nil {:customer (:customer create-invoice-req)})
  (ok create-invoice-req))

(def invoice-routes
  [(POST "/invoices" []
     :body [create-invoice-req CreateInvoiceRequestSchema]
     (create-invoice-handler create-invoice-req))])

(def app (api {} (apply routes invoice-routes)))

(defn -main
  [& args]
  (run-jetty app {:port 3000}))
