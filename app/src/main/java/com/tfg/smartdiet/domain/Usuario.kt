package com.tfg.smartdiet.domain

data class Usuario(
    val userID: String,
    val nombreUsuario: String,
    val correo: String,
) {
    fun toMap():MutableMap<String, Any>{
        return mutableMapOf(
            "userId" to userID,
            "nombreUsuario" to nombreUsuario,
            "correo" to correo
        )
    }

}
