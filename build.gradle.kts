plugins {
    id("org.springframework.boot") version "3.4.5"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.cyclonedx.bom") version "1.10.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"

}

group = "gy.roach.monitor"
version = "0.0.1-SNAPSHOT"

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
// Set the GraalVM home directory
val graalVmHome = System.getenv("GRAALVM_HOME") ?: "/Users/steveroach/Library/Java/JavaVirtualMachines/graalvm-jdk-23.0.2/Contents/Home"
tasks.withType<org.gradle.api.tasks.JavaExec> {
    jvmArgs = listOf("-XX:+UnlockExperimentalVMOptions", "-XX:+UseJVMCINativeLibrary")
    environment("JAVA_HOME", graalVmHome)
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
        // Set the GraalVM home directory
        vendor.set(JvmVendorSpec.matching("GraalVM"))
        implementation.set(JvmImplementation.VENDOR_SPECIFIC)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // OpenAPI Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.5.0")

    // Markdown processing
    implementation("org.commonmark:commonmark:0.21.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.21.0")

    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
