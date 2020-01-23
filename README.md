# Clinico

Locally hosted webapp to aid nurses with language barrier problems in the triage process

Built for the TechChallenge at TUM

## Contributors

- Pavel Czempin
- Julius Herter
- Sezin Özer
- Julius Theye
- Milen Vitanov

## Setup Instructions

The source code of the project can be found at the following link:
https://github.com/PavelCz/TechChallenge-Clinico
Required packages to run the software: Java 8 or a newer version; Node.js. In case there is a need
to import the backend as a project, eclipse10 or IntelliJ IDEA11 can be used.

### Instructions to run the backend server:

1. Download ”Clinico.zip” from the repository and unzip it. The zip archive can be found in the downloaded repository or downloaded from [releases](https://github.com/PavelCz/TechChallenge-Clinico/releases)
2. Inside the unzipped directory ”Clinico”, you will find a file called ”Clinico.jar”. Double
click the file and the backend UI program will start. In case there is a problem run the jar
file through
the terminal by first navigating to its directory and then using the command
```java -jar Clinico.jar```
3. In case you want to import the source code in a project, it is located under the directory
”Clinico”. We tested an import in eclipse and IntelliJ IDEA, but other editors might also
work.
4. Now that you have started the backend program, it is time for the frontend client.

### Instructions to run the frontend client:

1. Download the directory ”NODE” from the repository.
2. Open the terminal and navigate inside the directory ”NODE”.
3. Run the command:
```npm install```
to install all necessary node dependencies.
Remark: Node.js is required for this part of the running process
4. Run the command:
npm start
to start the frontend program.
5. Open your favourite browser and go to the web address:
`http://localhost:3000`
This will open the frontend UI and you can start using it.
6. In case you want to use the frontend application on another device, you need to find out
what your local IP address is and insert it into the variable HOST inside the file `NODE/views/index`.
html (line 14). Then you can access the frontend web-page from the external device at
the address:
`http://[your local ip]:3000`
