package com.automacorp.model

enum class WindowStatus {
    OPENED,
    CLOSED
}

data class WindowDto(
    val id: Long, // Identifiant unique de la fenêtre
    val name: String, // Nom ou description de la fenêtre
    val roomName: String, // Nom de la salle associée
    val roomId: Long, // ID de la salle associée
    val windowStatus: WindowStatus // Statut de la fenêtre (ouverte ou fermée)
)
