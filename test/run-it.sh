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

readonly PROJECT_HOME=$(cd "$SCRIPT_HOME/.." && pwd)

###############################
### Aergo Config

readonly AERGO_PROPERTIES="$SCRIPT_HOME/aergo.properties"
readonly AERGO_VERSIONS=($(grep aergoVersions ${AERGO_PROPERTIES} | cut -d"=" -f2))


###############################
### Main

for aergo_version in "${AERGO_VERSIONS[@]}"; do
  # setup
  echo "Setup integration test env.."
  ${SCRIPT_HOME}/rm-container.sh
  ${SCRIPT_HOME}/setup-genesis.sh "$aergo_version"

  # plaintext
  echo "Run aergo node as plaintext.."
  ${SCRIPT_HOME}/run-container.sh "$aergo_version"
  pushd ${PROJECT_HOME} && ${PROJECT_HOME}/gradlew clean integrationTest && popd
  ${SCRIPT_HOME}/rm-container.sh

  # tls
  echo "Run aergo node with tls.."
  ${SCRIPT_HOME}/run-container-tls.sh "$aergo_version"
  pushd ${PROJECT_HOME} && ${PROJECT_HOME}/gradlew clean integrationTest && popd
  ${SCRIPT_HOME}/rm-container.sh
done
