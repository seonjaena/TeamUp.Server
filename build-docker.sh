#!/bin/bash

GIT_BRANCH=""

if [ -n "$BRANCH_NAME" ]; then
  GIT_BRANCH=$BRANCH_NAME
else
  GIT_BRANCH=$(git symbolic-ref --short HEAD)
fi

PROJECT_VER=$(./gradlew properties | grep -Po '(?<=version: ).*')

gradle wrapper

docker build -t teamup.server:$PROJECT_VER-$GIT_BRANCH .