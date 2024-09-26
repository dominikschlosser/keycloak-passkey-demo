#!/bin/bash

source <(sed -E 's/^/export /' .env)

./dist/target/dist/bin/kc.sh -v start-dev --debug 
