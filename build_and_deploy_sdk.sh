#!/usr/bin/env bash

# Exit on error
set -e

git stash

git checkout release

git merge --strategy-option theirs origin/master-2.0

git stash apply

./gradlew clean uploadArchives

git add .

git commit -m 'new sdk release'

git push

git checkout master-2.0
