package com.example.springmvc.service;

import com.example.springmvc.dao.GeneriqueDao;
import com.example.springmvc.model.Admin;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    GeneriqueDao generiqueDao = new GeneriqueDao();

//    login
    public int loginAdmin(String email, String mdp) throws Exception{
        Admin admin = generiqueDao.loginAdmin(email, mdp);
        int valiny = 0;
        if(admin.getEmail() == null || admin.getMdp() == null || (admin.getEmail() == null && admin.getMdp() == null)){
            valiny = 1;
        }
        else {
            valiny = 0;
        }
        System.out.println("Valiny = "+valiny);
        return valiny;
    }
}
