#!/bin/bash

readonly IT_FILE="ContractOperationIT"

echo "Remove payload files? (y|n)"
echo -n "> "
read answer
[[ $answer = "y" ]] && rm *payload > /dev/null 2>&1

LUAFILES=$(find . -name "*.lua")
for file in ${LUAFILES[@]}; do
  payload_file="$IT_FILE.$(echo $file | sed -e "s/\.lua//g" | sed -e "s/\.\///g" | sed "s/contract_//g")_payload"
  echo "Generating payload of \"$file\" into \"$payload_file\""
  payload=$(aergoluac --payload "$file" | sed -e "s/\\n//g")
  echo -n "$payload" > "$payload_file"
done

