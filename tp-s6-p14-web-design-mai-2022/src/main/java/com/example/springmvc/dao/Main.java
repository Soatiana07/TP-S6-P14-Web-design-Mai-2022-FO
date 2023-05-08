package com.example.springmvc.dao;

import com.example.springmvc.model.Contenu;
import com.example.springmvc.service.ContenuService;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        Contenu contenu = new ContenuService().getContenuById(1);
        String[] splits = contenu.getTitre().split(" ");
        String keys = "";
        for(int i = 0; i < splits.length; i++) {
            keys += splits[i] + ",";
            if (i == splits.length -1){
//                System.out.println(i);
                keys.substring(0, keys.length() -1);
            }
        }
        System.out.println("KEYWORDS : "+keys);
    }
}
