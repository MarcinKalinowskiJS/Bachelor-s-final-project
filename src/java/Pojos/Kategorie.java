package Pojos;






// Generated Jan 11, 2019 3:58:09 PM by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Kategorie generated by hbm2java
 */
public class Kategorie  implements java.io.Serializable {


     private Integer idKategorii;
     private String nazwa;
     private String opis;
     private Set opisyproduktows = new HashSet(0);

    public Kategorie() {
    }

    public Kategorie(String nazwa, String opis, Set opisyproduktows) {
       this.nazwa = nazwa;
       this.opis = opis;
       this.opisyproduktows = opisyproduktows;
    }
   
    public Integer getIdKategorii() {
        return this.idKategorii;
    }
    
    public void setIdKategorii(Integer idKategorii) {
        this.idKategorii = idKategorii;
    }
    public String getNazwa() {
        return this.nazwa;
    }
    
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
    public String getOpis() {
        return this.opis;
    }
    
    public void setOpis(String opis) {
        this.opis = opis;
    }
    public Set getOpisyproduktows() {
        return this.opisyproduktows;
    }
    
    public void setOpisyproduktows(Set opisyproduktows) {
        this.opisyproduktows = opisyproduktows;
    }




}

