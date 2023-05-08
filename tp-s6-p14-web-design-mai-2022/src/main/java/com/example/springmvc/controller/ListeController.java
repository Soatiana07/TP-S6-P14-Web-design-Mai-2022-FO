package com.example.springmvc.controller;

import com.example.springmvc.model.Contenu;
import com.example.springmvc.service.CategorieService;
import com.example.springmvc.service.ContenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


@Controller
public class ListeController {
    @Autowired
    ContenuService contenuService;

    @Autowired
    CategorieService categorieService;

    @GetMapping("/")
    public ModelAndView afficherListe(@RequestParam(value = "limit", defaultValue = "2") int limit, @RequestParam(value = "offset", defaultValue = "0") int offset) throws Exception{
        ModelAndView modelAndView = new ModelAndView();
        ArrayList<Contenu> liste = contenuService.getAllContenus(limit, offset);
        modelAndView.addObject("liste", liste);
        modelAndView.addObject("limit", limit);
        modelAndView.addObject("offset", offset);
        modelAndView.setViewName("liste_contenu");
        return modelAndView;
    }

    @GetMapping("detail_contenu/{id}")
    public ModelAndView afficher_detail(HttpServletRequest request, @PathVariable("id") int id) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
//        int id = Integer.parseInt(request.getParameter("id"));
        Contenu contenu = (Contenu) contenuService.getContenuById(id);
        String nomcategorie = categorieService.getNomCatgeorie(contenu.getIdcategorie());
        modelAndView.addObject("contenu", contenu);
        modelAndView.addObject("nomCategorie", nomcategorie);
        modelAndView.setViewName("detail_contenu");
        return modelAndView;
    }

    @GetMapping("recherche")
    public ModelAndView recherche(HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        String motCle = request.getParameter("motCle");
        ArrayList<Contenu> liste = contenuService.recherche(motCle);
        modelAndView.setViewName("liste_contenu");
        modelAndView.addObject("liste", liste);
        return modelAndView;
    }
}
