import com.commercehub.gradle.plugin.avro.GenerateAvroJavaTask

description = "Includes a second example of a service producing messages whose schema is registered in the Spring Cloud Schema Registry\n and is in a different format to the first."

plugins {
    id("org.springframework.boot")
    id("com.commercehub.gradle.plugin.avro-base")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("software.amazon.awssdk:sqs")
    implementation(project(":util:avro-spring-cloud-schema-registry-sqs-client"))
}

val generateAvro = tasks.register<GenerateAvroJavaTask>("generateAvro") {
    setSource("src/main/resources/avro")
    setOutputDir(file("build/generated/sources/avro"))
}

tasks.named<JavaCompile>("compileJava").configure {
    source(generateAvro)
}