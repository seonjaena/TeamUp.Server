#!/bin/bash

GIT_BRANCH=""

if [ -n "$BRANCH_NAME" ]; then
  GIT_BRANCH=$BRANCH_NAME
else
  GIT_BRANCH=$(git symbolic-ref --short HEAD)
fi

PROJECT_NAME=$(./gradlew properties | grep -Po '(?<=name: ).*')
PROJECT_VER=$(./gradlew properties | grep -Po '(?<=version: ).*')

docker build -t $PROJECT_NAME:$PROJECT_VER-$GIT_BRANCH .