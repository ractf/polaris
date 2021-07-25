#!/usr/bin/env bash
if [ -z $(POLARIS_CONTROLLER+x) ]; then
  /bin/java -jar /opt/polaris/polaris.jar node /etc/polaris/node.yml
else
  /bin/java -jar /opt/polaris/polaris.jar controller /etc/polaris/controller.yml
fi