package com.example.proyecto_agenda.Objetos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotaDao {

    @Insert
    long insert(Nota nota);

    @Update
    void update(Nota nota);

    @Delete
    void delete(Nota nota);

    @Query("SELECT * FROM notas")
    List<Nota> getAllNotas();

    @Query("SELECT * FROM notas WHERE id = :id")
    Nota getNotaById(int id);

    @Query("DELETE FROM notas WHERE id = :id")
    void deleteNotaById(int id);

    // Nueva consulta para obtener las notas de un usuario específico
    @Query("SELECT * FROM notas WHERE uid_usuario = :uidUsuario")
    List<Nota> getNotasByUsuario(String uidUsuario);

}
