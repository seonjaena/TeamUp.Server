# Team Up Platform Server Program

## About Program
This is a program that helps carry out team projects.<br/>
You can make private or public channel and invite team members to your channel.<br/>
Private channels are not visible to others.<br/>
You can safely work on projects with authenticated users in private channels.<br/>
Public channels are visible to others.<br/>
Others may ask to join your team.<br/>
If you create a profile, leaders of other teams that need members can scout you.<br/>
---

## Development Principle
### Assign one ticket per function.

### Logging level
#### trace
```text

```

#### debug
```text

```

#### info
```text

```

#### warn
```text
* User Input is not appropriate
* Unauthenticated request
* Unauthorized request
```

#### error
```text
// Error log makes alarm and this makes developers tired.

* System Internal Error
```
---

## Technical Spec

### Spec List
1. Spring Boot
2. Spring Data JPA
3. Spring Security 
4. Querydsl 
5. MariaDB

### Why Use

#### Querydsl
> Use Querydsl because it is difficult to use dynamic queries when using only Spring Data JPA.
---

## How To Build & Run
```shell
# make jar file (with test)
gradle clean build -Pprofile={profile}

# make jar file (without teset)
gradle clean build -x test -Pprofile={profile}

# docker build (only for linux)
./build-docker.sh

# run docker image
docker run --rm --name ${container name} -p 8080:8080 teamup:${docker image version}
```
