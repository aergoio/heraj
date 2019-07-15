#!/bin/bash -e

SERVER_KEY="server.key"
SERVER_CERT="server.crt"
CLIENT_KEY="client.key"
CLIENT_PEM="client.pem"
CLIENT_CERT_REQUEST="client.csr"
CLIENT_CERT="client.crt"

COUNTRY="KR"
STATE=""
LOCALITY="seoul"
ORGANIZATIION="aergo"
ORGANIZATIION_UNIT="aergo"
SERVER_COMMON_NAME="aergo.node"
CLIENT_COMMON_NAME="aergo.client"

# create server key
openssl genrsa -out ${SERVER_KEY} 2048
chmod 600 ${SERVER_KEY}

# create server crt
openssl req -new -x509 -sha256 -key ${SERVER_KEY} -out ${SERVER_CERT} -days 3650 \
  -subj "/C=${COUNTRY}/ST=${STATE}/L=${LOCALITY}/O=${ORGANIZATIION}/OU=${ORGANIZATIION_UNIT}/CN=${SERVER_COMMON_NAME}"

# create server key
openssl genrsa -out ${CLIENT_KEY} 2048
chmod 600 ${CLIENT_KEY}

# create client csr
openssl req -new -sha256 -key ${CLIENT_KEY} -out ${CLIENT_CERT_REQUEST} \
  -subj "/C=${COUNTRY}/ST=${STATE}/L=${LOCALITY}/O=${ORGANIZATIION}/OU=${ORGANIZATIION_UNIT}/CN=${CLIENT_COMMON_NAME}"

# create client crt
openssl x509 -req -sha256 -days 3650 -in ${CLIENT_CERT_REQUEST} -CA ${SERVER_CERT} -CAcreateserial -CAkey ${SERVER_KEY} -out ${CLIENT_CERT}

# pkcs1 to pkcs8
openssl pkcs8 -topk8 -nocrypt -in ${CLIENT_KEY} -out ${CLIENT_PEM}
chmod 600 ${CLIENT_PEM}

# show cert infos
echo "---- Server cert -----"
openssl x509 -text -in ${SERVER_CERT}
echo ""

echo "----- Client cert -----"
openssl x509 -text -in ${CLIENT_CERT}
echo ""
