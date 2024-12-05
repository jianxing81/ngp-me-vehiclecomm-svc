# README #

This template will help you to setup build files for build and deployment onto AWS CodePipeline using blue-green deployment. Please read carefully on how to modify the template based on your need.

### What is this repository for? ###

* Dev team who want to bootstrap thier repository with CI/CD without the help of devops team.
* DevOpser who is helping dev team to setup pipeline.

### How do I get set up? ###

* Copy the the ```codebuild``` folder in the template directory (e.g: ```standard-ecs-blue-green/codebuild```) and paste it into your source code root folder.
* Your repo code structure would looks like this:
![Alt text](../../images/repostructure.png)
* To modify the build files to your need, further reading on readme of the specific template.

### How do I modify the buildfile based on my need? ###

1. For modifying build step, your only concern is the buildspec.yaml file
* You can modify the env block declaration for modify your app specific env, secret, and parameter store (Other common and critical env like ECR_URI, SONAR related, target deployment configuration will be maintained and overwritten using CODEBUILD env - which is maintained by DevOps team).
```
######################################
# DEV TEAM CAN MODIFY ENV BLOCK
# For App specific variable, Dev team can CRUD themself in this file env block.
# For more detail you can look into this guide: https://blog.shikisoft.com/define-environment-vars-aws-codebuild-buildspec/
######################################
env:
  # Plain text variable configuration (Dev team can modify this)
  variables:
    CPU: 1024
    SIDECAR_CPU: 256
    APP_CPU: 512
    SIDECAR_MEMORY: 512
    MEMORY: 2048
    APP_MEMORY: 1024
    CONTAINER_PORT: 8080

  # If you want to add variable from parameter store, do as follow:
  # parameter-store:
  #   S3_BUCKET: "my_s3_bucket"
  #   ORG_BUCKET: "/my_org/s3_bucket"
  #   DB_USERNAME: "db_username"

  # If you want to add app specific secret environment, do as follow:
  # secrets-manager:
  #   ParameterName: "<secret-name>:<secret-key>"
```
* You can modify the build phase command to your build need. You should keep the DOCKER TAG command the same for everything to work seemlessly
```
  ######################################
  # DEV TEAM CAN MODIFY THIS BUILD PHASE ACCORDING TO THIER APP REQUIREMENT 
  ######################################
  build:
    on-failure: ABORT
    commands:
      # MODIFY YOUR DOCKER BUILD CODE HERE:
      - >-
        docker build --platform linux/amd64 
        --build-arg SONAR_TOKEN=$SONAR_TOKEN 
        --build-arg SONAR_HOST=$SONAR_HOST 
        --build-arg SONAR_PROJECT=$SONAR_PROJECT 
        --build-arg SONAR_ORGANIZATION=$SONAR_ORGANIZATION 
        --build-arg SONAR_BRANCH=$SONAR_BRANCH 
        --build-arg ECR_URI=$ECR_URI 
        --build-arg profile=$profile 
        -t $REPOSITORY_URI:latest .

      # DOCKER TAG
      - docker tag $REPOSITORY_URI:latest $REPOSITORY_URI:$IMAGE_TAG
```
* You can modify the cache block to modify folder you want to cache (Library folder, e.g: node_modules, mvn m2 library folder)
```
######################################
# DEV TEAM CAN MODIFY CACHE BLOCK
# Modify the path to the location you want to cache
# This default value is for caching maven library
######################################
cache:
  paths:
    - '/root/.gradle/caches/**/*'
```


2. For modifying run time container configuration, you can look into the containerdef.json file (NOT RECOMMEND UNLESS YOU KNOW WHAT YOU ARE DOING) 
* Normally you only need to modify this file when you want to modify container run time environment but you should do it in your dockerfile instead.

### Who own this template? ###

* Harley Tran Xuan Hai
* Tommy Tran Duc Thang

```Example
version: 0.2

env:
  variables:
    CPU: 1024
    SIDECAR_CPU: 256
    APP_CPU: 512
    SIDECAR_MEMORY: 512
    MEMORY: 2048
    APP_MEMORY: 1024
    CONTAINER_PORT: 8080

phases:

  pre_build:
    on-failure: ABORT
    commands:
      - echo ________ Connecting to Amazon ECR ________
      - aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin $ECR_URI

      - REPOSITORY_URI=$ECR_URI/$ECR_IMAGE_NAME
      - IMAGE_TAG=$(echo build_$(echo `date -d '+7 hours' +%F`)_$(echo `date -d '+7 hours' +%T`) | awk ' { gsub (":", ".")} 1 ')

      - echo ________ Downloading template taskdef.json ________
      - aws s3 sync $TEMPLATE_S3 ./codebuild/

  build:
    on-failure: ABORT
    commands:
      - >-
        docker build --platform linux/amd64 
        --build-arg SONAR_TOKEN=$SONAR_TOKEN 
        --build-arg SONAR_HOST=$SONAR_HOST 
        --build-arg SONAR_PROJECT=$SONAR_PROJECT 
        --build-arg SONAR_ORGANIZATION=$SONAR_ORGANIZATION 
        --build-arg SONAR_BRANCH=$SONAR_BRANCH 
        --build-arg ECR_URI=$ECR_URI 
        --build-arg profile=$profile 
        -t $REPOSITORY_URI:latest .
      - docker tag $REPOSITORY_URI:latest $REPOSITORY_URI:$IMAGE_TAG

  post_build:
    commands:
      - echo ________ Build completed on `date` ________
      - echo ________ Pushing the Docker images ________
      - docker push $REPOSITORY_URI:latest
      - docker push $REPOSITORY_URI:$IMAGE_TAG
      - echo ________ Writing taskdef.json file ________
      - export CONTAINERDEF=$(cat ./codebuild/containerdef.json)
      - echo $(jq ".containerDefinitions += [$CONTAINERDEF]" ./codebuild/taskdef.json) > ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{NAMESPACE}"'/'$NAMESPACE'/g'                ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{ENV}"'/'$ENV'/g'                            ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{APPNAME}"'/'$APPNAME'/g'                    ./codebuild/taskdef.json
      - sed -i.bak -e "s/\"{CONTAINER_PORT}\"/$CONTAINER_PORT/g"        ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{CPU}"'/'$CPU'/g'                            ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{MEMORY}"'/'$MEMORY'/g'                      ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"\"{SIDECAR_CPU}\""'/'$SIDECAR_CPU'/g'        ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"\"{SIDECAR_MEMORY}\""'/'$SIDECAR_MEMORY'/g'  ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"\"{APP_CPU}\""'/'$APP_CPU'/g'                ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"\"{APP_MEMORY}\""'/'$APP_MEMORY'/g'          ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{AWS_ACCOUNT_ID}"'/'$AWS_ACCOUNT_ID'/g'      ./codebuild/taskdef.json
      - sed -i.bak -e 's/'"{AWS_REGION}"'/'$AWS_REGION'/g'              ./codebuild/taskdef.json
      - echo ________ Writing imageDetail.json file ________
      - printf '{"ImageURI":"%s"}' $REPOSITORY_URI:$IMAGE_TAG  > imageDetail.json
      - echo ________ Writing appspec.yaml file ________
      - sed -i.bak -e 's/'"{NAMESPACE}"'/'$NAMESPACE'/g'                ./codebuild/appspec.yaml
      - sed -i.bak -e 's/'"{ENV}"'/'$ENV'/g'                            ./codebuild/appspec.yaml
      - sed -i.bak -e 's/'"{APPNAME}"'/'$APPNAME'/g'                    ./codebuild/appspec.yaml
      - sed -i.bak -e 's/'"{CONTAINER_PORT}"'/'$CONTAINER_PORT'/g'      ./codebuild/appspec.yaml
      - echo ________ ls ________
      - ls ./codebuild/
      - cat ./codebuild/taskdef.json
      - cat ./codebuild/appspec.yaml

cache:
  paths:
    - '/root/.gradle/caches/**/*'

artifacts:
  files:
    - imageDetail.json
    - ./codebuild/appspec.yaml
    - ./codebuild/taskdef.json
  discard-paths: yes
```