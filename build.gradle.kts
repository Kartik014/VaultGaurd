plugins {
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	kotlin("plugin.serialization") version "2.1.0"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.1")
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	//Supabase Dependencies
	implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
	implementation("io.ktor:ktor-client-core:3.1.3")
	implementation("io.ktor:ktor-client-cio:3.1.3")
	implementation("io.ktor:ktor-client-serialization:3.1.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

	//PostgreSQL Dependencies
	implementation("org.postgresql:postgresql:42.7.2")

	//MongoDB Dependencies
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.mongodb:mongodb-driver-sync:4.10.2")
	implementation("org.mongodb:mongodb-driver-core:4.10.2")

	//JSON PARSER
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

	// JWT TOKEN DEPENDENCY
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

	// Web client Dependency
	implementation("org.springframework.boot:spring-boot-starter-webflux")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.register<Jar>("fatJar") {
	group = "build"
	archiveClassifier.set("fat")
	manifest {
		attributes(
			"Main-Class" to "com.example.VaultGuard.ApplicationKt"
		)
	}
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	from(sourceSets.main.get().output)
	dependsOn(configurations.runtimeClasspath)
	from({
		configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
	})
}

tasks.withType<Test> {
	useJUnitPlatform()
}
