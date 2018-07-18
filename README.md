# Docker of the IOLITE Multimodal Interaction Project

This Project is about developing an application and driver for the IOLITE Plattform. The project is based on a multi modal interation aproach. The app with be a SlackBot and the driver will make it possible to communicato over a bluetooth ring with the iolite box.

## Getting Started
If you are in the showroom and just want to test our app skip the following explanation and jump to section Installation.
To get started replace the .iolite folder in the main directory with the configuration of iolite you woud like to use inside the docker container. Also we provided you with two default ownes in the folder "configurations". Please copy one .iolite folder from the local or showroom folder towards the root folder of the repository. After that you can start building the docker image and start the container.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

## Installation Docker Image and Container
You need to pull the repository of the branch docker and navigate to the dockerfile in your commandline.

```
cd PATH_TO_DOCKERFILE
docker build -t iolite .
docker run -d -p 8080:8080 iolite
docker run -i -t iolite /bin/bash
```

## Login for the basic 
Use http://localhost:8080/hcc/login.html
Username:admin
Password:admin


## Debbuging
docker port <containerid>
