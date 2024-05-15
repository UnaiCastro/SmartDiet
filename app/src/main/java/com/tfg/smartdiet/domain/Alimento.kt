package com.tfg.smartdiet.domain

data class Alimento(
    val agua:String,
    val calorias:String,
    val carbohidratos:String,
    val descripcion:String,
    val grasas:String,
    val imagen:String,
    val minerales:String,
    val nombre:String,
    val proteinas:String,
    val tipo:String,
    val vitaminas:String

){
    fun toMap():MutableMap<String, Any>{
        return mutableMapOf(
            "agua" to agua,
            "calorias" to calorias,
            "carbohidratos" to carbohidratos,
            "descripcion" to descripcion,
            "grasas" to grasas,
            "imagen" to imagen,
            "minerales" to minerales,
            "nombre" to nombre,
            "proteinas" to proteinas,
            "tipo" to tipo,
            "vitaminas" to vitaminas
        )
    }

}
