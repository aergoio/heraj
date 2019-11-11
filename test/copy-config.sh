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

readonly IT_PROJECTS=(
  "core/transport"
  "client/wallet"
  "client/smart-contract"
)
readonly TEST_RESOURCES_PATH="src/test/resources/hera/it"
readonly COPY_TARGETS=(
  "aergo.properties"
  "config/cert/*"
)

for project in "${IT_PROJECTS[@]}"; do
  TARGET_PATH="$PROJECT_HOME/$project/$TEST_RESOURCES_PATH"
  for target in "${COPY_TARGETS[@]}"; do
    cp -R ${SCRIPT_HOME}/${target} ${TARGET_PATH}
  done
done
