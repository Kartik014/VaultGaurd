package com.example.VaultGuard.services

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class StorageService(private val webClient: WebClient) {

    private val bucketName: String = "backups"

    fun uploadFile(userid: String, dbid: String, filename: String, fileContent: ByteArray, contentType: String): Mono<String> {
        val fullPath = "$userid/$dbid/$filename"
        return webClient.put()
            .uri("object/$bucketName/$fullPath")
            .header("Content-Type", contentType)
            .bodyValue(fileContent)
            .retrieve()
            .bodyToMono(String::class.java)
            .map{
                "https://jyrlcsjpkssmoliyhfcz.supabase.co/storage/v1/object/public/$bucketName/$fullPath"
            }
    }
}