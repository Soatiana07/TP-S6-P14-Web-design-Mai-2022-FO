package com.example.springmvc.service;

import com.example.springmvc.dao.GeneriqueDao;
import com.example.springmvc.model.Categorie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategorieService {
    GeneriqueDao generiqueDao = new GeneriqueDao();
    Categorie categorie = new Categorie();

//    GetAllCategorie
    public ArrayList<Categorie> getAllCategorie() throws Exception {
        return generiqueDao.getAll(categorie);
    }

//    GetNomcategorie
    public String getNomCatgeorie(int id) throws Exception {
        return generiqueDao.getNomCategorie(id);
    }
}
