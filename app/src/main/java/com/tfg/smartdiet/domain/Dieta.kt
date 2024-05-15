package com.tfg.smartdiet.domain

data class Dieta(
    val caloriasAct: String,
    val caloriasObj: String,
    val carbohidratosAct: String,
    val carbohidratosObj: String,
    val grasasAct: String,
    val grasasObj: String,
    val proteinasAct: String,
    val proteinasObj: String,
    val fecha: String,
    val userID:String
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "caloriasAct" to caloriasAct,
            "caloriasObj" to caloriasObj,
            "carbohidratosAct" to carbohidratosAct,
            "carbohidratosObj" to carbohidratosObj,
            "grasasAct" to grasasAct,
            "grasasObj" to grasasObj,
            "proteinasAct" to proteinasAct,
            "proteinasObj" to proteinasObj,
            "fecha" to fecha,
            "userID" to userID
        )
    }
}

