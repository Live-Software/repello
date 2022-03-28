# Repello
## Introduction
Repello is a board game, played by two to four players. The goal is to take as many tokens from the board as possible,
by knocking them down with one's tower and other tokens.
## Installation
The project uses maven, thus it can be installed the same way as any other maven project.
In root directory:
```
mvn install
```

To do a clean installation (remove any previously created artifacts, to make sure there are no clashes with previous files):
```
mvn clean install
```
This will create .jar files in the respective target directories.

To only build one of the modules, enter the corresponding subdirectories, and execute the commands inside.
```
cd game
mvn clean install
```
or
```
cd lobby-server
mvn clean install
```
## Running the application
Installing the maven project should create the required .jar files in the corresponding target directories.

To run the application, run it like any other java application:
```
java -jar game/target/game-<version>.jar
```
or
```
java -jar lobby-server/target/lobby-server-<version>.jar
```

## Starting Dev RabbitMQ broker
The connection between game clients and the lobby server is established through a message queue.

A docker container is provided for development purposes, so it is not required to connect to the production broker.

A pre-requirement is to have docker and docker-compose installed on the dev system.

To build the container, run (```rabbitmq``` is the name of the folder, where the ```Dockerfile``` is located)
```
docker build rabbitmq
```
If the build was successful, the container can be started with (where ```rabbitmq/docercompose.yaml``` is the location
of the compose file)
```
docker-compose -f rabbitmq/docercompose.yaml up -d
```
There should be 3 containers starting, since we are using a cluster (a kind of back-up system, if one fails the two
other can take over). If it is successful, the management view can be accessed from a local browser through
```http://localhost:15672```. There is a default administrator user which can be easily accessed:
```
Username: guest
Password: guest
```