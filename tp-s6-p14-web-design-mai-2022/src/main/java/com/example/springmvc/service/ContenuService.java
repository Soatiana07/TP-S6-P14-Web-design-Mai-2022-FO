package com.example.springmvc.service;

import com.example.springmvc.dao.GeneriqueDao;
import com.example.springmvc.dao.HibernateDAO;
import com.example.springmvc.model.Contenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContenuService {
    GeneriqueDao generiqueDao = new GeneriqueDao();
    Contenu contenu = new Contenu();
//    getAllPaginé
    public ArrayList<Contenu> getAllContenus(int limit, int offset) throws Exception{
        return generiqueDao.getAllPagination(contenu, limit, offset);
    }

//    insert
    public void insertContenu(Contenu contenu) throws Exception {
        generiqueDao.insert(contenu);
    }

//    getById
    public Contenu getContenuById(int id) throws Exception {
        return(Contenu) generiqueDao.findById(contenu, id);
    }

    //    Upload image
    public static String uploadPhoto(String path, CommonsMultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        byte[] bytes = file.getBytes();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(path +File.separator + fileName)));
        bufferedOutputStream.write(bytes);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        return fileName;
    }

//    delete
    public String deleteContenu(int id) throws Exception {
        String message = "Contenu supprime avec succes!";
        generiqueDao.delete(contenu, id);
        return message;
    }

//    modification
    public void modifierContenu(int id, Contenu contenu) throws Exception {
        generiqueDao.modifierContenu(id, contenu);
    }

//    recherche par titre
    public ArrayList<Contenu> recherche(String titre) throws Exception {
        return generiqueDao.recherche("titre", contenu, titre);
    }
}
