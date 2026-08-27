#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Get the current date in YYYY.MM.DD-HH.MM format
RELEASE_VERSION=$(date +'%Y.%m.%d-%H.%M')
echo $RELEASE_VERSION
# Ensure RELEASE_VERSION is set
if [ -z "$RELEASE_VERSION" ]; then
  echo "RELEASE_VERSION is not set"
  exit 1
fi
# Backup the current package.json
package_backup=$(mktemp)
cp package.json "$package_backup"
trap 'mv "$package_backup" package.json' EXIT

# Update package.json with the nightly version
node -e "
const fs = require('fs');
const path = './package.json';
const pkg = require(path);
pkg.version = \`\${pkg.version}-nightly-${RELEASE_VERSION}\`;
fs.writeFileSync(path, JSON.stringify(pkg, null, 2));
"

# Publish the package with the nightly tag
npm publish --tag nightly
