# Install Camunda Platform

## Goal

The goal of this lab is to install the Camunda Platform running on Wildfly application server.

## Short Description

* Download Camunda Platform. [Choose the full distribution for Wildfly enterprise version](https://docs.camunda.org/enterprise/download/) for this training.
* Unzip the downloaded package.
* Run the `start-camunda.{bat|sh}` file.
* Login to the Cockpit and Tasklist.

## Detailed Steps

1. Download the [Enterprise Edition of Camunda Platform for Wildfly](https://docs.camunda.org/enterprise/download/)
2. Unzip the archive file to your harddrive
3. Check if Java 8 or later is installed on your computer. Open a command line window and enter `java -version`. It should answer:
```
openjdk version "11.0.6-internal" 2020-01-14
```
or something similar like:
```
java version "1.8.0_211"
```
If you got
```
"java" is not recognized as an internal or external command, operable program or batch file.
```
check if java is installed on your computer and install as needed. For Windows environments be sure to add `JAVA_HOME` environmental variable and point it to the installed JDK and/or add `JRE_HOME` environmental variable and point it to the installed JRE. Close the command line window.
4. Navigate into the extracted archive with the explorer and double click `start-camnda{bat|sh}`. Check the output for the content
```
WildFly Full 26.0.1.Final (WildFly Core 18.0.4.Final) started in XXXXXms
```
5. Open your browser and point to [localhost:8080/camunda/app/welcome/default/](localhost:8080/camunda/app/welcome/default/) and login with the user `demo`and the password `demo`.
6. Accept or decline the telemetry meta and usage statistics collection.
7. Inspect the Cockpit, Tasklist, and Admin.
8. Congratulations, you have successfully installed Camunda Platform on Wildfly.