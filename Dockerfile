FROM openjdk:17-alpine

# 외부 환경요소 : jar이 위치할 공간을 지정
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

# JAR_FILE을 아래의 이름으로 COPY하겠다.
COPY ${JAR_FILE} chameleon.jar

# env, java는 CMD가 아니라 jar를 실행시키기 위해 entrypoint 
ENTRYPOINT ["java", "-jar", "/chameleon.jar"]