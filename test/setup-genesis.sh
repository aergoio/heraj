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

readonly AERGO_PROPERTIES="$SCRIPT_HOME/aergo.properties"
readonly AERGO_VERSION=$(grep aergoVersion ${AERGO_PROPERTIES} | cut -d"=" -f2)

readonly DATA_DIR="$SCRIPT_HOME/config/data"
echo "Setup genesis with aergo version: $AERGO_VERSION"
if [ -d ${DATA_DIR} ]; then
  echo "Data directory exists.. remove $DATA_DIR"
  rm -rf ${DATA_DIR};
fi

docker run --rm \
  -v ${SCRIPT_HOME}/config:/aergo \
  aergo/node:${AERGO_VERSION} \
  aergosvr init --genesis /aergo/genesis.json --home /aergo --config /aergo/config.toml
