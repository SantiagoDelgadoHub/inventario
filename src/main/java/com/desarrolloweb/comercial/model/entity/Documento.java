package com.desarrolloweb.comercial.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Documento {
    private Long id;
    private String nombreDocumento;
    private String descripcion;
    private String tipoArchivo;
    private LocalDateTime fechaCarga;
    private int tamanio;
    private String estado;
    private LocalDate fechaCreacion;
    private LocalDate fechaModificacion;

}
