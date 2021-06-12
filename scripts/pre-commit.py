#!/usr/bin/env python
import os

os.system("git stash -q --keep-index")
result = os.system("./gradlew test")
os.system("git stash pop -q")
exit(result)
