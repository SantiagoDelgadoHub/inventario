package com.desarrolloweb.comercial.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion", length = 200, nullable = false)
    @NotEmpty
    private String descripcion;

    @Column(name = "estado", length = 15)
    private String estado;

    @Column(name = "fecha_carga")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime fechaCarga;

    @Column(name = "fecha_creacion")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaModificacion;

    @Column(name = "nombre_documento", length = 80, nullable = false)
    private String nombreDocumento;

    @Column(name = "tipo_archivo", length = 5, nullable = false)
    private String tipoArchivo;

    @Column(name = "tamanio", nullable = false)
    private String tamanio;

    // @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    // private String archivo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente cliente;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now().withNano(0);
        this.fechaCarga = LocalDateTime.now().withNano(0);
    }



    public Documento() {
    }

    public Documento(Long id, @NotEmpty String descripcion, String estado, LocalDateTime fechaCarga,
            LocalDateTime fechaCreacion, LocalDate fechaModificacion, @NotEmpty String nombreDocumento,
            @NotEmpty String tipoArchivo, @NotEmpty String tamanio) {
        this.id = id;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCarga = fechaCarga;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.nombreDocumento = nombreDocumento;
        this.tipoArchivo = tipoArchivo;
        this.tamanio = tamanio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDate fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


}
