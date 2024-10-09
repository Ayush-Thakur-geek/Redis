#!/bin/bash

redis-cli PING &
redis-cli PING &
redis-cli ECHO hey &
redis-cli Echo Ayush &
redis-cli SET foo bar PX 800 &&
sleep 0.2 && redis-cli GET foo &&
sleep 0.9 && redis-cli GET foo &
redis-cli CONFIG GET dir &
redis-cli SET bash src &&
sleep 0.2 && redis-cli SET Ayush Thakur &&
sleep 0.4 && redis-cli KEYS "*"

