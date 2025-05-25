package com.example.VaultGuard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [MongoAutoConfiguration::class])
class VaultGuardApplication

fun main(args: Array<String>) {
	runApplication<VaultGuardApplication>(*args)
}
