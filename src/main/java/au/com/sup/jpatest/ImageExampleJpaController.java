/*
 * $Id: ImageExampleJpaController.java,v 1.1 2012/05/13 22:05:42 paul Exp $
 */

package au.com.sup.jpatest;

import au.com.sup.jpatest.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.transaction.UserTransaction;

/**
 *
 * @author Paul Snow
 */
public class ImageExampleJpaController {

    // The @PersistenceUnit is a tag to allow you to create your persistence unit with the appropriate name.
    // Thus it should be the same: @PersistenceUnit(unitName="H2JPAImageOpenJPA") == emf = Persistence.createEntityManagerFactory("H2JPAImageOpenJPA");
    // for some reason it was not working, so I have commented it out for now.
    // @PersistenceUnit(unitName="H2JPAImageOpenJPA")
    private EntityManagerFactory emf = null;

    @Resource
    private UserTransaction utx;

    /**
     *
     */
    public ImageExampleJpaController() {
        //
        //emf = Persistence.createEntityManagerFactory("JPATestPU");
        //emf = Persistence.createEntityManagerFactory("JPAImageOpenJPA");
        /*
           * Create an entity manager factory based on the name found in the persistence.xml
           * this allows you to have multiple persistence units defined.  Thus in the one
           * persistence.xml file you could have TestPersistenceUnit and ProdPersistenceUnit
           * and this would allow you to swap between say JPA and local DB's and Oracle in production.
           */
        emf = Persistence.createEntityManagerFactory("H2JPAImageOpenJPA");
    }

    /**
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    /**
     *
     * @param imageExample
     */
    public void create(ImageExample imageExample) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(imageExample);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    /**
     *
     * @param imageExample
     * @throws NonexistentEntityException
     * @throws Exception
     */
    public void edit(ImageExample imageExample) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            imageExample = em.merge(imageExample);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = imageExample.getId();
                if (findImageExample(id) == null) {
                    throw new NonexistentEntityException("The imageExample with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    /**
     *
     * @param id
     * @throws NonexistentEntityException
     */
    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ImageExample imageExample;
            try {
                imageExample = em.getReference(ImageExample.class, id);
                imageExample.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The imageExample with id " + id + " no longer exists.", enfe);
            }
            em.remove(imageExample);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    /**
     *
     * @return
     */
    public List<ImageExample> findImageExampleEntities() {
        return findImageExampleEntities(true, -1, -1);
    }
    /**
     *
     * @param maxResults
     * @param firstResult
     * @return
     */
    public List<ImageExample> findImageExampleEntities(int maxResults, int firstResult) {
        return findImageExampleEntities(false, maxResults, firstResult);
    }
    /**
     *
     * @param all
     * @param maxResults
     * @param firstResult
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<ImageExample> findImageExampleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from ImageExample as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    /**
     *
     * @param id
     * @return
     */
    public ImageExample findImageExample(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ImageExample.class, id);
            /**
             *  Getting fresh results from database
             * To get fresh data from database there are several ways. First way is using refresh operations. Other ways are explained in the following sections.
             * There are two kinds of refresh operations â€“ portable EntityManager.refresh() and TopLink-specific refresh hint.
             * 1. EntityManager.refresh()
             * Use find and refresh combination like below. This is a simple and portable way to get fresh data.
             * Employee e = em.find(Employee.class, id);
             * try {
             *   em.refresh(e);
             * } catch(EntityNotFoundException ex){
             *   e = null;
             * }
             */
        } finally {
            em.close();
        }
    }
    /**
     *
     * @return
     */
    public int getImageExampleCount() {
        EntityManager em = getEntityManager();
        try {
            return ((Long) em.createQuery("select count(o) from ImageExample as o").getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /** <code>delete from Human o</code> */
    public int deleteAll() {
        //return getEntityManager().createNamedQuery("ImageExample.deleteAll").executeUpdate();

        EntityManager em = null;

        int returnInt = 0;

        try {
            em = getEntityManager();
            em.getTransaction().begin();

            try {
                returnInt = em.createNamedQuery("ImageExample.deleteAll").executeUpdate();
            } catch (EntityNotFoundException enfe) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, enfe);
            }
            em.getTransaction().commit();
            em.flush();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return returnInt;

    }

    /** <code>delete from Human o</code> */
    //public List<Human> getHumanDeleteAllByRange(int firstResult,
    //                                            int maxResults) {
    //    Query query = getEntityManager().createNamedQuery("Human.deleteAll");
    //    if (firstResult > 0) {
    //        query = query.setFirstResult(firstResult);
    //    }
    //    if (maxResults > 0) {
    //        query = query.setMaxResults(maxResults);
    //   }
    //    return query.getResultList();
    //}


}
