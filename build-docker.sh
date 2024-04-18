#!/bin/bash

GIT_BRANCH=""

if [ -n "$BRANCH_NAME" ]; then
  GIT_BRANCH=$BRANCH_NAME
else
  GIT_BRANCH=$(git symbolic-ref --short HEAD)
fi

gradle wrapper

PROJECT_VER=$(./gradlew properties | grep -Po '(?<=version: ).*')

docker build -t teamup.server:$PROJECT_VER-$GIT_BRANCH .