package com.ucs.chove2.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CidadeDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chove.db";
    private static final int DATABASE_VERSION = 1;

    public CidadeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public CidadeDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // criação da tabela
        db.execSQL("CREATE TABLE cidade " +
                "(id INTEGER PRIMARY KEY, " +
                "cidade TEXT, " +
                "estado TEXT, " +
                "pais TEXT, " +
                "desc_tempo TEXT, " +
                "temperatura REAL, " +
                "icone TEXT, " +
                "lat REAL, " +
                "lon REAL, " +
                "data INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    // inserir ou atualizar nova cidade
    public void insertCidade(Cidade cidade) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", cidade.getId());
        values.put("cidade", cidade.getCidade());
        values.put("estado", cidade.getEstado());
        values.put("pais", cidade.getPais());
        values.put("desc_tempo", cidade.getDesc_tempo());
        values.put("temperatura", cidade.getTemp());
        values.put("icone", cidade.getIcone_tempo());
        values.put("lat", cidade.getLat());
        values.put("lon", cidade.getLon());
        values.put("data", cidade.getDataUnix());

        // substituir o id existente
        long rowId = db.insertWithOnConflict("cidade", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        // true se inseriu, false se substituiu

    }

    // todas as cidades em uma lista
    @SuppressLint("Range")
    public List<Cidade> getAllCidade(String pesq) {

        String query;
        String[] args;

        // pesquisa parcial pelo nome da cidade, mas só se for informado
        if ((pesq != null) && !pesq.isEmpty()) {
            query = "SELECT * FROM cidade WHERE cidade LIKE ? || '%' COLLATE NOCASE ORDER BY pais, estado, cidade";
            args = new String[]{pesq};
        } else {
            query = "SELECT * FROM cidade ORDER BY pais, estado, cidade";
            args = null;
        }

        List<Cidade> cidades = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {

                Cidade cidade = new Cidade();

                cidade.setId(cursor.getInt(cursor.getColumnIndex("id")));
                cidade.setCidade(cursor.getString(cursor.getColumnIndex("cidade")));
                cidade.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
                cidade.setPais(cursor.getString(cursor.getColumnIndex("pais")));
                cidade.setDesc_tempo(cursor.getString(cursor.getColumnIndex("desc_tempo")));
                cidade.setTemp(cursor.getDouble(cursor.getColumnIndex("temperatura")));
                cidade.setIcone_tempo(cursor.getString(cursor.getColumnIndex("icone")));
                cidade.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                cidade.setLon(cursor.getDouble(cursor.getColumnIndex("lon")));
                cidade.setDataUnix(cursor.getLong(cursor.getColumnIndex("data")));

                cidades.add(cidade);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cidades;
    }

    public List<Cidade> getAllCidade() {
        return getAllCidade(null);
    }

    public boolean apagarCidade(int id) {
        SQLiteDatabase db = getWritableDatabase();

        // deletar a cidade com o id fornecido
        String[] whereArgs = { String.valueOf(id) };
        int rowsDeleted = db.delete("cidade", "id = ?", whereArgs);

        db.close();

        return (rowsDeleted > 0);

    }

    public boolean apagarCidade(Cidade cidade) {
        return apagarCidade(cidade.getId());
    }

    public void apagarTodas() {

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM cidade");

        db.close();

    }

    public void zerarTemperaturas() {

        // remover a temperatura e descrição de todas as cidades

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Long unix = new Date().getTime() - 86400000;

        values.put("desc_tempo", "");
        values.put("temperatura", 0);
        values.put("data", unix / 1000);

        db.update("cidade", values, null, null);
        db.close();

    }


}
