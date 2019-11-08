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
readonly AERGO_VERSION=$(grep aergoVersion ${AERGO_PROPERTIES} | cut -d"=" -f2)
readonly AERGO_NODE=$(grep aergoNodeName ${AERGO_PROPERTIES} | cut -d"=" -f2)

echo "Remove container: $AERGO_NODE"
readonly CONTAINER=$(docker ps -q -f name=${AERGO_NODE})
if [ ! -z ${CONTAINER} ]; then
  docker stop ${CONTAINER}
  docker rm ${CONTAINER}
fi
