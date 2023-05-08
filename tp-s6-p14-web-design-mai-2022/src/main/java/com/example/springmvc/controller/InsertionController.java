package com.example.springmvc.controller;

import com.example.springmvc.model.Categorie;
import com.example.springmvc.model.Contenu;
import com.example.springmvc.service.CategorieService;
import com.example.springmvc.service.ContenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

@Controller
public class InsertionController {
    @Autowired
    ContenuService contenuService;

    @Autowired
    CategorieService categorieService;

    @GetMapping("/insertion_contenu")
    public ModelAndView afficherInsertionContenu() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        ArrayList<Categorie> liste = categorieService.getAllCategorie();
        modelAndView.addObject("liste_categorie", liste);
        modelAndView.setViewName("insertion_contenu");
        return modelAndView;
    }

    @PostMapping("/inserer_contenu")
    public String insererContenu(HttpServletRequest request, @RequestParam CommonsMultipartFile file, HttpSession session) throws Exception{
        Contenu contenu = new Contenu();
        String titre = request.getParameter("titre");
        int idcategorie = Integer.parseInt(request.getParameter("idcategorie"));
        String description = request.getParameter("description");
        String path = session.getServletContext().getRealPath("/images");
        String fileName = contenuService.uploadPhoto(path, file);
        contenu.setTitre(titre);
        contenu.setIdcategorie(idcategorie);
        java.sql.Date date=new java.sql.Date(System.currentTimeMillis());
        contenu.setDatecreation(date);
        contenu.setDescription(description);
        contenu.setImage(fileName);
        System.out.println("Id categorie : "+contenu.getIdcategorie());
        System.out.println("Titre :"+contenu.getTitre());
        System.out.println("Contenu.date : "+contenu.getDatecreation());
        System.out.println("Descri : "+contenu.getDescription());
        System.out.println("Image : "+contenu.getImage());
        contenuService.insertContenu(contenu);
        return "redirect:/";
    }

    @GetMapping("supprimer_contenu")
    public String supprimer(HttpServletRequest request, Model model) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        String message = contenuService.deleteContenu(id);
        model.addAttribute("message", message);
        return "forward:/";
    }

    @GetMapping("modification_contenu")
    public ModelAndView afficherModif(HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        ArrayList<Categorie> liste = categorieService.getAllCategorie();
        int id = Integer.parseInt(request.getParameter("id"));
        Contenu contenu = contenuService.getContenuById(id);
        modelAndView.addObject("liste_categorie", liste);
        modelAndView.addObject("contenu", contenu);
        modelAndView.setViewName("modifier_contenu");
        return modelAndView;
    }

    @PostMapping("modifier_contenu")
    public String modifierContenu(Model model,HttpServletRequest request,@RequestParam CommonsMultipartFile file, HttpSession session) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Contenu contenu = new Contenu();
        String titre = request.getParameter("titre");
        int idcategorie = Integer.parseInt(request.getParameter("idcategorie"));
        String description = request.getParameter("description");
        String path = session.getServletContext().getRealPath("/images");
        String fileName = contenuService.uploadPhoto(path, file);
        contenu.setTitre(titre);
        contenu.setIdcategorie(idcategorie);
        java.sql.Date date=new java.sql.Date(System.currentTimeMillis());
        contenu.setDatecreation(date);
        contenu.setDescription(description);
        contenu.setImage(fileName);
        contenuService.modifierContenu(id, contenu);
        return "redirect:/";
    }
}
