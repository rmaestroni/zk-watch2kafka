#!/usr/bin/env bash

# check required options
[[ -z $LOG_LEVEL ]] && echo 'LOG_LEVEL was not provided' >&2 && exit 1
[[ -z $JAVA_OPTIONS ]] && echo 'JAVA_OPTIONS was not provided' >&2 && exit 1

exec java "${JAVA_OPTIONS}" \
  -cp "dependency/*:./*" \
  -Dlogging.level=${LOG_LEVEL} \
  cloud.thh.zk_watch2kafka.App \
  "$@"
