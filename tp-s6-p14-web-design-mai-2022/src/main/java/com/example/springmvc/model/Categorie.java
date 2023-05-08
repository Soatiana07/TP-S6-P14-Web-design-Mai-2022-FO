package com.example.springmvc.model;

import com.example.springmvc.annotations.ColumnName;
import com.example.springmvc.annotations.TableName;

import javax.persistence.Column;

@TableName(value="categorie")
public class Categorie {
    @ColumnName(value = "id")
    private int id;

    @ColumnName(value = "nomcategorie")
    private String nomcategorie;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomcategorie() {
        return nomcategorie;
    }

    public void setNomcategorie(String nomcategorie) {
        this.nomcategorie = nomcategorie;
    }
}
