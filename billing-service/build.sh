#!/bin/bash
echo "Building billing-service..."

# Run generate-sources first to download protoc plugins
./mvnw generate-sources 2>/dev/null || true

# Fix permissions on protoc plugins
chmod +x target/protoc-plugins/*.exe 2>/dev/null || true
xattr -d com.apple.quarantine target/protoc-plugins/*.exe 2>/dev/null || true

# Full clean build
./mvnw clean install

echo "Done!"