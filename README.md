# uc-backend

![alt text](project-dependency-graph.svg)

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=Project-UniCloud_uc-backend)](https://sonarcloud.io/summary/new_code?id=Project-UniCloud_uc-backend)

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

## Importing postman collection
1. Go to localhost:8080/swagger-ui/index.html
    ![7](7.png)
2. Copy all
   ![8](8.png)
3. Open Postman and click import
   ![9](9.png)
4. Paste copied text
   ![10](10.png)