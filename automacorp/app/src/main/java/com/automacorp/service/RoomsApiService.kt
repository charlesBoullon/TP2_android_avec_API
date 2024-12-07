package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import retrofit2.Call
import retrofit2.http.*

interface RoomsApiService {

    // Récupérer toutes les salles
    @GET("rooms")
    fun findAll(): Call<List<RoomDto>>

    // Récupérer une salle par son ID
    @GET("rooms/{id}")
    fun getRoomById(@Path("id") id: Long): Call<RoomDto>

    // Mettre à jour une salle existante
    @PUT("rooms/{id}")
    fun updateRoom(
        @Path("id") id: Long,
        @Body room: RoomCommandDto
    ): Call<RoomDto>

    // Créer une nouvelle salle
    @POST("rooms")
    fun createRoom(@Body room: RoomDto): Call<RoomDto>

    // Supprimer une salle par son ID
    @DELETE("rooms/{id}")
    fun deleteRoomById(@Path("id") id: Long): Call<Unit>

    // Récupérer la liste des fenêtres d'une salle
    @GET("rooms/{id}/windows")
    fun listWindows(@Path("id") roomId: Long): Call<List<WindowDto>>

    // Mettre à jour une fenêtre spécifique
    @PUT("windows/{id}")
    fun updateWindow(
        @Path("id") windowId: Long,
        @Body window: WindowDto
    ): Call<WindowDto>
}
