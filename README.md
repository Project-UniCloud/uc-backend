# uc-backend

![alt text](project-dependency-graph.svg)

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=Project-UniCloud_uc-backend)](https://sonarcloud.io/summary/new_code?id=Project-UniCloud_uc-backend)

## Running instruction
To run application from IntelliJ, you have to add environment variable. Just follow these steps:
1. Click Edit Configurations...

![1](1.png)

2. Enable Environment variables

![2](2.png)

3. Add environment variable named POSTGRES_HOST and value localhost

![3](3.png)

4. Just run/debug application

## Remote debugging
To debug application running as docker container, you have to follow these steps:
1. Click Edit Configurations...

![1](1.png)

2. Click Add New Configuration

![4](4.png)

3. Find Remote JVM Debug

![5](5.png)

4. Name your configuration, leave default options and hit Apply

![6](6.png)

5. Just click debug and enjoy debugging :)