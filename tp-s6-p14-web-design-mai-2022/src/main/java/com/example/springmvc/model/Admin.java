package com.example.springmvc.model;

import com.example.springmvc.annotations.ColumnName;
import com.example.springmvc.annotations.IsPk;
import com.example.springmvc.annotations.TableName;

@TableName(value = "admin")
public class Admin {
    @IsPk(value = true)
    @ColumnName(value = "id")
    private int id;
    @ColumnName(value = "nomadmin")
    private String email;
    @ColumnName(value="mdp")
    private String mdp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}
