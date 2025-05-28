package com.example.proyecto_agenda.Objetos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notas")
public class Nota implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "uid_usuario")
    private String uidUsuario;

    @ColumnInfo(name = "correo_usuario")
    private String correoUsuario;

    @ColumnInfo(name = "fecha_hora_actual")
    private String fechaHoraActual;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "fecha_nota")
    private String fechaNota;

    @ColumnInfo(name = "estado")
    private String estado;

    // Constructor por defecto
    public Nota() {}

    // Constructor con parámetros
    public Nota(String uidUsuario, String correoUsuario, String fechaHoraActual, String titulo, String descripcion, String fechaNota, String estado) {
        this.uidUsuario = uidUsuario;
        this.correoUsuario = correoUsuario;
        this.fechaHoraActual = fechaHoraActual;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaNota = fechaNota;
        this.estado = estado;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getFechaHoraActual() {
        return fechaHoraActual;
    }

    public void setFechaHoraActual(String fechaHoraActual) {
        this.fechaHoraActual = fechaHoraActual;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaNota() {
        return fechaNota;
    }

    public void setFechaNota(String fechaNota) {
        this.fechaNota = fechaNota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}