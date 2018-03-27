# Simple REST API file manager

This is a simple file server which provides REST API using Eclipse Micro Profile(MP).


## Prerequisite

This program depends several software/tools. See the list below and prepare them.

- Java 8 SDK
- Eclipse
- Gradle
- Payara Micro 5.181 (https://www.payara.fish/)
  - 5.181 supports MP1.2


## How to build

Run build with gradle.

Your build will fail at the first build because the server isn't running.
If you see the failure at the ':test' task, you have to run the server like as the next topic.
After running the server, you can re-run the build task and it will success.


## How to run

Build uber jar and run as:

```
> makeUberJarAndRun.bat
```


## How to use

We assume that you run the server on host as ```localhost```,
port as ```8080```.
(Please replace them with your environment variables)

### get a file list

```$ curl http://localhost:8080/filemanager/rest/files```

### post(register) a file

```$ curl -F "file=@yourfile" http://localhost:8080/filemanager/rest/files/```

### get the file (```yourfile```)

```$ curl http://localhost:8080/filemanager/rest/files/yourfile```

### delete the file (```yourfile```)

```$ curl -X DELETE http://localhost:8080/filemanager/rest/files/yourfile```


Enjoy!
