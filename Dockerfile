FROM gradle:7.1.1-jdk11
ADD . /polaris
WORKDIR /polaris/
RUN gradle polaris-application:buildRpm

FROM centos:7
COPY --from=0 /polaris/polaris-application/build/distributions/ /polaris/.
RUN yum install -y /polaris/*