package com.example.springmvc.controller;

import com.example.springmvc.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/loginadmin")
    public String afficherLogin(){
        return "login_admin";
    }

    @GetMapping("login")
    public String loginAdmin(HttpServletRequest request, Model model, HttpSession session) throws Exception {
        String vue = "";
        String message = "";
        String email = request.getParameter("email");
        String mdp = request.getParameter("mdp");
        int a = adminService.loginAdmin(email, mdp);
        if(a == 1){
            message = "Veuillez verifier votre email ou mot de passe";
            vue = "forward:/";
        }
        else {
            vue = "redirect:/liste_contenu";
        }
        model.addAttribute("message", message);
        return vue;
    }
}
