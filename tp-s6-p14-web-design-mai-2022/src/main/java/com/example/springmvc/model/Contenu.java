package com.example.springmvc.model;

import com.example.springmvc.annotations.ColumnName;
import com.example.springmvc.annotations.IsFk;
import com.example.springmvc.annotations.IsPk;
import com.example.springmvc.annotations.TableName;

import javax.persistence.*;
import java.sql.Date;

@TableName(value = "contenu")
public class Contenu {
    @IsPk(value = true)
    @ColumnName(value = "id")
    private Integer id;

    @ColumnName(value = "titre")
    private String titre;

    @ColumnName(value = "description")
    private String description;

    @ColumnName(value = "datecreation")
    private Date datecreation;

    @ColumnName(value = "idcategorie")
    private int idcategorie;

    @ColumnName(value = "image")
    private String image;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIdcategorie() {
        return idcategorie;
    }

    public void setIdcategorie(int idcategorie) {
        this.idcategorie = idcategorie;
    }

}
