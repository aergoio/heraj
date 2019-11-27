#!/bin/bash

readonly KEYSTORE_FILE="JavaKeyStoreTest.keystore.p12"
readonly KEYSTORE_TYPE="pkcs12"
readonly KEYSTORE_PASSWORD="password"

readonly KEY_ALIAS="keyalias"
readonly KEY_PASSWORD="password"

keytool -genkeypair \
  -keyalg EC -keysize 256 -sigalg SHA256WithECDSA -alias ${KEY_ALIAS} -keypass ${KEY_PASSWORD} \
  -keystore ${KEYSTORE_FILE} -storetype ${KEYSTORE_TYPE} -storepass ${KEYSTORE_PASSWORD}
