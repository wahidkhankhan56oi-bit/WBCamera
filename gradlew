#!/bin/sh
exec "$(dirname "$0")/gradle-dist/gradle-8.2/bin/gradle" "$@"
