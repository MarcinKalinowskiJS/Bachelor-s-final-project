package Pojos;






// Generated Jan 11, 2019 3:58:09 PM by Hibernate Tools 4.3.1



/**
 * ProduktySegmentyId generated by hbm2java
 */
public class ProduktySegmentyId  implements java.io.Serializable {


     private String nazwaSegmentu;
     private int idProduktu;

    public ProduktySegmentyId() {
    }

    public ProduktySegmentyId(String nazwaSegmentu, int idProduktu) {
       this.nazwaSegmentu = nazwaSegmentu;
       this.idProduktu = idProduktu;
    }
   
    public String getNazwaSegmentu() {
        return this.nazwaSegmentu;
    }
    
    public void setNazwaSegmentu(String nazwaSegmentu) {
        this.nazwaSegmentu = nazwaSegmentu;
    }
    public int getIdProduktu() {
        return this.idProduktu;
    }
    
    public void setIdProduktu(int idProduktu) {
        this.idProduktu = idProduktu;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof ProduktySegmentyId) ) return false;
		 ProduktySegmentyId castOther = ( ProduktySegmentyId ) other; 
         
		 return ( (this.getNazwaSegmentu()==castOther.getNazwaSegmentu()) || ( this.getNazwaSegmentu()!=null && castOther.getNazwaSegmentu()!=null && this.getNazwaSegmentu().equals(castOther.getNazwaSegmentu()) ) )
 && (this.getIdProduktu()==castOther.getIdProduktu());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getNazwaSegmentu() == null ? 0 : this.getNazwaSegmentu().hashCode() );
         result = 37 * result + this.getIdProduktu();
         return result;
   }   


}


