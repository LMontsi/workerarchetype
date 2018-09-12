#!/usr/bin/env bash

function echoBold () {
    echo $'\n\e[1m'"${1}"$'\e[0m'
}

#create namespace
echoBold 'Creating camunda-worker-compliance namespace...'
kubectl create namespace camunda-worker-compliance

# set namespace
kubectl config set-context $(kubectl config current-context) --namespace=camunda-worker-compliance

echoBold 'Deploying Camunda Worker...'
kubectl create -f kubernetes-deployment.yaml