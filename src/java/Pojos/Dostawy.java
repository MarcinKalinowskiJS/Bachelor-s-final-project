package Pojos;






// Generated Jan 11, 2019 3:58:09 PM by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Dostawy generated by hbm2java
 */
public class Dostawy  implements java.io.Serializable {


     private Integer idDostawy;
     private Stany stany;
     private String nazwaDostawcy;
     private Date czasDostawy;

    public Dostawy() {
    }

    public Dostawy(Stany stany, String nazwaDostawcy, Date czasDostawy) {
       this.stany = stany;
       this.nazwaDostawcy = nazwaDostawcy;
       this.czasDostawy = czasDostawy;
    }
   
    public Integer getIdDostawy() {
        return this.idDostawy;
    }
    
    public void setIdDostawy(Integer idDostawy) {
        this.idDostawy = idDostawy;
    }
    public Stany getStany() {
        return this.stany;
    }
    
    public void setStany(Stany stany) {
        this.stany = stany;
    }
    public String getNazwaDostawcy() {
        return this.nazwaDostawcy;
    }
    
    public void setNazwaDostawcy(String nazwaDostawcy) {
        this.nazwaDostawcy = nazwaDostawcy;
    }
    public Date getCzasDostawy() {
        return this.czasDostawy;
    }
    
    public void setCzasDostawy(Date czasDostawy) {
        this.czasDostawy = czasDostawy;
    }




}

