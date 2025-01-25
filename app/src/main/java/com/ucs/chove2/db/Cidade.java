package com.ucs.chove2.db;

import java.util.Date;

public class Cidade {

    private int id;
    private String cidade;
    private String estado;
    private String pais;
    private String desc_tempo;
    private Double temp;
    private String icone_tempo;
    private Double lat;
    private Double lon;
    private Date data;

    public Cidade(String cidade, String estado, String pais, Double lat, Double lon) {
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.lat = lat;
        this.lon = lon;
    }

    public Cidade() {
        
    }


    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Date getData() {
        return data;
    }

    public long getDataUnix() {

        if (data != null) {
            return data.getTime() / 1000;
        } else {
            return 0;
        }

    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setDataUnix(long dataUnix) {

        this.data = new Date(dataUnix * 1000);

    }

    public String getDesc_tempo() {
        return desc_tempo;
    }

    public void setDesc_tempo(String desc_tempo) {
        this.desc_tempo = desc_tempo;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getIcone_tempo() {
        return icone_tempo;
    }

    public void setIcone_tempo(String icone_tempo) {
        this.icone_tempo = icone_tempo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
