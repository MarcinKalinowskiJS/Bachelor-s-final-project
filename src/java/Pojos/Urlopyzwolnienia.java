package Pojos;






// Generated Jan 11, 2019 3:58:09 PM by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Urlopyzwolnienia generated by hbm2java
 */
public class Urlopyzwolnienia  implements java.io.Serializable {


     private Integer idWolnego;
     private Pracownicy pracownicy;
     private Date rozpoczecie;
     private Date zakonczenie;
     private String rodzaj;

    public Urlopyzwolnienia() {
    }

    public Urlopyzwolnienia(Pracownicy pracownicy, Date rozpoczecie, Date zakonczenie, String rodzaj) {
       this.pracownicy = pracownicy;
       this.rozpoczecie = rozpoczecie;
       this.zakonczenie = zakonczenie;
       this.rodzaj = rodzaj;
    }
   
    public Integer getIdWolnego() {
        return this.idWolnego;
    }
    
    public void setIdWolnego(Integer idWolnego) {
        this.idWolnego = idWolnego;
    }
    public Pracownicy getPracownicy() {
        return this.pracownicy;
    }
    
    public void setPracownicy(Pracownicy pracownicy) {
        this.pracownicy = pracownicy;
    }
    public Date getRozpoczecie() {
        return this.rozpoczecie;
    }
    
    public void setRozpoczecie(Date rozpoczecie) {
        this.rozpoczecie = rozpoczecie;
    }
    public Date getZakonczenie() {
        return this.zakonczenie;
    }
    
    public void setZakonczenie(Date zakonczenie) {
        this.zakonczenie = zakonczenie;
    }
    public String getRodzaj() {
        return this.rodzaj;
    }
    
    public void setRodzaj(String rodzaj) {
        this.rodzaj = rodzaj;
    }




}


