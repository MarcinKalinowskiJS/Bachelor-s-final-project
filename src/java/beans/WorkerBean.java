/*
 * To change this license header, choose License Headers lastStateToBeRemoved Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template lastStateToBeRemoved the editor.
 */
package beans;

import Pojos.Kategorie;
import Pojos.Obrazyproduktow;
import Pojos.ObrazyproduktowId;
import Pojos.OpisyStany;
import Pojos.Opisyproduktow;
import Pojos.Produkty;
import Pojos.ProduktySegmenty;
import Pojos.ProduktySegmentyId;
import Pojos.ProduktyStany;
import Pojos.Stany;
import Pojos.Uzytkownicy;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Marcin
 */
@ManagedBean
@ViewScoped
public class WorkerBean {

    //Oba pola do dodawania obrazków
    private Obrazyproduktow obrazek = null;
    private UploadedFile obrazekUF;
    private Opisyproduktow opToAdd;
    private Produkty prToAdd;
    private Integer idProduktuToChange;
    private Stany completionState = null;
    private Uzytkownicy selectedUserData;
    private String password;

    /**
     * Creates a new instance of workerBean
     */
    public WorkerBean() {
        opToAdd = new Opisyproduktow();
        obrazek = new Obrazyproduktow();
        obrazek.setId(new ObrazyproduktowId());
    }

    public Integer getIdProduktuToChange() {
        return idProduktuToChange;
    }

    public void setIdProduktuToChange(Integer idProduktuToChange) {
        this.idProduktuToChange = idProduktuToChange;
    }

    public Produkty getPrToAdd() {
        return prToAdd;
    }

    public void setPrToAdd(Produkty prToAdd) {
        this.prToAdd = prToAdd;
    }

    public Opisyproduktow getOpToAdd() {
        return opToAdd;
    }

    public void setOpToAdd(Opisyproduktow opToAdd) {
        this.opToAdd = opToAdd;
    }

    public Obrazyproduktow getObrazek() {
        return obrazek;
    }

    public void setObrazek(Obrazyproduktow obrazek) {
        this.obrazek = obrazek;
    }

    public Stany getCompletionState() {
        return completionState;
    }

    public void setCompletionState(Stany completionState) {
        this.completionState = completionState;
    }

    public Uzytkownicy getSelectedUserData() {
        return selectedUserData;
    }

    public void setSelectedUserData(Uzytkownicy selectedUserData) {
        this.selectedUserData = selectedUserData;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UploadedFile getObrazekUF() {
        return obrazekUF;
    }

    public void setObrazekUF(UploadedFile obrazekUF) {
        this.obrazekUF = obrazekUF;
    }

    public int getMaxIdObrazkaForIdOpisu(int id) {
        Session session = MagazynHibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("select ob.id.idObrazka from Obrazyproduktow ob "
                + "where ob.id.idOpisu=:idOpisu ORDER BY ob.id.idObrazka DESC");
        query.setParameter("idOpisu", id);

        Integer maxIdObrazka = null;
        if (query.list().size() == 0) {
            System.out.println("WorkerBean getMaxIdOBrazkaForIdOpisu: NULL dla idOpisu:" + id);
            maxIdObrazka = new Integer(0);
        } else {
            maxIdObrazka = (Integer) (query.list().get(0));
        }
        System.out.println("MaxIdObrazka: " + maxIdObrazka);
        session.close();
        return maxIdObrazka;
    }

    public List<Opisyproduktow> getIdOpisowSelect() {
        List<Opisyproduktow> opList = (List<Opisyproduktow>) (Object) WorkerBean.
                findObjectsByProperty("Opisyproduktow", "1", 1);

        return opList;
    }

    public void onIdOpisuChange() {//W upload_photots.xhtml do pobierania danych dla produktów o danym id
        Integer idOpisu = obrazek.getId().getIdOpisu();
        System.out.println("WorkerBean.onIdOpisuChange Id Opisu Obrazka" + idOpisu);
        Opisyproduktow op = (Opisyproduktow) ((WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu)).get(0));
        System.out.println("WorkerBean.onIdOpisuChange Size:" + WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu).size());

        String message = "";
        boolean breakLine = false;
        boolean insertSpace = false;
        if (op.getProducent() != null) {
            message += "<br/><b>Producent:</b>" + op.getProducent();
            insertSpace = true;
            breakLine = true;
        }
        if (op.getKodKreskowy() != null) {
            if (insertSpace == true) {
                insertSpace = false;
                message += " ";
            }
            message += " <b>Kod kreskowy:</b>" + op.getKodKreskowy();
            breakLine = true;
            insertSpace = true;
        }
        if (breakLine == true) {
            insertSpace = false;
            message += "<br/>";
            breakLine = false;
        }
        if (op.getSeria() != null) {
            if (insertSpace == true) {
                insertSpace = false;
                message += " ";
            }
            message += "<b>Seria:</b>" + op.getSeria();
            insertSpace = true;
            breakLine = true;
        }
        if (op.getModel() != null) {
            if (insertSpace == true) {
                insertSpace = false;
                message += " ";
            }
            message += "<b>Model:</b>" + op.getModel();
            breakLine = true;
            insertSpace = true;
        }
        if (breakLine == true) {
            insertSpace = false;
            message += "<br/>";
            breakLine = false;
        }
        if (op.getOpis() != null) {
            if (insertSpace == true) {
                insertSpace = false;
                message += " ";
            }
            message += "<b>Opis:</b>" + op.getOpis();
            breakLine = true;
        }
        FacesContext.getCurrentInstance().addMessage("DodajObrazekForm:Opis", new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Info", message));
    }

    public void onAddPhoto() {
        Session session = MagazynHibernateUtil.getSessionFactory().openSession();

        //Walidacja danych do dodawania obrazka
        //Obrazek przetwarzany
        if (obrazekUF != null) {
            System.out.println("Rozmiar obrazka: " + obrazekUF.getSize());
        } else {
            System.out.println("ObrazekUF == null");
            //Info o braku obrazka
            return;
        }
        byte[] b = null;
        try {
            InputStream is = obrazekUF.getInputstream();
            b = new byte[(int) obrazekUF.getSize()];
            DataInputStream dis = new DataInputStream(is);
            dis.readFully(b);
        } catch (Exception e) {
            System.out.println("Błąd wgrywania obrazu na serwer");
            e.printStackTrace();
        }
        //Wyszukiwanie wolnego id dla obrazka
        obrazek.getId().setIdObrazka(getMaxIdObrazkaForIdOpisu(obrazek.getId().getIdOpisu()) + 1);

        //Wgrywanie obrazka do bazy danych
        obrazek.setObrazek(b);
        session.beginTransaction();
        session.save(obrazek);
        session.getTransaction().commit();
        session.close();

    }

    public void addNewProdukt() {
        if (opToAdd == null || opToAdd.getIdOpisu() == null || opToAdd.getIdOpisu() == -1) {
            return;
        }
        String outputMessage = "";
        Integer sendedMails = 0;
        Integer notSendedMails = 0;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime()) + " ";
        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Produkty p = new Produkty();
            p.extractProduktFromOpis(p, opToAdd);
            p.setOpisyproduktow(opToAdd);
            //opToAdd = (Opisyproduktow) session.merge(opToAdd);
            session.refresh(opToAdd);
            if (opToAdd.getProdukties() == null) {
                opToAdd.setProdukties(new HashSet());
            }
            session.save(p);

            opToAdd.getProdukties().add(p);
            session.save(opToAdd);
            tx.commit();

            outputMessage += time + "Dodano produkt: " + p.getKodKreskowy();

            //Pobierz listę maili subskrybentów
            Set<Uzytkownicy> usersToSendMailList = opToAdd.getUzytkownicies();

            if (usersToSendMailList != null) {
                List<String> mailsToSend = new ArrayList<>();
                for (Uzytkownicy u : usersToSendMailList) {
                    mailsToSend.add(u.getEmail());
                }

                try {
                    String subject = "Nowy produkt " + opToAdd.getNazwa();
                    String message = "Witaj, subskrybowałeś się na produkt dostępny "
                            + "pod linkiem http://localhost:8084/Magazyn/magazyn/"
                            + "guest/szczegoly_produktu.xhtml?idOpisu=" + opToAdd.getIdOpisu()
                            + "&idProduktu=" + p.getIdProduktu();
                    for (String s : mailsToSend) {
                        boolean errorSendingMail = SendMailSSLBean.sendMailSSL(subject, message, s);
                        if (errorSendingMail == false) {
                            sendedMails++;
                        } else {
                            notSendedMails++;
                        }
                    }
                    outputMessage += "<br/>i wysłano " + sendedMails + " maili do subskrybentów";
                    outputMessage += "<br/>błąd wysłania " + notSendedMails + " maili do subskrybentów";
                } catch (Exception e) {
                }
                FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                        new FacesMessage(outputMessage));
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(time + "Błąd dodania produktu: " + opToAdd.getKodKreskowy()));

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

//Pobranie ogólnego opisu produktu połączonego z szczegółowym produktem do wyświetlania
    public void getOpisyProduktowInWorkerBean(Integer idOpisu, Integer idProduktu) {
        int errors = 0;
        opToAdd = null;
        if ((idOpisu == null || idOpisu == -1 || idOpisu == 0)) {
            errors += 1;
        } else {
            if (idProduktu != null && idProduktu != -1 && idProduktu != 0) {
                System.out.println("WorkerBean getOpisyProduktowInWorkerBean(" + idOpisu + ", " + idProduktu + ")");
                List<Opisyproduktow> opList = (List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu);
                List<Produkty> pList = (List<Produkty>) (Object) WorkerBean.findObjectsByProperty("Produkty", "idProduktu", idProduktu);
                if ((opList != null && pList != null) && (opList.size() > 0 && pList.size() > 0)) {
                    opToAdd = Opisyproduktow.mergeWithProdukt(opList.get(0), pList.get(0));
                } else {
                    errors += 2;
                }
            } else {//Wczytywanie danych ogólnych
                List<Opisyproduktow> opList = (List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu);
                if (opList != null && opList.size() > 0) {
                    opToAdd = opList.get(0);
                } else {
                    errors += 2;
                }
            }
        }
    }

    public void addNewOpisyProduktow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String message = sdf.format(cal.getTime()) + " ";
        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            opToAdd.setIdOpisu(null);
            session.save(session.merge(opToAdd));
            tx.commit();
            message += "Dodano opis produktu o id: " + opToAdd.getIdOpisu();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            message += "Błąd dodania opisu produktów";
            System.out.println("WorkerBean addNewOpisyProduktow() Error");
        } finally {
            if (session != null) {
                session.close();
            }
        }
        FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                new FacesMessage(message));
    }

    public void setOpisproduktu() {
        Session session = null;
        Transaction tx = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String message = sdf.format(cal.getTime()) + " ";
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(session.merge(opToAdd));
            tx.commit();
            message += "Zmieniono opis o id: " + opToAdd.getIdOpisu();

        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            message += "Błąd zmiany opisu o id: " + opToAdd.getIdOpisu();

        } finally {
            if (session != null) {
                session.close();
            }
        }
        FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                new FacesMessage(message));
    }

    public void setProduktDetails(Integer idProduktu) {

        if (idProduktu == null) {
            System.out.println("IdProduktu = NULL");
            return;
        }
        System.out.println("Update produktu" + idProduktu);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime());
        Session session = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            Produkty p = (Produkty) session.get(Produkty.class,
                    idProduktu);
            p.extractProduktFromOpis(p, opToAdd);
            session.beginTransaction();
            session.update(p);
            session.getTransaction().commit();
            session.close();
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(time + "<br/>Zmieniono produkt o id: " + idProduktu));
        } catch (Exception e) {
            if (session != null) {
                session.close();
            }
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(time + "<br/>Błąd zmiany produktu o id: " + idProduktu));
        }
    }

    public void deleteProduktOrOpis(int idOpisu, Integer idProduktu) {//Tutaj przekierowanie
        if (idProduktu == 0 || idProduktu == null || idProduktu == -1) {
            List<Opisyproduktow> opisyToDelete = (List<Opisyproduktow>) (Object) findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu);
            Session session = MagazynHibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            for (Opisyproduktow p : opisyToDelete) {
                session.delete(p);
            }
            session.getTransaction().commit();
            session.close();
        } else {
            List<Produkty> produktyToDelete = (List<Produkty>) (Object) findObjectsByProperty("Produkty", "idProduktu", idProduktu);
            Session session = MagazynHibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            for (Produkty p : produktyToDelete) {
                session.delete(p);
            }
            session.getTransaction().commit();
            session.close();
        }

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/magazyn/guest/lista_produktow.xhtml");
        } catch (Exception e) {
            System.out.println("Błąd przekierowania WorkerBean.deleteProdukt(idOpisu, idProduktu)");
        }
    }

    public List<String> getSegmenty(Integer idProduktu) {

        Session session = MagazynHibernateUtil.getSessionFactory().openSession();
        Query qMain = session.createQuery("select ps.id.nazwaSegmentu from Produkty p, ProduktySegmenty ps"
                + " where p.idProduktu = :idProduktu AND p.idProduktu = ps.id.idProduktu");
        qMain.setParameter("idProduktu", idProduktu);

        System.out.println("idProduktu:" + idProduktu + " SizeQMainGetSegmenty" + qMain.list().size());
        List<String> segmenty = (List<String>) qMain.list();
        return segmenty;
    }

    public static List<Object> findObjectsByProperty(String objectName, String propertyName, Object value) {
        Session session = null;
        Transaction tx = null;
        List<Object> qList = null;
        try {
            String q;
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            //tx = session.beginTransaction();
            Query qMain = session.createQuery(q = ("from " + objectName + " where " + propertyName + " = :Value"));
            qMain.setParameter("Value", value);
            System.out.println("Query in WorkerBean findObjectsByProperty:" + q + " Value:" + value + " ValType:" + value.getClass());
            qList = qMain.list();
            //tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println("function in WorkerBean findObjectsByProperty error");
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return qList;
    }
//Znaleźć usages

    public static List<Object> findObjectsByProperty(Session session, String objectName, String propertyName, Object value) {
        Transaction tx = null;
        List<Object> qList = null;
        try {
            //tx = session.beginTransaction();
            String q;
            Query qMain = session.createQuery(q = ("from " + objectName + " where " + propertyName + " = :Value"));
            qMain.setParameter("Value", value);
            System.out.println("Query in WorkerBean findObjectsByProperty:" + q + " Value:" + value + " ValType:" + value.getClass());
            qList = qMain.list();
            //tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            System.out.println("function in WorkerBean findObjectsByProperty error");
        }
        return qList;
    }

    public static List<Object> findObjectsByProperties(String objectName, List<String> properties, List<Object> values, String connector, String equalSign) {
        System.out.println("findObjectsByProperties " + properties.size() + " " + values.size());
        Session session = null;
        Transaction tx = null;
        List<Object> qList = null;
        try {
            if (properties.size() != values.size()) {
                System.out.println("Properties and values do not mach");
                throw new IllegalArgumentException();
            }

            session = MagazynHibernateUtil.getSessionFactory().openSession();
            //tx = session.beginTransaction();
            String qString = "from " + objectName + " where";

            for (int i = 0; i < properties.size(); i++) {
                qString += " " + properties.get(i) + " " + equalSign + " :" + properties.get(i).replace(".", "") + i;

                if (i + 1 < properties.size()) {
                    qString += " " + connector;
                }
            }

            //System.out.println("qString" + qString);
            Query qMain = session.createQuery(qString);

            for (int i = 0; i < properties.size(); i++) {
                if (equalSign.toLowerCase().equals("like")) {
                    qMain.setParameter(properties.get(i).replace(".", "") + i, "%" + values.get(i) + "%");
                } else {
                    qMain.setParameter(properties.get(i).replace(".", "") + i, values.get(i));
                }

                //System.out.println("SetProperty" + properties.get(i).replace(".", "") + i + " Val:" + values.get(i) + " ValType:" + values.get(i).getClass());
            }
            qList = qMain.list();
            //tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println("function in WorkerBean findObjectByProperties error");
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return qList;
    }

    public static List<Object> findObjectsByProperties(Session session, String objectName, List<String> properties, List<Object> values, String connector, String equalSign) {
        Transaction tx = null;
        List<Object> qList = null;
        System.out.println("findObjectsByProperties " + properties.size() + " " + values.size());
        try {
            //tx = session.beginTransaction();
            if (properties.size() != values.size()) {
                System.out.println("Properties and values do not mach");
                throw new IllegalArgumentException();
            }
            String qString = "from " + objectName + " where";

            for (int i = 0; i < properties.size(); i++) {
                qString += " " + properties.get(i) + " " + equalSign + " :" + properties.get(i).replace(".", "") + i;

                if (i + 1 < properties.size()) {
                    qString += " " + connector;
                }
            }

            //System.out.println("qString" + qString);
            Query qMain = session.createQuery(qString);

            for (int i = 0; i < properties.size(); i++) {
                if (equalSign.toLowerCase().equals("like")) {
                    qMain.setParameter(properties.get(i).replace(".", "") + i, "%" + values.get(i) + "%");
                } else {
                    qMain.setParameter(properties.get(i).replace(".", "") + i, values.get(i));
                }

                //System.out.println("SetProperty" + properties.get(i).replace(".", "") + i + " Val:" + values.get(i) + " ValType:" + values.get(i).getClass());
            }
            qList = qMain.list();
            //tx.commit();
        } catch (Exception e) {
            /*if (tx != null) {
                tx.rollback();
            }*/
            System.out.println("function in WorkerBean findObjectByProperties error");
        }
        return qList;
    }

    public static List<Object> findObjectsByPropertiesAndGroupBy(String objectName, List<String> properties, List<Object> values, String connector, String equalSign, String fieldToGroupBy) {
        System.out.println("findObjectsByPropertiesAndGroupBy " + properties.size() + " " + values.size());
        Session session = null;
        Transaction tx = null;
        List<Object> qList = null;
        try {
            if (properties.size() != values.size()) {
                System.out.println("Properties and values do not mach");
                throw new IllegalArgumentException();
            }
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            //tx = session.beginTransaction();
            String qString = "from " + objectName + " where";

            for (int i = 0; i < properties.size(); i++) {
                qString += " " + properties.get(i) + " " + equalSign + " :" + properties.get(i).replace(".", "") + i;

                if (i + 1 < properties.size()) {
                    qString += " " + connector;
                }
            }

            qString += " group by " + fieldToGroupBy;

            //System.out.println("qString" + qString);
            Query qMain = session.createQuery(qString);

            for (int i = 0; i < properties.size(); i++) {
                if (equalSign.toLowerCase().equals("like")) {
                    qMain.setParameter(properties.get(i).replace(".", "") + i, "%" + values.get(i) + "%");
                } else {
                    qMain.setParameter(properties.get(i).replace(".", "") + i, values.get(i));
                }

                //System.out.println("SetProperty" + properties.get(i).replace(".", "") + i + " Val:" + values.get(i) + " ValType:" + values.get(i).getClass());
            }

            qList = qMain.list();
        } catch (Exception e) {
            /*if (tx != null) {
                tx.rollback();
            }*/
            System.out.println("function in WorkerBean findObjectByProperties error");
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return qList;
    }

    public static boolean addObjectToTable(Object o) {
        Session session = MagazynHibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        boolean inserted = false;

        try {
            session.saveOrUpdate(o);
            tx.commit();
            inserted = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
        return inserted;
    }

    public static boolean deleteObjectFromTable(Object o) {
        Session session = null;
        Transaction tx = null;
        boolean deleted = false;

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            deleted = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return deleted;
    }

    public static List<Object> getValuesFromTableByProperty(String tableName, String propertyForSearch,
            Object propertyValue, String propertyForReturn) {
        Session session = null;
        Transaction tx = null;
        List<Object> valuesFromDB = new ArrayList<>();
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            String q = "";
            Query query = session.createQuery(q = "from " + tableName + " where " + propertyForSearch
                    + " = :" + propertyForSearch.replace(".", ""));
            query.setParameter(propertyForSearch.replace(".", ""), propertyValue);

            //Tworzenie metod do pobierania wartości
            String[] propertiesFS;
            List<Method> methods = new ArrayList<>();
            if ((propertiesFS = propertyForSearch.split("\\.")).length > 1) {
                propertiesFS[0] = propertiesFS[0].substring(0, 1).toUpperCase() + propertiesFS[0].substring(1);
                propertiesFS[1] = propertiesFS[1].substring(0, 1).toUpperCase() + propertiesFS[1].substring(1);
                methods.add(Class.forName("Pojos." + tableName).getMethod("get" + propertiesFS[0]));
                methods.add(Class.forName("Pojos." + tableName + "Id").getMethod("get" + propertiesFS[1]));
            } else {
                propertiesFS[0] = propertiesFS[0].substring(0, 1).toUpperCase() + propertiesFS[0].substring(1);
                methods.add(Class.forName("Pojos." + tableName).getMethod("get" + propertiesFS[0]));
            }

            //Pobieranie wartości z pola obiektu i dodawanie do listy
            if (propertiesFS.length > 1) {
                for (Object o : query.list()) {
                    valuesFromDB.add(methods.get(1).invoke(methods.get(0).invoke(o)));
                }
            } else {
                for (Object o : query.list()) {
                    valuesFromDB.add(methods.get(0).invoke(0));
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println("Error in WorkerBean getPropertyFromTable");
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return valuesFromDB;
    }

    public static Object findObjectByProperty(Session session, String objectName, String propertyName, Object value) {
        Transaction tx = null;
        Object oReturn = null;
        try {
            //tx = session.beginTransaction();
            String q;
            Query qMain = session.createQuery(q = ("from " + objectName + " where " + propertyName + " = :Value"));
            qMain.setParameter("Value", value);
            System.out.println("Query in WorkerBean findObjectsByProperty:" + q + " Value:" + value + " ValType:" + value.getClass());
            oReturn = qMain.uniqueResult();
            //tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            System.out.println("function in WorkerBean findObjectsByProperty error");
        }
        return oReturn;
    }

    public void addSegment(String segName, Integer idProduktu) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        boolean errors = false;
        if (WorkerBean.findObjectsByProperties("ProduktySegmenty",
                Arrays.asList("id.nazwaSegmentu", "id.idProduktu"),
                Arrays.asList((Object) segName, (Object) idProduktu),
                "AND", "=").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(sdf.format(cal.getTime()) + " Produkt był już w danym segmencie"));
            return;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            List<Produkty> pList = (List<Produkty>) (Object) WorkerBean.findObjectsByProperty(session, "Produkty", "idProduktu", idProduktu);

            if (pList.size() > 0) {
                ProduktySegmenty newPS = new ProduktySegmenty(new ProduktySegmentyId(segName, idProduktu), pList.get(0));
                session.saveOrUpdate(newPS);
                pList.get(0).getProduktySegmenties().add(newPS);
            } else {
                throw new NullPointerException("Not found produkty for " + idProduktu);
            }

            tx.commit();
        } catch (Exception e) {
            System.out.println("WorkerBean addSegment(" + segName + ", " + idProduktu + ") Error");
            errors = true;
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        if (errors == false) {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(sdf.format(cal.getTime()) + " Dodano powiązanie produktu i segmentu"));
        } else {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(sdf.format(cal.getTime()) + " Błąd powiązania produktu i segmentu"));
        }
    }

    public void deleteSegment(String segName, Integer idProduktu) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        boolean errors = false;
        String message = sdf.format(cal.getTime()) + " ";
        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            List<ProduktySegmenty> psList = (List<ProduktySegmenty>) (Object) (WorkerBean.findObjectsByProperties(
                    session,
                    "ProduktySegmenty", Arrays.asList("id.nazwaSegmentu", "id.idProduktu"),
                    Arrays.asList((Object) segName, (Object) idProduktu),
                    "AND", "="));
            if (psList.size() < 1) {
                FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                        new FacesMessage(sdf.format(cal.getTime()) + " Brak połączenia<br/> danego segmentu i produktu"));
                return;
            }
            ProduktySegmenty ps = psList.get(0);

            //if (WorkerBean.deleteObjectFromTable(ps)) {
            session.delete(ps);
            tx.commit();
            message += "Usunięto segment z produktu";
        } catch (Exception e) {
            errors = true;
            if (tx != null) {
                tx.rollback();
            }
            message += " Błąd usuwania segmentu";
        } finally {
            if (session != null) {
                session.close();
            }
        }

        FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                new FacesMessage(message));
    }

    public List<Stany> getAllTasks(Integer doState) {//0-To do   -1-In progress -2-Done -3-All

        System.out.println("WorkerBean.getAllTasks doState=" + doState);
        List<String> statesForLooking;
        //Listy stanów dla różnych stopni wykonania
        if (doState == 0) {//Stany do zrobienia
            statesForLooking = Arrays.asList(
                    "Zapas mniej lub równy 100", "Brak towaru",
                    "Oczekuje na rozładunek",
                    "Zamówiony przez klienta", "Przyjęty na gwarancje",
                    "Przyjęty na reklamację", "Przybył z gwarancji",
                    "Przybył z reklamacji");
        } else if (doState == -1) {//Stany w trakcie robienia
            statesForLooking = Arrays.asList(
                    "W trakcie rozładunku", "W trakcie ładowania do utylizacji",
                    "W trakcie kompletacji", "W trakcie przyjmowania gwarancji",
                    "W trakcie przyjmowania reklamacji", "W trakcie przygotowywania do wysłania na gwarancję",
                    "W trakcie przygotowywania do wysłania na reklamację", "W trakcie wysyłki do klienta");
        } else if (doState == -2) {//Stany zrobione
            statesForLooking = Arrays.asList("Zamówiony u dostawcy",
                    "Odebrany od dostawcy", "Przeterminowany", "Oddany do utylizacji",
                    "Wysłany do klienta", "Dostarczony do klienta",
                    "Wysłany na gwarancję", "Wysłany na reklamację",
                    "Dotarczony na gwarancję", "Dostarczony na reklamację",
                    "Odebrany z gwarancji", "Odebrany z reklamacji");
        } else if (doState == -3) {
            statesForLooking = Arrays.asList(//Do zrobienia
                    "Zapas mniej lub równy 100", "Brak towaru",
                    "Oczekuje na rozładunek",
                    "Zamówiony przez klienta", "Przyjęty na gwarancje",
                    "Przyjęty na reklamację", "Przybył z gwarancji",
                    "Przybył z reklamacji",//W trakcie
                    "W trakcie rozładunku", "W trakcie ładowania do utylizacji",
                    "W trakcie kompletacji", "W trakcie przyjmowania gwarancji",
                    "W trakcie przyjmowania reklamacji", "W trakcie przygotowywania do wysłania na gwarancję",
                    "W trakcie przygotowywania do wysłania na reklamację",
                    "W trakcie wysyłki do klienta", //Zrobione
                    "Zamówiony u dostawcy",
                    "Odebrany od dostawcy", "Przeterminowany", "Oddany do utylizacji",
                    "Wysłany do klienta", "Dostarczony do klienta",
                    "Wysłany na gwarancję", "Wysłany na reklamację",
                    "Dotarczony na gwarancję", "Dostarczony na reklamację",
                    "Odebrany z gwarancji", "Odebrany z reklamacji");
        } else {
            System.out.println("WorkerBean.getAllTaska doState spoza zakresu");
            return null;
        }
        List<String> properties = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (String s : statesForLooking) {
            properties.add("nazwaStanu");
            values.add(s);
        }
        List<Stany> sList = (List<Stany>) (Object) WorkerBean.findObjectsByProperties(
                "Stany", properties, values, "OR", "=");

        sList.sort(Comparator.nullsFirst(
                Comparator.comparing(Stany::getDataRozpoczecia, Comparator.nullsFirst(
                        Comparator.naturalOrder()))));

        return sList;
    }

    //select o2 from Stany oa, ProduktyStany oo, Produkty o2 WHERE oa.idStanu = oo.id.idStanu AND oo.id.idProduktu = o2.idProduktu
    public static List<Object> getByConnectionTable(String t1, String tBetween, String t2, String t1con, String t2con) {
        Session session = null;
        Transaction tx = null;
        List<Object> qList = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Query q = session.createQuery("select t2 from " + t1 + " t1, " + tBetween + " tB, " + t2 + " t2 "
                    + "where t1." + t1con + " = tB." + t1con + " AND tB." + t2con + " = t2." + t2con);
            qList = q.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            /*
            if (session != null) {
                session.close();
            }*/
        }
        return qList;
    }

    public static List<Object> getByConnectionTableWhereT2Id(String t1, String tBetween, String t2, String t1con, String t2con, String t2IdName, Integer t2IdNumber) {
        Session session = null;
        Transaction tx = null;
        List<Object> qList = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Query q = session.createQuery("select t2 from " + t1 + " t1, " + tBetween + " tB, " + t2 + " t2 "
                    + "where t1." + t1con + " = tB." + t1con + " AND tB." + t2con + " = t2." + t2con + " AND "
                    + "WHERE " + t2 + "." + t2IdName + " = :id");
            q.setParameter("id", t2IdNumber);
            qList = q.list();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            /*
            if (session != null) {
                session.close();
            }*/
        }
        return qList;
    }

    //Nie wiem czy to poniżej jest potrzebne
    public void getLastState(Integer idProduktu) {
        //Pobranie ostatniego stanu
        Session session = MagazynHibernateUtil.getSessionFactory().openSession();
        Query q = session.createQuery("select s from Stany s,  where " + "AND");
//List<Stany> 
    }

    public List<Stany> getToPay() {
        Session session = null;
        Transaction tx = null;
        List<Stany> sListToBePayed = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            sListToBePayed = (List<Stany>) (Object) WorkerBean.findObjectsByProperty(session, "Stany", "nazwaStanu", "Oczekuje na zapłatę");
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return sListToBePayed;
    }

    public void setAsPayed(Integer idStanu) {
        Session session = null;
        Transaction tx = null;

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Stany s = ((List<Stany>) (Object) WorkerBean.findObjectsByProperty(session, "Stany", "idStanu", idStanu)).get(0);

            s.setNazwaStanu("Oczekuje na wysyłkę");

            session.saveOrUpdate(s);

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Stany> getToSend() {
        return (List<Stany>) (Object) WorkerBean.findObjectsByProperty("Stany", "nazwaStanu", "Oczekuje na wysyłkę");
    }

    public void getToSending(Integer idStanu) {
        //Ustaw stan jako w trakcie kompletacji
        Session session = null;
        Transaction tx = null;

        boolean canComplete = false;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Stany s = ((List<Stany>) (Object) WorkerBean.findObjectsByProperty(session, "Stany", "idStanu", idStanu)).get(0);

            if (s.getNazwaStanu().equals("Oczekuje na wysyłkę")) {
                canComplete = true;
                s.setNazwaStanu("W trakcie kompletacji");
                session.saveOrUpdate(s);
            }

            tx.commit();
        } catch (Exception e) {
            canComplete = false;
            //e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        //przekieruj na stronę tworzenia przesyłki, jeśli udało się zmienić nazwę stanu
        if (canComplete) {
            try {
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(ec.getRequestContextPath() + "/magazyn/worker/kompletacja.xhtml?faces-redirect=true&idStanu=" + idStanu);
            } catch (Exception e) {
                System.out.println("Błąd przekierowania");
                e.printStackTrace();
            }
        }
    }

    public void getToCompletion(Integer idStanu) {
        //Pobranie stanów 
        List<Stany> sList = (List<Stany>) (Object) WorkerBean.findObjectsByProperty("Stany", "idStanu", idStanu);

        if (sList != null && sList.size() > 0) {
            completionState = sList.get(0);
        } else {
            //Wiadomość o braku stanu
        }
    }

    public List<Produkty> getProduktyForCompletionState() {
        List<Produkty> pList = null;
        if (completionState != null) {
            Session session = null;
            Transaction tx = null;
            try {
                session = MagazynHibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                pList = new ArrayList<>();
                ProduktyStany psTmp;
                completionState = (Stany) session.merge(completionState);
                for (Object psOTmp : completionState.getProduktyStanies()) {
                    psTmp = (ProduktyStany) psOTmp;
                    Produkty pTmp = psTmp.getProdukty().copy();
                    pTmp.setIlosc(psTmp.getIlosc());
                    pList.add(pTmp);
                }
                tx.commit();
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        }
        return pList;
    }

    public void setAsSend(Integer idStanu) {
        System.out.println("WorkerBean setAsSend(" + idStanu + ")");
        Session session = null;
        Transaction tx = null;

        boolean checkCommit = false;
        boolean errorSendingMail = false;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Stany s = ((List<Stany>) (Object) WorkerBean.findObjectsByProperty(session, "Stany", "idStanu", idStanu)).get(0);

            s.setNazwaStanu("Oczekuje na dostarczenie");
            session.saveOrUpdate(s);
            String subject = "Przesyłka o id " + s.getIdStanu() + " została nadana";
            String message = "Id\tNazwa\tIlość\n";
            ProduktyStany psTmp;
            Integer actualId = 1;
            for (Object psOTmp : s.getProduktyStanies()) {
                psTmp = (ProduktyStany) psOTmp;
                //Tutaj zmieniłem możliwe że to do poprawy
                //psTmp.getProdukty().setIlosc(psTmp.getIlosc());
                message += actualId++ + " " + psTmp.getProdukty().getOpisyproduktow().getNazwa()
                        + " " + psTmp.getIlosc() + "\n";
            }
            errorSendingMail = SendMailSSLBean.sendMailSSL(subject, message, s.getUzytkownicy().getEmail());

            tx.commit();

            checkCommit = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        if (errorSendingMail) {//Wyświetlenie wiadomości o błędzie wysłania maila
            FacesContext.getCurrentInstance().addMessage("SendForm:sendDT", new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Warn", "Wysyłka zatwierdzona. Błąd wysłania maila o wysłanej przesyłce dla stanu " + idStanu));
        }else if (checkCommit) {//Przekierowanie do wszystkich towarów do wysyłki
            try {
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(ec.getRequestContextPath() + "/magazyn/worker/zadania_wysylka.xhtml?faces-redirect=true");
            } catch (Exception e) {
                System.out.println("Błąd przekierowania");
                e.printStackTrace();
            }
        }
    }

    public List<Uzytkownicy> getAllUzytkownicy() {
        return (List<Uzytkownicy>) (Object) WorkerBean.findObjectsByProperty("Uzytkownicy", "1", 1);
    }

    public void getUserDataInWorkerBean(Integer idUzytkownika) {
        System.out.println("WorkerBean getUserDataInWorkerBean TUTAJ JEST ID UZYTKWNIKA RÓWNE: " + idUzytkownika);
        selectedUserData = null;
        List<Uzytkownicy> uList = (List<Uzytkownicy>) (Object) WorkerBean.findObjectsByProperty("Uzytkownicy", "idUzytkownika", idUzytkownika);
        if (uList != null && uList.size() > 0) {
            selectedUserData = uList.get(0);
        }
    }

    public void saveUserDataFromWorkerBean() {
        System.out.println("public void saveUserDataFromWorkerBean");
        Session session = null;
        Transaction tx = null;
        boolean accessDenied = false;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String message = sdf.format(cal.getTime()) + " ";
        if (selectedUserData != null) {
            try {
                session = MagazynHibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();

                selectedUserData = (Uzytkownicy) session.merge(selectedUserData);
                session.saveOrUpdate(selectedUserData);

                tx.commit();
                message += "Poprawnie zmieniono dane";
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                message += "Błąd zmiany danych";
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        } else {
            accessDenied = true;
            message += "Brak dostępu";
        }

        //wiadomość o stanie zmiany danych użytkownika
        if (accessDenied) {
            FacesContext.getCurrentInstance().addMessage("UserForm:UserDataPanel", new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Warn", message));
        } else {
            FacesContext.getCurrentInstance().addMessage("UserForm:UserDataPanel", new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Info", message));
        }
    }

    public void changePassword(String username, String oldPwd, String newPwd1, String newPwd2) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String message = "";
        boolean errors = false;
        //String message = sdf.format(cal.getTime()) + " ";

        if (newPwd1.equals(newPwd2)) {
            Uzytkownicy u = LoginBean.validate(username, oldPwd);

            if (u != null) {
                Session session = null;
                Transaction tx = null;

                try {
                    session = MagazynHibernateUtil.getSessionFactory().openSession();
                    tx = session.beginTransaction();
                    session.refresh(u);
                    u.setHaslo(newPwd1);
                    tx.commit();
                    message += "Hasło zmienione";
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                } finally {
                    if (session != null) {
                        session.close();
                    }
                }
            } else {//Błędne stare hasło
                errors = true;
                message += "Błędne stare hasło";
            }
        } else {//Hasła są różne
            errors = true;
            message += "Nowe hasła się nie zgadzają";
        }

        message = sdf.format(cal.getTime()) + " " + message;

        if (errors) {
            FacesContext.getCurrentInstance().addMessage("UserForm:UserDataChangePasswordPanel",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", message));
        } else {
            FacesContext.getCurrentInstance().addMessage("UserForm:UserDataChangePasswordPanel",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", message));
        }
    }

    public void deleteProdukt(Integer idOpisu, Integer idProduktu) {
        System.out.println("WorkerBean deleteProdukt(" + idOpisu + ", " + idProduktu + ")");
        Session session = null;
        Transaction tx = null;
        boolean errors = false;
        String message = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Produkty pToDelete = (Produkty) WorkerBean.findObjectByProperty(session, "Produkty", "idProduktu", idProduktu);

            //Usuwanie segmentów produktu
            for (ProduktySegmenty psTmp : pToDelete.getProduktySegmenties()) {
                session.delete(psTmp);
            }

            if (pToDelete.getProduktyStanies() == null || pToDelete.getProduktyStanies().size() < 1) {
                session.delete(pToDelete);
                tx.commit();
                //Przekierowanie na strone z samym opisem gdy się udało usunąć
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(ec.getRequestContextPath() + "/magazyn/guest/szczegoly_produktu.xhtml"
                        + "?faces-redirect=true&idOpisu=" + idOpisu + "&idProduktu=-1");
                message += "Poprawnie usunięto produkt o id: " + idProduktu;
            } else {//Wypisanie wiadomości o błędzie, jeśli nie dało się usunąć
                tx.rollback();
                errors = true;
                message += "Błąd usunięcia. Prawdopodobnie produkt jest zapisany w jednym ze stanów";
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        message = sdf.format(cal.getTime()) + " " + message;
        if (errors) {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", message));
        } else {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", message));
        }
    }

    public void deleteOpis(Integer idOpisu) {
        Session session = null;
        Transaction tx = null;
        String message = "";
        boolean errors = false;
        int stanyProdukty;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Opisyproduktow opToDelete = (Opisyproduktow) WorkerBean.findObjectByProperty(session, "Opisyproduktow", "idOpisu", idOpisu);

            if (opToDelete.getOpisyStanies() == null || opToDelete.getOpisyStanies().size() < 1) {
                if (opToDelete.getProdukties() == null || opToDelete.getProdukties().size() < 1) {

                    for (Kategorie kTmp : opToDelete.getKategories()) {
                        kTmp.getOpisyproduktows().remove(opToDelete);
                        if (kTmp.getOpisyproduktows() != null && kTmp.getOpisyproduktows().size() > 0) {
                            session.saveOrUpdate(kTmp);
                        } else {
                            session.delete(kTmp);
                        }
                    }

                    for (Obrazyproduktow obpTmp : opToDelete.getObrazyproduktows()) {
                        session.delete(obpTmp);
                    }

                    for (Uzytkownicy uTmp : opToDelete.getUzytkownicies()) {
                        uTmp.getOpisyproduktows().remove(opToDelete);
                    }

                    session.delete(opToDelete);
                    tx.commit();
                    message = "Udało się usunąć opis produktu";
                } else {
                    message = "Istnieją powiązania tego opisu produktu z produktami";
                }
            } else {//Istnieją nieusuwalne powiązania
                message = "Istnieją powiązania tego opisu produktu ze stanami";
            }
        } catch (Exception e) {
            errors = true;
            message = "Błąd usuwania opisu produktu";
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        message = sdf.format(cal.getTime()) + " " + message;
        if (errors) {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", message));
        } else {
            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", message));
        }
    }
}
