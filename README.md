#Readme
##Getting Started
If you are in the showroom and just want to test our app skip the following explanation and jump to section Installation.
To get started replace the .iolite folder in the main directory with the configuration of iolite you woud like to use inside the docker container. Also we provided you with two default ownes in the folder "configurations". Please copy one .iolite folder from the local or showroom folder towards the root folder of the repository. After that you can start building the docker image and start the container.

##Installation Docker Image and Container
cd PATH_TO_DOCKERFILE
docker build -t iolite .
docker run -d -p 8080:8080 iolite
docker run -i -t iolite /bin/bash

##Login for the basic 
Use http://localhost:8080/hcc/login.html
Username:admin
Password:admin


##Debbuging
docker port <containerid>