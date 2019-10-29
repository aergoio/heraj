#!/bin/bash -e

# Resolve script home
SOURCE="${BASH_SOURCE[0]}"
# resolve $SOURCE until the file is no longer a symlink
while [ -h "$SOURCE" ]; do
  SCRIPT_HOME="$( cd -P "$( dirname "$SOURCE" )" >/dev/null && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
  [[ $SOURCE != /* ]] && SOURCE="$SCRIPT_HOME/$SOURCE"
done
readonly SCRIPT_HOME="$( cd -P "$( dirname "$SOURCE" )" >/dev/null && pwd )"

###############################
### Aergo Config

readonly AERGO_PROPERTIES="$SCRIPT_HOME/aergo.properties"
readonly AERGO_NODE=$(grep aergoNodeName ${AERGO_PROPERTIES} | cut -d"=" -f2)

# files
readonly TARGET_DIR="$SCRIPT_HOME/config/cert"
readonly SERVER_KEY=$TARGET_DIR/"server.key"
readonly SERVER_CERT="$TARGET_DIR/server.crt"
readonly CLIENT_KEY="$TARGET_DIR/client.key"
readonly CLIENT_PEM="$TARGET_DIR/client.pem"
readonly CLIENT_CERT_REQUEST="$TARGET_DIR/client.csr"
readonly CLIENT_CERT="$TARGET_DIR/client.crt"

# cert config
readonly COUNTRY="KR"
readonly STATE=""
readonly LOCALITY="seoul"
readonly ORGANIZATIION="aergo"
readonly ORGANIZATIION_UNIT="aergo"
readonly SERVER_COMMON_NAME=${AERGO_NODE}
readonly CLIENT_COMMON_NAME="aergo.client"

# make cert dir
mkdir -p ${TARGET_DIR}

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
