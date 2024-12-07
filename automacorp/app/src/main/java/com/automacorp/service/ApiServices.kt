package com.automacorp.service

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object ApiServices {
    private const val BASE_URL = "https://automacorp.devmind.cleverapps.io/" // URL de l'API
    private const val API_USERNAME = "user" // Nom d'utilisateur pour l'authentification
    private const val API_PASSWORD = "password" // Mot de passe pour l'authentification

    // Création d'un client OkHttp sécurisé
    private val client: OkHttpClient by lazy {
        getUnsafeOkHttpClient()
            .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
            .build()
    }

    // Instance Retrofit pour RoomsApiService
    val roomsApiService: RoomsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create()) // Convertisseur Moshi
            .client(client) // Client OkHttp personnalisé
            .build()
            .create(RoomsApiService::class.java)
    }

    // Intercepteur pour l'authentification basique
    class BasicAuthInterceptor(private val username: String, private val password: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
                .newBuilder()
                .header("Authorization", Credentials.basic(username, password)) // Ajout de l'en-tête Authorization
                .build()
            return chain.proceed(request)
        }
    }

    // Création d'un client OkHttp non sécurisé pour l'environnement de développement
    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            val trustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
            val sslContext = SSLContext.getInstance("SSL").also {
                it.init(null, arrayOf(trustManager), SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustManager)
            hostnameVerifier { hostname, _ -> hostname.contains("cleverapps.io") } // Vérification de l'hôte
        }
}