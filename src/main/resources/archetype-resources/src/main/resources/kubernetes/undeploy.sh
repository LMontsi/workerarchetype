#!/usr/bin/env bash

function echoBold () {
    echo $'\n\e[1m'"${1}"$'\e[0m'
}

# set namespace
kubectl config set-context $(kubectl config current-context) --namespace=camunda-worker-compliance

echoBold 'Deleting Camunda Worker...'
kubectl delete -f kubernetes-deployment.yaml