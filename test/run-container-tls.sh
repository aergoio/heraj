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
readonly DEFAULT_AERGO_VERSION=$(grep defaultAergoVersion ${AERGO_PROPERTIES} | cut -d"=" -f2)
readonly AERGO_NODE=$(grep aergoNodeName ${AERGO_PROPERTIES} | cut -d"=" -f2)

readonly AERGO_VERSION=${1:-$DEFAULT_AERGO_VERSION}


###############################
### Main

echo "Run container: $AERGO_NODE"
docker run -d --log-driver json-file --log-opt max-size=1000m --log-opt max-file=7 \
  -v ${SCRIPT_HOME}/config:/aergo \
  -p 7845:7845 -p 7846:7846 -p 6060:6060 \
  --restart="always" --name ${AERGO_NODE} \
  aergo/node:${AERGO_VERSION} \
  aergosvr --home /aergo --config /aergo/config-tls.toml
