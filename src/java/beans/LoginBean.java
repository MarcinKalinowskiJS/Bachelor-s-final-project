package beans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Pojos.Uzytkownicy;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Marcin
 */
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    /**
     * Creates a new instance of LoginBean
     */
    private String pwd;
    private String username;
    private int userId;
    private String userType;
    private boolean logged;
    private int poziomDostepu = 0;

    public LoginBean() {

        logged = false;
        userType = "guest";
        username = null;
        pwd = null;
        userId = -1;
        System.out.println("UserType in constructor: " + userType);
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getPoziomDostepu() {
        if (isAdmin()) {
            poziomDostepu = 16;
        } else if (isSupervisor()) {
            poziomDostepu = 8;
        } else if (isAccountant()) {
            poziomDostepu = 4;
        } else if (isWorker()) {
            poziomDostepu = 2;
        } else if (isUser()) {
            poziomDostepu = 1;
        } else {
            poziomDostepu = 0;
        }

        return poziomDostepu;
    }

    public static Uzytkownicy validate(String username, String pwd) {
        Uzytkownicy u = null;
        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("from Uzytkownicy u WHERE "
                    + "login=:login AND haslo=:pwd");
            query.setParameter("login", username);
            query.setParameter("pwd", pwd);
            u = (Uzytkownicy) query.uniqueResult();
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
        return u;
    }

    public boolean isLogged() {
        return logged;
    }

    public boolean isAdmin() {
        return userType.equals("admin");
    }

    public boolean isSupervisor() {
        return userType.equals("supervisor");
    }

    public boolean isAccountant() {
        return userType.equals("accountant");
    }

    public boolean isWorker() {
        return userType.equals("worker");
    }

    public boolean isUser() {
        return userType.equals("user");
    }

    public String login() throws IOException {
        Uzytkownicy u = validate(username, pwd);
        
        if (u != null) {
            logged = true;
            userType = u.getRodzajUzytkownika();
            userId = u.getIdUzytkownika();
            System.out.println("user_type" + userType);
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/");
            return "/";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    "myForm:username",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Błędna nazwa użytkownika lub hasło.",
                            "Wpisz poprawną nazwę użytkownika i hasło")
            );
            return null;
        }
    }

    public void logout() throws IOException {
        poziomDostepu = 0;
        logged = false;
        username = null;
        userId = -1;
        userType = "guest";
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/");
    }
}
