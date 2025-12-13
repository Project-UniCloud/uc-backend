#!/bin/sh
set -e

if [ -f "$LDAP_CERT_PATH" ]; then
    echo "[INFO] Importing LDAP root CA..."

    # Prepare writable truststore path for non-root user
    TRUSTSTORE_PATH=${CUSTOM_TRUSTSTORE:-/app/cacerts}
    TRUSTSTORE_PASS=${TRUSTSTORE_PASSWORD:-changeit}

    # Ensure base directory exists
    mkdir -p "$(dirname "$TRUSTSTORE_PATH")"

    # If no custom truststore yet, seed it from the JRE default (read-only)
    if [ ! -f "$TRUSTSTORE_PATH" ]; then
        if [ -f "$JAVA_HOME/lib/security/cacerts" ]; then
            cp "$JAVA_HOME/lib/security/cacerts" "$TRUSTSTORE_PATH"
        else
            # Create an empty truststore if default one is missing
            echo "[WARN] Default cacerts not found, creating new truststore at $TRUSTSTORE_PATH"
            keytool -genkeypair -alias temp-initialize -keystore "$TRUSTSTORE_PATH" -storepass "$TRUSTSTORE_PASS" -keypass "$TRUSTSTORE_PASS" -dname "CN=init" -keyalg RSA -keysize 2048 -validity 1 >/dev/null 2>&1 || true
            keytool -delete -alias temp-initialize -keystore "$TRUSTSTORE_PATH" -storepass "$TRUSTSTORE_PASS" >/dev/null 2>&1 || true
        fi
    fi

    # Import the provided LDAP CA into our writable truststore
    keytool -import -noprompt \
        -trustcacerts \
        -alias labs-wmi-rootCA \
        -file "$LDAP_CERT_PATH" \
        -keystore "$TRUSTSTORE_PATH" \
        -storepass "$TRUSTSTORE_PASS"

    # Ensure JVM uses the custom truststore
    export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Djavax.net.ssl.trustStore=$TRUSTSTORE_PATH -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASS"
else
    echo "[INFO] No LDAP cert found at $LDAP_CERT_PATH, skipping import"
fi

echo "[INFO] Starting Spring Boot application..."
exec "$@"
