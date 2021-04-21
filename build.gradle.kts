plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.30"
    id("org.jetbrains.kotlin.kapt") version "1.4.30"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.30"
    id("groovy")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("io.micronaut.application") version "1.4.2"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("codenarc")
    id("jacoco")
}

version = "0.1"
group = "br.com.kafkautils"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

micronaut {
    runtime("netty")
    testRuntime("spock2")
    processing {
        incremental(true)
        annotations("br.com.kafkautils.*")
    }
}

dependencies {
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.security:micronaut-security-annotations")
    kapt("org.mapstruct:mapstruct-processor:1.4.2.Final")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-multitenancy")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.beanvalidation:micronaut-hibernate-validator")
    implementation("io.micronaut.r2dbc:micronaut-r2dbc-core")
    implementation("io.micronaut.r2dbc:micronaut-data-r2dbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("com.password4j:password4j:1.5.3")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.testcontainers:spock")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:testcontainers")
    compileOnly("org.graalvm.nativeimage:svm")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.rxjava3:micronaut-rxjava3")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("dev.miku:r2dbc-mysql")
}

extensions.getByType(CodeNarcExtension::class.java).toolVersion = "2.1.0"
tasks.withType(CodeNarc::class.java) {
    group = "verification"
}

application {
    mainClass.set("br.com.kafkautils.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = BigDecimal("0.8")
            }
            excludes = listOf(
                "br.com.kafkautils.Api",
                "br.com.kafkautils.ApplicationKt"
            )
        }
    }
}
