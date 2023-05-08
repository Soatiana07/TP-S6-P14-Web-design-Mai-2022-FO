/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.springmvc.dao;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.springmvc.Connexion.MyConnexion;
import com.example.springmvc.annotations.ColumnName;
import com.example.springmvc.annotations.IsFk;
import com.example.springmvc.annotations.IsPk;
import com.example.springmvc.annotations.TableName;
import com.example.springmvc.model.Admin;
import com.example.springmvc.model.Categorie;
import com.example.springmvc.model.Contenu;


/**
 *
 * @author SOATIANA
 */
public class GeneriqueDao {
//    Fonction maka ny attributs annotées rehetra (nom colonne)
    public List<Field> getAnnotatedFields(Object o) throws Exception{
        List<Field> liste = new ArrayList();
        Field[] tab = o.getClass().getDeclaredFields();
        try{
            for (int i = 0; i < tab.length; i++) {
                if(tab[i].isAnnotationPresent(ColumnName.class)){
                    liste.add(tab[i]);
                }
            }
        } catch(Exception e){
            e = new Exception("Il n'y a pas d'attributs annotes dans la classe de cet objet");
            throw e;
        }
        return liste;
    }

//    Fonction maka ny pk ana objet iray
    public int getPrimaryKey(Object o) throws Exception{
        int pk = 0;
        Field[] tabAttributs = o.getClass().getDeclaredFields();
        for (int i = 0; i < tabAttributs.length; i++) {
            if(tabAttributs[i].isAnnotationPresent(IsPk.class)){
                if(tabAttributs[i].getAnnotation(IsPk.class).value()){
                    Method getter = o.getClass().getMethod("get" + tabAttributs[i].getName().toString().substring(0, 1).toUpperCase() + tabAttributs[i].getName().toString().substring(1));
//                    System.out.println(getter.getReturnType());
                    pk = Integer.parseInt(getter.invoke(o).toString());
                }
            }
        }
        return pk;
    }

//    Fonction mi set pk ana objet iray
    public void setPrimaryKey(Object o) throws Exception{
        int pk = this.getPrimaryKey(o);
        Field[] tabAttributs = o.getClass().getDeclaredFields();
        for (int i = 0; i < tabAttributs.length; i++) {
            if(tabAttributs[i].isAnnotationPresent(IsPk.class)){
                if(tabAttributs[i].getAnnotation(IsPk.class).value()){
                    Method setter = o.getClass().getMethod("set"
                            + tabAttributs[i].getName().toString().substring(0, 1).toUpperCase()
                            + tabAttributs[i].getName().toString().substring(1), tabAttributs[i].getType());
                    setter.invoke(o, pk);
                }
            }
        }
    }

//    Fonction maka ny classe annotée (nom table)
    public String getTableName(Object o) throws Exception{
        String nomClasse = o.getClass().getSimpleName();
        try{
            if(o.getClass().isAnnotationPresent(TableName.class)){
                nomClasse = o.getClass().getAnnotation(TableName.class).value();
            }
        }
        catch(Exception e){
            e = new Exception("Cette classe n'est pas associee a une table.");
            throw e;
        }
        return nomClasse;
    }

//    Fonction maka ny gettersn'ny attributs annotés ColumnName rehetra ao anaty classen'ilay Object o
    public Method[] getGetters(Object o) throws Exception {
        List<Field> tabAttributs = this.getAnnotatedFields(o);
        Method[] getters = new Method[tabAttributs.size()];
        for (int i = 0; i < getters.length; i++) {
            getters[i] = o.getClass().getMethod("get" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1));
        }
        return getters;
    }



//    Fonction insert généralisée
    public void insert(Object o) throws Exception {
        String nomClasse = this.getTableName(o);
        List<Field> tabAttributs = this.getAnnotatedFields(o);
        String[] nomAttributs = new String[tabAttributs.size()];
        String sql = "INSERT INTO " + nomClasse + "(";

       //  Manoratra -> "INSERT INTO NomTable(Attribut1,Attribut2,...) VALUES("
          for (int i = 0; i < tabAttributs.size(); i++) {
            if(tabAttributs.get(i).isAnnotationPresent(ColumnName.class)){
                nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
                // Raha id ilay izy dia tsy asiana n'inona n'inona
                if(tabAttributs.get(i).getAnnotation(ColumnName.class).value().equals("id")){
                    continue;
                }
                sql = sql + nomAttributs[i] + ",";
            }
        }
        sql = sql.substring(0, sql.length() - 1);
        sql = sql + ") VALUES(";

        String valeur = "";
        Method[] getters = this.getGetters(o);
        String[] nomTypeAttributs = new String[tabAttributs.size()];
        Object[] valeurRetourneParGetters = new Object[getters.length];

        //  Maka ny type-n'ny attributs rehetra, miantso ny getters rehetra(miaraka @ valeurs retournény avy),
        //  raha mila quotes ilay type dia asiana ilay requete sinon tsy asiana n'inona n'inona

        for (int i = 0; i < tabAttributs.size(); i++) {
            String nomTypeAttribut = nomTypeAttributs[i];
            nomTypeAttribut = tabAttributs.get(i).getType().toString();
            valeurRetourneParGetters[i] = getters[i].invoke(o);

            if(tabAttributs.get(i).isAnnotationPresent(IsPk.class)){
                if(tabAttributs.get(i).getAnnotation(IsPk.class).value()){
                    continue;
                }
            }
            if (nomTypeAttribut.contains("String") || nomTypeAttribut.contains("Date")) {
                valeur = "'" + valeurRetourneParGetters[i].toString() + "'" + ",";
            }
            else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
                if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
                    Method get = null;
                    for (int j = 0; j < getters.length; j++) {
                        if(getters[j].getName().equalsIgnoreCase("get"+tabAttributs.get(i).getName())){
                            get = getters[j];
                        }
                    }
                    if(get != null){
                        valeur = this.getPrimaryKey(get.invoke(o)) + ",";
                    }
                }
            }
            else {
                valeur = valeurRetourneParGetters[i].toString() + ",";
//                System.out.println("TAFIDITRA");
                System.out.println("valeurs : "+valeur);
            }

            sql = sql + valeur;
        }

        //  Manoratra (getAttribut1,getAttribut2,...)
        sql = sql.substring(0, sql.length() - 1);
        sql = sql + ")";
        System.out.println(sql);
        //  Manao insertion any anaty base rehefa feno ilay requete
        Connection con = null;
        Statement st = null;

        try {
            con = new MyConnexion().getConnection();
            st = con.createStatement();
            st.executeUpdate(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (con != null) {
                con.close();
            }
            if (st != null) {
                st.close();
            }
        }
    }

//    Fonction select * généralisée
    public ArrayList getAll(Object o) throws Exception {
        String nomTable = this.getTableName(o);
        ArrayList liste = null;
        Connection con = null;
        String sql = null;
        Statement st = null;
        ResultSet res = null;
        Object ob = null;

        List<Field> tabAttributs = this.getAnnotatedFields(o);
        Method[] getters = this.getGetters(o);

        String[] nomAttributs = new String[tabAttributs.size()];
        String[] nomTypeAttributs = new String[tabAttributs.size()];

        try {
            con = new MyConnexion().getConnection();
            sql = "SELECT * FROM " + nomTable;
            st = con.createStatement();
            res = st.executeQuery(sql);
            liste = new ArrayList();

            while (res.next()) {
                //  Mamorona instance vaovao isaky ny miodina ny boucle
                //  mba hamenohana an'ilay arraylist
                ob = o.getClass().getConstructor().newInstance();
                for (int i = 0; i < tabAttributs.size(); i++) {
                    nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
                    nomTypeAttributs[i] = tabAttributs.get(i).getType().toString();

                    //  Miset valeur selon typen'ny attribut an'ilay setter
                    if (nomTypeAttributs[i].contains("String")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getString(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Date")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDate(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Integer")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Double")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDouble(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("int")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    //  Raha misy objet tsy de type primitif, ka misy annotation Fk true
                    else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
                        if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
                            //  Manao SELECT * FROM AttributEnQuestion WHERE id=res.getInt("AttributEnQuestion.getAnnotation")
                            Object attributbyId = this.findById(tabAttributs.get(i).getType().getConstructor().newInstance(), res.getInt(nomAttributs[i]));
                            //  Mi set an'ilay objet en question ao anatin'ilay classe
                            Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                            set.invoke(ob,attributbyId);
                        }
                    }
                }
                liste.add(ob);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return liste;
    }

    public ArrayList getAllWithoutSupprime(Object o) throws Exception {
        String nomTable = this.getTableName(o);
        ArrayList liste = null;
        Connection con = null;
        String sql = null;
        Statement st = null;
        ResultSet res = null;
        Object ob = null;

        List<Field> tabAttributs = this.getAnnotatedFields(o);
        Method[] getters = this.getGetters(o);

        String[] nomAttributs = new String[tabAttributs.size()];
        String[] nomTypeAttributs = new String[tabAttributs.size()];

        try {
            con = new MyConnexion().getConnection();
            sql = "SELECT * FROM " + nomTable+" LEFT JOIN "+nomTable+"supprime"+ " on "+nomTable+".id="+nomTable+"supprime.id"+nomTable+
                    " WHERE "+nomTable+"supprime.id"+nomTable+" is null";
            st = con.createStatement();
            res = st.executeQuery(sql);
            liste = new ArrayList();

            while (res.next()) {
                //  Mamorona instance vaovao isaky ny miodina ny boucle
                //  mba hamenohana an'ilay arraylist
                ob = o.getClass().getConstructor().newInstance();
                for (int i = 0; i < tabAttributs.size(); i++) {
                    nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
                    nomTypeAttributs[i] = tabAttributs.get(i).getType().toString();

                    //  Miset valeur selon typen'ny attribut an'ilay setter
                    if (nomTypeAttributs[i].contains("String")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getString(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Date")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDate(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Integer")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Double")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDouble(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("int")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    //  Raha misy objet tsy de type primitif, ka misy annotation Fk true
                    else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
                        if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
                            //  Manao SELECT * FROM AttributEnQuestion WHERE id=res.getInt("AttributEnQuestion.getAnnotation")
                            Object attributbyId = this.findById(tabAttributs.get(i).getType().getConstructor().newInstance(), res.getInt(nomAttributs[i]));
                            //  Mi set an'ilay objet en question ao anatin'ilay classe
                            Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                            set.invoke(ob,attributbyId);
                        }
                    }
                }
                liste.add(ob);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return liste;
    }



//    Fonction select * from Table where id = id généralisée
    public Object findById(Object o, int id) throws Exception {
        String nomTable = this.getTableName(o);
        Connection con = null;
        String sql = null;
        Statement st = null;
        ResultSet res = null;

        List<Field> tabAttributs = this.getAnnotatedFields(o);

        String[] nomAttributs = new String[tabAttributs.size()];
        String[] nomTypeAttributs = new String[tabAttributs.size()];

        try {
            con = new MyConnexion().getConnection();
            sql = "SELECT * FROM " + nomTable + " WHERE id=" + id;
            st = con.createStatement();
            res = st.executeQuery(sql);

            while (res.next()) {
                //  Mamorona instance vaovao isaky ny miodina ny boucle
                //  mba hamenohana an'ilay arraylist

                for (int i = 0; i < tabAttributs.size(); i++) {
                    nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
                    nomTypeAttributs[i] = tabAttributs.get(i).getType().toString();

                    //  Miset valeur selon typen'ny attribut an'ilay setter
                    if (nomTypeAttributs[i].contains("String")) {
                        Method set = o.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(o, res.getString(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Date")) {
                        Method set = o.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(o, res.getDate(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Integer")) {
                        Method set = o.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(o, res.getInt(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("int")) {
                        Method set = o.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(o, res.getInt(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Double")) {
                        Method set = o.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(o, res.getDouble(nomAttributs[i]));
                    }
                    else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
                        if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
                            continue;
                        }
                    }
                }
//                Method getId = o.getClass().getDeclaredMethod("setId",int.class);
//                getId.invoke(o,id);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return o;
    }

//    Fonction delete généralisée
//    public void delete(Object o, int id) throws Exception{
//        String nomTable = this.getTableName(o);
//        Connection con = null;
//        String sql = null;
//        Statement st = null;
//        ResultSet res = null;
//
//        try {
//            con = new MyConnexion().getConnection();
//            sql = "DELETE FROM " + nomTable+" WHERE id="+id;
//            st = con.createStatement();
//            st.executeUpdate(sql);
//
//        } catch (Exception e) {
//            throw e;
//        }finally {
//            if (st != null) {
//                st.close();
//            }
//            if (res != null) {
//                res.close();
//            }
//            if (con != null) {
//                con.close();
//            }
//        }
//    }
    public void delete(Object o, int id) throws Exception{
        String nomTable = this.getTableName(o);
        Connection con = null;
        String sql = null;
        Statement st = null;
        ResultSet res = null;

        try {
            con = new MyConnexion().getConnection();
            sql = "INSERT INTO " + nomTable+"supprime"+"(id"+nomTable+") VALUES("+id+")";
            st = con.createStatement();
            st.executeUpdate(sql);

        } catch (Exception e) {
            throw e;
        }finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }


//    Fonction getAll avec pagination
//    public ArrayList getAllPagination(Object o, int limit, int offset) throws Exception {
//        String nomTable = this.getTableName(o);
//        ArrayList liste = null;
//        Connection con = null;
//        String sql = null;
//        Statement st = null;
//        ResultSet res = null;
//        Object ob = null;
//
//        List<Field> tabAttributs = this.getAnnotatedFields(o);
//        Method[] getters = this.getGetters(o);
//
//        String[] nomAttributs = new String[tabAttributs.size()];
//        String[] nomTypeAttributs = new String[tabAttributs.size()];
//
//        try {
//            con = new MyConnexion().getConnection();
//            sql = "SELECT * FROM " + nomTable + " LIMIT "+limit+" OFFSET "+offset;
//            st = con.createStatement();
//            res = st.executeQuery(sql);
//            liste = new ArrayList();
//
//            while (res.next()) {
//                //  Mamorona instance vaovao isaky ny miodina ny boucle
//                //  mba hamenohana an'ilay arraylist
//                ob = o.getClass().getConstructor().newInstance();
//                for (int i = 0; i < tabAttributs.size(); i++) {
//                    nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
//                    nomTypeAttributs[i] = tabAttributs.get(i).getType().toString();
//
//                    //  Miset valeur selon typen'ny attribut an'ilay setter
//                    if (nomTypeAttributs[i].contains("String")) {
//                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
//                        set.invoke(ob, res.getString(nomAttributs[i]));
//                    }
//                    if (nomTypeAttributs[i].contains("Date")) {
//                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
//                        set.invoke(ob, res.getDate(nomAttributs[i]));
//                    }
//                    if (nomTypeAttributs[i].contains("Integer")) {
//                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
//                        set.invoke(ob, res.getInt(nomAttributs[i]));
//                    }
//                    if (nomTypeAttributs[i].contains("Double")) {
//                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
//                        set.invoke(ob, res.getDouble(nomAttributs[i]));
//                    }
//                    if (nomTypeAttributs[i].contains("int")) {
//                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
//                        set.invoke(ob, res.getInt(nomAttributs[i]));
//                    }
//                    //  Raha misy objet tsy de type primitif, ka misy annotation Fk true
//                    else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
//                        if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
//                            //  Manao SELECT * FROM AttributEnQuestion WHERE id=res.getInt("AttributEnQuestion.getAnnotation")
//                            Object attributbyId = this.findById(tabAttributs.get(i).getType().getConstructor().newInstance(), res.getInt(nomAttributs[i]));
//                            //  Mi set an'ilay objet en question ao anatin'ilay classe
//                            Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
//                            set.invoke(ob,attributbyId);
//                        }
//                    }
//                }
//                liste.add(ob);
//            }
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            if (st != null) {
//                st.close();
//            }
//            if (res != null) {
//                res.close();
//            }
//            if (con != null) {
//                con.close();
//            }
//        }
//
//        return liste;
//    }
    public ArrayList getAllPagination(Object o, int limit, int offset) throws Exception {
        String nomTable = this.getTableName(o);
        ArrayList liste = null;
        Connection con = null;
        String sql = null;
        Statement st = null;
        ResultSet res = null;
        Object ob = null;

        List<Field> tabAttributs = this.getAnnotatedFields(o);
        Method[] getters = this.getGetters(o);

        String[] nomAttributs = new String[tabAttributs.size()];
        String[] nomTypeAttributs = new String[tabAttributs.size()];

        try {
            con = new MyConnexion().getConnection();
            sql = "SELECT * FROM " + nomTable+" LEFT JOIN "+nomTable+"supprime"+ " on "+nomTable+".id="+nomTable+"supprime.id"+nomTable+
                    " WHERE "+nomTable+"supprime.id"+nomTable+" is null ORDER BY id"+" LIMIT "+limit+" OFFSET "+offset;
            st = con.createStatement();
            System.out.println("sql :"+sql);
            res = st.executeQuery(sql);
            liste = new ArrayList();

            while (res.next()) {
                //  Mamorona instance vaovao isaky ny miodina ny boucle
                //  mba hamenohana an'ilay arraylist
                ob = o.getClass().getConstructor().newInstance();
                for (int i = 0; i < tabAttributs.size(); i++) {
                    nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
                    nomTypeAttributs[i] = tabAttributs.get(i).getType().toString();

                    //  Miset valeur selon typen'ny attribut an'ilay setter
                    if (nomTypeAttributs[i].contains("String")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getString(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Date")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDate(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Integer")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Double")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDouble(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("int")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    //  Raha misy objet tsy de type primitif, ka misy annotation Fk true
                    else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
                        if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
                            //  Manao SELECT * FROM AttributEnQuestion WHERE id=res.getInt("AttributEnQuestion.getAnnotation")
                            Object attributbyId = this.findById(tabAttributs.get(i).getType().getConstructor().newInstance(), res.getInt(nomAttributs[i]));
                            //  Mi set an'ilay objet en question ao anatin'ilay classe
                            Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                            set.invoke(ob,attributbyId);
                        }
                    }
                }
                liste.add(ob);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return liste;
    }

//    Fonction recherche par 1 mot cle sans pagination
    public ArrayList recherche(String nomColonne, Object o, String value) throws Exception {
        String nomTable = this.getTableName(o);
        ArrayList liste = null;
        Connection con = null;
        String sql = null;
        Statement st = null;
        ResultSet res = null;
        Object ob = null;

        List<Field> tabAttributs = this.getAnnotatedFields(o);
        Method[] getters = this.getGetters(o);

        String[] nomAttributs = new String[tabAttributs.size()];
        String[] nomTypeAttributs = new String[tabAttributs.size()];

        try {
            con = new MyConnexion().getConnection();
            sql = "SELECT * FROM " + nomTable+" LEFT JOIN "+nomTable+"supprime"+ " on "+nomTable+".id="+nomTable+"supprime.id"+nomTable+
                    " WHERE "+nomTable+"supprime.id"+nomTable+" is null and "+nomColonne+" ILIKE '%"+value+"%'";
            st = con.createStatement();
            res = st.executeQuery(sql);
            liste = new ArrayList();

            while (res.next()) {
                //  Mamorona instance vaovao isaky ny miodina ny boucle
                //  mba hamenohana an'ilay arraylist
                ob = o.getClass().getConstructor().newInstance();
                for (int i = 0; i < tabAttributs.size(); i++) {
                    nomAttributs[i] = tabAttributs.get(i).getAnnotation(ColumnName.class).value();
                    nomTypeAttributs[i] = tabAttributs.get(i).getType().toString();

                    //  Miset valeur selon typen'ny attribut an'ilay setter
                    if (nomTypeAttributs[i].contains("String")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getString(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Date")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDate(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Integer")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("Double")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getDouble(nomAttributs[i]));
                    }
                    if (nomTypeAttributs[i].contains("int")) {
                        Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                        set.invoke(ob, res.getInt(nomAttributs[i]));
                    }
                    //  Raha misy objet tsy de type primitif, ka misy annotation Fk true
                    else if(tabAttributs.get(i).isAnnotationPresent(IsFk.class)){
                        if(tabAttributs.get(i).getAnnotation(IsFk.class).value()){
                            //  Manao SELECT * FROM AttributEnQuestion WHERE id=res.getInt("AttributEnQuestion.getAnnotation")
                            Object attributbyId = this.findById(tabAttributs.get(i).getType().getConstructor().newInstance(), res.getInt(nomAttributs[i]));
                            //  Mi set an'ilay objet en question ao anatin'ilay classe
                            Method set = ob.getClass().getMethod("set" + tabAttributs.get(i).getName().toString().substring(0, 1).toUpperCase() + tabAttributs.get(i).getName().toString().substring(1), tabAttributs.get(i).getType());
                            set.invoke(ob,attributbyId);
                        }
                    }
                }
                liste.add(ob);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return liste;
    }

//    -----------------------------------------------------------------------------------------------------------------------
//    Fonction updateContenu
    public void modifierContenu(int id, Contenu contenu) throws Exception {
        Connection con = null;
        Statement st = null;
        String sql = null;
        try {
            con = new MyConnexion().getConnection();
            sql = "UPDATE contenu SET titre='"+ contenu.getTitre() +"', description="+ "'"+
                    contenu.getDescription()+"'"+", idcategorie="+contenu.getIdcategorie()+", image='"+
                    contenu.getImage()+"'" +" WHERE id="+id;
            System.out.println("SQL ="+sql);
            st = con.createStatement();

            st.executeUpdate(sql);
        } catch (Exception e){
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

//    Fonction login admin
    public Admin loginAdmin(String email, String mdp) throws Exception{
        Admin admin = null;
        String sql = null;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            con = new MyConnexion().getConnection();
            st = con.createStatement();
            sql = "SELECT * FROM Admin WHERE nomadmin='"+email+"' AND mdp='"+mdp+"'";
            res = st.executeQuery(sql);
            admin = new Admin();
            while (res.next()) {
                admin.setId(res.getInt("id"));
                admin.setEmail(res.getString("nomadmin"));
                admin.setMdp(res.getString("mdp"));
            }
            System.out.println("sql === "+sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if(con != null){
                con.close();
            }
        }
        return admin;
    }

//    Fonction getNomCategorie
    public String getNomCategorie(int id) throws Exception {
        String nomcategorie = null;
        String sql = null;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            con = new MyConnexion().getConnection();
            st = con.createStatement();
            sql = "SELECT nomcategorie FROM Categorie WHERE id="+id;
            res = st.executeQuery(sql);
            while (res.next()) {
                nomcategorie = res.getString("nomcategorie");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }
            if (res != null) {
                res.close();
            }
            if(con != null){
                con.close();
            }
        }
        return nomcategorie;
    }
}
