#!/bin/sh
set -e

if [ -f "$LDAP_CERT_PATH" ]; then
    echo "[INFO] Importing LDAP root CA..."
    keytool -import -noprompt \
        -trustcacerts \
        -alias labs-wmi-rootCA \
        -file "$LDAP_CERT_PATH" \
        -keystore "$JAVA_HOME/lib/security/cacerts" \
        -storepass changeit
else
    echo "[INFO] No LDAP cert found at $LDAP_CERT_PATH, skipping import"
fi

echo "[INFO] Starting Spring Boot application..."
exec "$@"
