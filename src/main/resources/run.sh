#!/bin/bash

# Set the CA name, default to "localCA" if not provided
CA_NAME=${1:-localCA}

# Generate CA private key
openssl genrsa -out ${CA_NAME}.key 4096

# Check if the configuration file exists, create it if not
if [[ ! -f "${CA_NAME}.conf" ]]; then
  echo "Creating default configuration file ${CA_NAME}.conf..."
  cat > ${CA_NAME}.conf <<EOF
[ req ]
default_bits       = 4096
distinguished_name = req_distinguished_name
x509_extensions    = v3_ca
prompt             = no

[ req_distinguished_name ]
C  = US
ST = State
L  = Locality
O  = Organization
CN = ${CA_NAME}

[ v3_ca ]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = CA:true
keyUsage = keyCertSign, cRLSign
EOF
fi

# Create CA certificate using the configuration file
openssl req -x509 -new -nodes -key ${CA_NAME}.key -config ${CA_NAME}.conf -sha256 -days 1024 -out ${CA_NAME}.crt

# Export the public key into a trustStore in PKCS12 format
# Using keytool to create a Java-compatible trustStore
keytool -importcert -file ${CA_NAME}.crt -alias ${CA_NAME} -storepass secret -noprompt -storetype PKCS12 \
  -keystore ${CA_NAME}.p12

echo "Certificate and keystore created successfully."
