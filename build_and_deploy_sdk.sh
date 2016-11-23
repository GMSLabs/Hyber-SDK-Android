#!/usr/bin/env bash

# Exit on error
set -e

git checkout release

git merge --strategy-option theirs origin/master-2.0

./gradlew clean uploadArchives

git add .

git commit -m 'new sdk release'

git push

git checkout master-2.0