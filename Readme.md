# Introduction

A simple CLI Client to Distributed Servers file sharing system implemented in Java using RMI (Remote Method Invocation) for a 2nd Year assignment as part of my Masters Degree in Computer Science.


The file sharing occurs between a client, which maintains a folder of files it has access to, and a front-end for a set of distributed servers. The system comprises up to 3 running file servers and a front-end. A client can only communicate with the system through the front-end, while the front-end can directly communicate with the file servers.

The system can accept file upload / download requests and other relevant operations from a client through the front-end. For each file upload request, the system will allocate the file for upload to a file server, which has the smallest number of files stored across all currently available servers. The client can also specify a “high reliability” option in a file upload request to flag the system replicating the uploaded file to all available servers. For a file download request, a client can download the requested file from any server which contains that file. For deleting a file, the system ensures all replicas (if any) of the requested file are deleted accordingly.

Any file server may stop working randomly. A failed server may be recovered and become workable again. The distributed system is able to monitor the failing and recovery of all servers. The client does not have any knowledge about the status of any individual file server.

Dynamic restarting of failed servers was not implemented.

## Usage

The connections between the client and frontend, and frontend and servers are made over the registry service.

To compile all sources:

```
cd src
javac */*.java
```

Next, start the registry service with the following command in terminal:

```
rmiregistry 1099
```

To start the server:
```
cd src
javac */*.java
java Server.TCPServer
```

In separate terminal windows, start the servers, frontend and client from the `src` directory as follows:
```
java -classpath . Server.TCPServer server1
java -classpath . Server.TCPServer server2
java -classpath . Server.TCPServer server3
java -classpath . Frontend.Frontend
java -classpath . Client.TCPClient
```

If you don't run into any problems, you should see the following output from the running servers and frontend:
``` 
Creating server1 skeleton object...
Locating the registry...
Binding the skeleton object to the registry...
server1 skeleton object running...
```

``` 
Servers running...
server1 ready.
server2 ready.
server3 ready.
Front end running...
```

The client has a simple CLI with several basic commands:
* CONN - connect to the server
* LIST - list the available files for download from the server
* DWLD - download a file (input the name of the file after calling)
* DELF - delete a file from the server (input the name of the file after calling)
* QUIT - quit the client

Files are stored in the local `files` folder within the `Client` and `Server` packages within `src`.

