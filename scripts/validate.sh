#!/usr/bin/env bash
# Validation script to check if all configurations are correct

set -e

echo "=== Kafka Playground Validation ==="
echo ""

# Check Maven
echo "Checking Maven..."
if command -v mvn &> /dev/null; then
    echo "✓ Maven found: $(mvn -version | head -1)"
else
    echo "✗ Maven not found"
    exit 1
fi

# Check Docker
echo ""
echo "Checking Docker..."
if command -v docker &> /dev/null; then
    echo "✓ Docker found: $(docker --version)"
else
    echo "✗ Docker not found"
    exit 1
fi

# Check Docker Compose
echo ""
echo "Checking Docker Compose..."
if docker compose version &> /dev/null; then
    echo "✓ Docker Compose found: $(docker compose version)"
else
    echo "✗ Docker Compose not found"
    exit 1
fi

# Validate Docker Compose configuration
echo ""
echo "Validating docker-compose.yml..."
if docker compose config --quiet; then
    echo "✓ docker-compose.yml is valid"
else
    echo "✗ docker-compose.yml has errors"
    exit 1
fi

# Validate Maven POM
echo ""
echo "Validating pom.xml..."
if mvn validate &> /dev/null; then
    echo "✓ pom.xml is valid"
else
    echo "✗ pom.xml has errors"
    exit 1
fi

# Try to compile
echo ""
echo "Compiling Java sources..."
if mvn clean compile &> /dev/null; then
    echo "✓ Java sources compiled successfully"
else
    echo "✗ Compilation failed"
    exit 1
fi

# Check if Nix is available
echo ""
echo "Checking Nix (optional for devenv)..."
if command -v nix &> /dev/null; then
    echo "✓ Nix found: $(nix --version)"
    
    # Try to evaluate flake
    echo ""
    echo "Validating flake.nix..."
    if nix flake check . 2>&1 | grep -q "error"; then
        echo "✗ flake.nix has errors"
    else
        echo "✓ flake.nix appears valid"
    fi
else
    echo "⚠ Nix not found (required for devenv shell)"
fi

echo ""
echo "=== Validation Complete ==="
echo ""
echo "All critical checks passed! ✓"
echo ""
echo "Next steps:"
echo "1. If you have Nix installed, run: devenv shell"
echo "2. Start Kafka: start-kafka (or docker compose up -d)"
echo "3. Run examples: run-producer, run-consumer, run-streams"
