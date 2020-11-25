#!/bin/bash
set -e

cp "licenses/UH000${INDEX}_license_key.txt" ./license_key.txt
cp "syncs/Sync${INDEX}.xml" conf/Sync.xml

exec "$@"
