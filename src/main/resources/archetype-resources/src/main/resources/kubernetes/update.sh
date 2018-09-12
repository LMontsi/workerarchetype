#!/usr/bin/env bash

function echoBold () {
    echo $'\n\e[1m'"${1}"$'\e[0m'
}

# set namespace
kubectl config set-context $(kubectl config current-context) --namespace=camunda-worker-compliance

echoBold 'Updating Camunda Worker...'
kubectl replace -f kubernetes-deployment.yaml