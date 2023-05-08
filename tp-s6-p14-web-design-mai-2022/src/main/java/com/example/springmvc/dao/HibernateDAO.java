package com.example.springmvc.dao;

import com.example.springmvc.model.*;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.criterion.Order;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaQuery;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class HibernateDAO implements InterfaceDAO {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Object o) throws Exception {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
            System.out.println("Saved");
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        Session session = null;
        List<T> results = null;
        try {
            session = this.sessionFactory.openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cr = cb.createQuery(clazz);
            Root<T> root = cr.from(clazz);
            cr.select(root);
            Query<T> query = session.createQuery(cr);
            results = query.getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            if (session != null) session.close();
        }
        return results;
    }

    @Override
    public <T> T getById(Class<T> clazz, Integer id) throws Exception {
        Session session = null;
        T result = null;
        try {
            session = this.sessionFactory.openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cr = cb.createQuery(clazz);
            Root<T> root = cr.from(clazz);
            Predicate p = cb.equal(root.get("id"), id);
            addPredicatesToQuery(cb, cr, p);
            cr.select(root);
            Query<T> query = session.createQuery(cr);
            result = (T) query.getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            if (session != null) session.close();
        }
        return result;
    }

    @Override
    public void update(Object o) throws Exception {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
            System.out.println("Updated");
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public void delete(Object o) throws Exception {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            System.out.println("Deleted");
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public <T> List<T> getAllByPagination(Class<T> clazz, Integer offset, Integer limit) {
        Session session = null;
        List<T> results = null;
        try {
            session = this.sessionFactory.openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cr = cb.createQuery(clazz);
            Root<T> root = cr.from(clazz);
            cr.select(root);
            Query<T> query = session.createQuery(cr).setFirstResult(offset).setMaxResults(limit);
            results = query.getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
        return results;
    }

    private void addPredicatesToQuery(CriteriaBuilder cb, CriteriaQuery query, Predicate... predicates) {
        query.where(cb.and(predicates));
    }

//    Zahh
//    public Integer getLastIdScene(){
//        Session session = sessionFactory.openSession();
//        Transaction tx = session.beginTransaction();
//
//        Criteria criteria = session.createCriteria(Scene.class)
//                .addOrder(Order.desc("id"))
//                .setMaxResults(1);
//        Scene lastScene = (Scene) criteria.uniqueResult();
//        Integer lastSceneId = lastScene.getId();
//
//        tx.commit();
//        session.close();
//
//        return lastSceneId;
//    }
//
//    public Scene getSceneById(Integer id) {
//        Session session = sessionFactory.getCurrentSession();
//        Transaction transaction = null;
//        Scene scene = null;
//        try {
//            transaction = session.beginTransaction();
//            scene = (Scene) session.get(Scene.class, id);
//            transaction.commit();
//        } catch (HibernateException e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//        return scene;
//    }
//
//    public List<DetailScene> getAllBy(Integer idscene) {
//        Session session = null;
//        Transaction transaction = null;
//        List<DetailScene> scenes = null;
//        try{
//            session = this.sessionFactory.openSession();
//            transaction = session.beginTransaction();
//            Criteria criteria = session.createCriteria(DetailScene.class);
//            criteria.createAlias("scene","s");
//            criteria.add(Restrictions.eq("s.id",idscene));
//            scenes = criteria.list();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        } finally {
//            if (session != null) session.close();
//        }
//        return scenes;
//    }
//
//    public void updateDureeScene(Integer id, double duree) throws Exception { // tsy généralisé
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            session = this.sessionFactory.openSession();
//            transaction = session.beginTransaction();
//            Scene scene = (Scene) session.get(Scene.class, id);
//            scene.setDuree(duree);
//            session.update(scene);
//            transaction.commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        } finally {
//            if (session != null) session.close();
//        }
//    }
//
////    TSY MBOLA METY
//    public List<Scene> getSceneWithPlateauLibre(){ // mbola mila ampiana condititon status scene non tourné
//        Session session = null;
//        Transaction transaction = null;
//        List<Scene> scenes = null;
//        try{
//            session = this.sessionFactory.openSession();
//            transaction = session.beginTransaction();
//            Criteria criteria = session.createCriteria(Scene.class);
//            criteria.createAlias("plateau","p");
//            criteria.add(Restrictions.eq("p.etat",0));
//            scenes = criteria.list();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        } finally {
//            if (session != null) session.close();
//        }
//        return scenes;
//    }
//
//    public void updateStatusScene(Integer id, Integer status) throws Exception { // tsy généralisé
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            session = this.sessionFactory.openSession();
//            transaction = session.beginTransaction();
//            Scene scene = (Scene) session.get(Scene.class, id);
//            scene.setStatus(status);
//            session.update(scene);
//            transaction.commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        } finally {
//            if (session != null) session.close();
//        }
//    }
//
//    public void updateEtatPlateau(Integer id, Integer status) throws Exception { // tsy généralisé
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            session = this.sessionFactory.openSession();
//            transaction = session.beginTransaction();
//            Plateau p = (Plateau) session.get(Plateau.class, id);
//            p.setEtat(status);
//            session.update(p);
//            transaction.commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        } finally {
//            if (session != null) session.close();
//        }
//    }
//
//
//    public List<Integer> listPlateauByIdScene(Integer sceneId) {
//        Session session = null;
//        Transaction transaction = null;
//        List<Integer> plateauIds = null;
//
//        try{
//            session = this.getSessionFactory().getCurrentSession();
//            transaction = session.beginTransaction();
//            Criteria criteria = session.createCriteria(Scene.class)
//                    .add(Restrictions.eq("id", sceneId))
//                    .createCriteria("plateau")
//                    .setProjection(Projections.property("id"));
//            plateauIds = criteria.list();
//            session.getTransaction().commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        }
//        finally {
//            if (session != null) session.close();
//        }
//
//        return plateauIds;
//    }
//
//    public List<Scene> findScenesById(Integer sceneId) {
//        Session session = null;
//        Transaction transaction = null;
//        List<Scene> scenes = null;
//
//        try {
//            session = this.getSessionFactory().getCurrentSession();
//            transaction = session.beginTransaction();
//            Criteria criteria = session.createCriteria(Scene.class)
//                    .add(Restrictions.eq("id", sceneId));
//            scenes = criteria.list();
//            session.getTransaction().commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        }
//        finally {
//            if (session != null) session.close();
//        }
//        return scenes;
//    }
//
//    public List<Scene> findScenesByStatus(Integer status) {
//        Session session = null;
//        Transaction transaction = null;
//        List<Scene> scenes = null;
//
//        try {
//            session = this.getSessionFactory().getCurrentSession();
//            transaction = session.beginTransaction();
//            Criteria criteria = session.createCriteria(Scene.class)
//                    .add(Restrictions.eq("status", status));
//            scenes = criteria.list();
//            session.getTransaction().commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        }
//        finally {
//            if (session != null) session.close();
//        }
//        return scenes;
//    }
//
//    public List<Plateau> findPlateauxById(Integer plateauId) {
//        Session session = null;
//        Transaction transaction = null;
//        List<Plateau> plateaux = null;
//
//        try {
//            session = this.getSessionFactory().getCurrentSession();
//            transaction = session.beginTransaction();
//            Criteria criteria = session.createCriteria(Plateau.class)
//                    .add(Restrictions.eq("id", plateauId));
//            plateaux = criteria.list();
//            session.getTransaction().commit();
//        }
//        catch (Exception e){
//            transaction.rollback();
//            throw e;
//        }
//        finally {
//            if (session != null) session.close();
//        }
//        return plateaux;
//    }
//
//    public List<Scene> getScenesNonOccupees(Date startDate, Date endDate) {
//        Session session = null;
//        Criteria criteria = null;
//        Transaction transaction = null;
//        try {
//            session = this.getSessionFactory().getCurrentSession();
//            transaction = session.beginTransaction();
//            criteria = session.createCriteria(Scene.class);
//            criteria.createAlias("plateau", "p"); // Créer une jointure avec la table plateau associée
//            criteria.add(Restrictions.eq("status", 3)); // Condition de status égal à 0
//            DetachedCriteria subCriteria = DetachedCriteria.forClass(PlateauIndisponible.class)
//                    .setProjection(Property.forName("plateau.id"))
//                    .add(Restrictions.between("date", startDate, endDate));
//            criteria.add(Subqueries.propertyNotIn("p.id", subCriteria));
//            List<Scene> results = criteria.list();
//            transaction.commit();
//            return results;
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            throw e;
//        } finally {
//            if (session != null) session.close();
//        }
//    }
////SELECT * FROM Scene WHERE status=0 AND idPlateau NOT IN(SELECT idPlateau FROM PLateauIndisponible WHERE date BETWEEN date1 AND date2) OR id IN (SELECT idScene FROM DetailScene WHERE idPersonnage NOT IN(SELECT idPersonnage FROM PersonnageIndisponible WHERE date BETWEEN date1 AND date2))
////public List<Scene> getAvailableScenes(Date date1, Date date2) {
////    Session session = sessionFactory.getCurrentSession();
////    Transaction transaction = session.beginTransaction();
////    CriteriaBuilder builder = session.getCriteriaBuilder();
////    CriteriaQuery<Scene> query = builder.createQuery(Scene.class);
////    Root<Scene> root = query.from(Scene.class);
////
////    Subquery<Integer> subquery1 = query.subquery(Integer.class);
////    Root<PlateauIndisponible> subRoot1 = subquery1.from(PlateauIndisponible.class);
////    subquery1.select(subRoot1.get("plateau").get("id"));
////    subquery1.where(builder.between(subRoot1.get("date"), date1, date2));
////
////    Subquery<Integer> subquery2 = query.subquery(Integer.class);
////    Root<PersonnageIndisponible> subRoot2 = subquery2.from(PersonnageIndisponible.class);
////    subquery2.select(subRoot2.get("personnage").get("id"));
////    subquery2.where(builder.between(subRoot2.get("date"), date1, date2));
////
////    Join<Scene, DetailScene> join1 = root.join("detailscene");
////    Join<DetailScene, Personnage> join2 = join1.join("personnage");
////
////    Predicate predicate1 = builder.and(builder.equal(root.get("status"), 0),
////            builder.not(root.get("plateau").get("id").in(subquery1)),
////            builder.not(root.get("id").in(query.select(join1.get("idScene"))
////                    .where(builder.not(join2.get("idPersonnage").in(subquery2))))));
////
////    query.select(root).where(predicate1);
////
////    List<Scene> results = session.createQuery(query).getResultList();
////
////    transaction.commit();
////
////    return results;
////}

}