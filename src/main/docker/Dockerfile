#############################################################
 # 
 #   Copyright (c) 2019 Commscope India. All rights reserved.
 # 
 #
 #  
 # @version 1.0
 # @since September 27, 2019
 # @author Prashant Kumar
 #
############################################################

FROM openjdk:8-alpine

ENV DB_USERNAME=root
ENV DB_PASSWORD=root
ENV DB_SERVICE=tr069adapter-mariadb
ENV DB_NAME=dmsdb
ENV FACTORY_PORT=8089

WORKDIR /opt/CSAdapter/

COPY ./csfactory-1.0.0.jar ./lib/csfactory-1.0.0.jar

EXPOSE ${FACTORY_PORT}
ENTRYPOINT ["java", "-jar", "./lib/csfactory-1.0.0.jar"]
