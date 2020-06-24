
description = "Spring Starter for automatically setting up the Spring Core implementation in a Spring Boot Application"

dependencies {
    api(project(":java-dynamic-sqs-listener-spring:java-dynamic-sqs-listener-spring-core"))

    testImplementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework:spring-tx")
    testImplementation("org.springframework:spring-test")
    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.springframework.boot:spring-boot-starter-aop")
    testImplementation(project(":util:elasticmq-sqs-client"))
}