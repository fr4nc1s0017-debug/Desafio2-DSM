package udb.edu.sv.dsm.agenciaviajes.model

data class Destino(
    var id: String = "",
    var nombre: String = "",
    var pais: String = "",
    var precio: Double = 0.0,
    var descripcion: String = "",
    var imagenBase64: String = "",
    var agenteId: String = "",
    var fechaCreacion: Long = System.currentTimeMillis()
)