#!/bin/bash

GIT_BRANCH=""

gradle wrapper

PROJECT_VER=$(./gradlew properties | grep -Po '(?<=version: ).*')

if [ -n "$BRANCH_NAME" ]; then
  GIT_BRANCH=$BRANCH_NAME
  docker buildx build --platform linux/amd64 -t teamup.server:$PROJECT_VER-$GIT_BRANCH .
else
  GIT_BRANCH=$(git symbolic-ref --short HEAD)
  docker build -t teamup.server:$PROJECT_VER-$GIT_BRANCH .
fi