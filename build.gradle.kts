plugins {
    java
}

group = "com.elangsegara.webautomation"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Selenium WebDriver
    implementation("org.seleniumhq.selenium:selenium-java:4.29.0")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.17")

    implementation("com.openai:openai-java:0.30.0")
    implementation("com.google.code.gson:gson:2.12.1")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.12.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

sourceSets {
    test {
        java {
            srcDirs("src/test/java")
        }
    }
}