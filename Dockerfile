FROM ubuntu:16.04
COPY iolite.jar /etc/iolite/
COPY .iolite /root/.iolite/
RUN apt-get update
RUN apt-get install -y default-jdk
#CMD java -jar /etc/iolite/iolite.jar -hccPort 8080
CMD ["nohup","java","-jar","/etc/iolite/iolite.jar","-hccPort","8080","&"]
