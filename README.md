# Team Up Platform Server Program

## About Program
This is a program that helps carry out team projects.<br/>
You can make private or public channel and invite team members to your channel.<br/>
Private channels are not visible to others.<br/>
You can safely work on projects with authenticated users in private channels.<br/>
Public channels are visible to others.<br/>
Others may ask to join your team.<br/>
If you create a profile, leaders of other teams that need members can scout you.<br/>

## Technical Spec

### Spec List
1. Spring Boot
2. Spring Data JPA
3. Querydsl
4. MariaDB
5. GraalVM

### Why Use

#### Querydsl
> Use Querydsl because it is difficult to use dynamic queries when using only Spring Data JPA.

#### GraalVM
> When using a JVM, the build result is generated as a jar file, so the Docker image must include a JRE. Therefore, use GraalVM, which builds with a single executable file.<br/><br/>
> Use GraalVM because it uses less CPU when running.


## How To Build & Run
```shell
# make docker image
gradle bootBuildImage

# run docker image
docker run --rm --name ${container name} -p 8080:8080 teamup:${docker image version}
```
