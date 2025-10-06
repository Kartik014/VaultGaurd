package com.example.VaultGuard.filters

import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.validators.LogInValidator
import com.example.VaultGuard.validators.SignUpValidator
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import java.io.ByteArrayInputStream
import com.example.VaultGuard.DTO.*

@Component
class RequestKeyValidationFilter : OncePerRequestFilter() {

    private val endpointDtoMap: Map<String, KClass<*>> = mapOf(
        "/auth/signUp" to UserDTO::class,
        "/auth/logIn" to UserDTO::class,
        "/backup/v1/create-backup-policy" to DatabaseBackupPolicyDTO::class,
        "/backup/v1/{dbId}/create-backup" to DatabaseBackupPolicyDTO::class,
        "/dbConn/addDb" to DbConnDTO::class,
        "/superadmin/addUser" to UserDTO::class,
        "/superadmin/updateRole" to RoleDTO::class,
    )

    private val objectMapper = ObjectMapper()
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val matchedEntry = endpointDtoMap.entries.find { request.requestURI.contains(it.key) }

        if (matchedEntry != null) {
            val dtoClass = matchedEntry.value
            val cachedBody = request.inputStream.readAllBytes()
            val body = cachedBody.decodeToString()

            if (body.isBlank()) {
                sendError(response, "Request body cannot be empty")
                return
            }

            try {
                val jsonNode = objectMapper.readTree(body)
                val actualKeys = jsonNode.fieldNames().asSequence().toSet()

                val group = when (request.requestURI) {
                    "/auth/signUp" -> arrayOf(SignUpValidator::class.java)
                    "/auth/logIn" -> arrayOf(LogInValidator::class.java)
                    else -> emptyArray()
                }

                val requiredFields = getRequiredFields(dtoClass.java, group.toSet())
                val missingKeys = requiredFields.filter { it !in actualKeys }

                if (missingKeys.isNotEmpty()) {
                    sendError(
                        response,
                        "Missing required fields: ${missingKeys.joinToString(", ")}"
                    )
                    return
                }

                val dtoObject = objectMapper.readValue(body, dtoClass.java)
                val violations: Set<ConstraintViolation<Any>> =
                    validator.validate(dtoObject, *group)

                if (violations.isNotEmpty()) {
                    val errors = violations.joinToString("; ") {
                        "${it.propertyPath}: ${it.message}"
                    }
                    sendError(response, "Validation failed: $errors")
                    return
                }

            } catch (e: Exception) {
                sendError(response, "Something went wrong: ${e.message}")
                return
            }

            val wrappedRequest = object : HttpServletRequestWrapper(request) {
                override fun getInputStream(): ServletInputStream {
                    val byteArrayInputStream = ByteArrayInputStream(cachedBody)
                    return object : ServletInputStream() {
                        override fun read(): Int = byteArrayInputStream.read()
                        override fun isFinished(): Boolean = byteArrayInputStream.available() == 0
                        override fun isReady(): Boolean = true
                        override fun setReadListener(readListener: ReadListener?) {}
                    }
                }
            }

            filterChain.doFilter(wrappedRequest, response)
        } else {
            filterChain.doFilter(request, response)
        }
    }

    fun getRequiredFields(dtoClass: Class<*>, group: Set<Class<*>>): Set<String> {
        return dtoClass.kotlin.memberProperties
            .filter { prop ->
                val annotations = prop.javaField?.annotations ?: return@filter false
                annotations.any { annotation ->
                    when (annotation) {
                        is NotBlank, is NotNull, is Email, is Size -> {
                            val groups = try {
                                annotation.annotationClass.java
                                    .getMethod("groups")
                                    .invoke(annotation) as Array<*>
                            } catch (_: Exception) {
                                emptyArray<Class<*>>()
                            }
                            group.isEmpty() || groups.any { it in group }
                        }

                        else -> false
                    }
                }
            }
            .map { it.name }
            .toSet()
    }

    private fun sendError(httpResponse: HttpServletResponse, message: String) {
        httpResponse.contentType = "application/json"
        httpResponse.status = HttpServletResponse.SC_BAD_REQUEST
        httpResponse.writer.write(
            objectMapper.writeValueAsString(
                ApiResponse(status = "error", message = message, data = null)
            )
        )
    }
}