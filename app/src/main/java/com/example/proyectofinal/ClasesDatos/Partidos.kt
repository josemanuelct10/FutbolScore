package com.example.proyectofinal.ClasesDatos

import com.google.type.DateTime
import java.sql.Time

data class Partidos(
    var local:String = "",
    var visitante:String="",
    var imagenLocal:String = "",
    var imagenVisitante:String = "",
    var competicion:String = "",
    var resultado:String = "",
)