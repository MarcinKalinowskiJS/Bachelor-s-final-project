package beans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import beans.MagazynHibernateUtil;
import Pojos.Opisyproduktow;
import Pojos.Produkty;
import Pojos.ProduktyStany;
import Pojos.ProduktyStanyId;
import Pojos.Stany;
import Pojos.Uzytkownicy;
//import Pojos.UzytkownicyOpisyproduktow;
//import Pojos.UzytkownicyOpisyproduktowId;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Marcin
 */
@ManagedBean
@ViewScoped
public class GuestBean implements Serializable {

    /**
     * Creates a new instance of GuestBean
     */
    private Uzytkownicy uzytkownik;
    private Opisyproduktow opisProduktu;
    private Produkty produkt;
    private String searchString;
    private String adres;

    public GuestBean() {
        uzytkownik = new Uzytkownicy();
        opisProduktu = new Opisyproduktow();
    }

    public Produkty getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkty produkt) {
        this.produkt = produkt;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Uzytkownicy getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(Uzytkownicy uzytkownik) {
        this.uzytkownik = uzytkownik;
    }

    public Opisyproduktow getOpisProduktu() {
        return opisProduktu;
    }

    public void setOpisProduktu(Opisyproduktow opisProduktu) {
        this.opisProduktu = opisProduktu;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public static List<Opisyproduktow> getOpisyProduktowWithProdukty(Integer idOpisu, Integer idProduktu) {

        System.out.println("getOpisyProduktowWithProdukty idOpisu=" + idOpisu);
        //Pobranie ogólnych
        Session session = null;
        List<Opisyproduktow> finalOpisyProduktow = new ArrayList<>();
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            List<Opisyproduktow> opisyProduktow = null;
            if (idOpisu != -1 && idOpisu != 0 && idOpisu != null) {
                opisyProduktow = (ArrayList<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty(session, "Opisyproduktow", "idOpisu", idOpisu);
            } else {
                opisyProduktow = (ArrayList<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty(session, "Opisyproduktow", "1", 1);
            }
            System.out.println("Op.Size" + opisyProduktow.size());

            Map mop1 = new HashMap();
            Map<Integer, Boolean> mop1Used = new HashMap();
            for (Opisyproduktow op : opisyProduktow) {
                mop1.put(op.getIdOpisu(), op);
                mop1Used.put(op.getIdOpisu(), false);
            }
            //Pobranie szczegółów jednego lub wszystkich
            List<Produkty> produkty = null;
            if (idProduktu != null && idProduktu != -1 && idProduktu != 0) {
                produkty = (ArrayList<Produkty>) (Object) WorkerBean.findObjectsByProperty(session, "Produkty", "idProduktu", idProduktu);
            } else {
                produkty = (ArrayList<Produkty>) (Object) WorkerBean.findObjectsByProperty(session, "Produkty", "1", 1);
            }

            //Merge opisyProduktow z produkty
            Opisyproduktow opTmp;
            for (int i = 0; i < produkty.size(); i++) {
                if ((opTmp = produkty.get(i).getOpisyproduktow()) == null) {
                    opTmp = null;
                    System.out.println("GuestBean getOpisyProduktowWithProdukty Nieścisłość w bazie danych (idOpisu=null)"
                            + "dla idProduktu=" + produkty.get(i).getIdProduktu());
                }
                if (opTmp != null) {
                    opTmp = opTmp.copy();
                    Opisyproduktow.mergeWithProdukt(opTmp, produkty.get(i));
                    finalOpisyProduktow.add(opTmp);
                    mop1Used.put(opTmp.getIdOpisu(), true);
                } else {
                    System.out.println("GuestBean getOpisyProduktowWithProdukty Nieścisłość w bazie danych "
                            + "dla idProduktu=" + produkty.get(i).getIdProduktu());
                }

            }

            //Dodawanie ogólnych jeśli produkt szczegółowy dla ogólnego nie istnieje
            for (Map.Entry<Integer, Boolean> entryUsed
                    : mop1Used.entrySet()) {
                if (entryUsed.getValue() == false) {
                    finalOpisyProduktow.add((Opisyproduktow) mop1.get(entryUsed.getKey()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error in GuestBean getOpisyProduktowWithProdukty()");
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return finalOpisyProduktow;
    }

    public List<String> getObrazyProduktow(Integer width, Integer height, Integer idOpisu) {
        Session session = null;
        Transaction tx = null;
        List<Object[]> ql = new ArrayList<>();

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Query query = session.createQuery("select o.obrazek, o.id.idOpisu, "
                    + "o.id.idObrazka from Obrazyproduktow o "
                    + "WHERE o.id.idOpisu = :idOpisu");
            query.setParameter("idOpisu", idOpisu);

            ql = query.list();

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

        List<String> obrazyProduktow = new ArrayList<>();
        byte[] b = null;

        for (int i = 0; i < ql.size(); i++) {
            try {
                //Pobieranie ścieżki dostępu do projektu
                ServletContext servletContext = (ServletContext) FacesContext.
                        getCurrentInstance().getExternalContext().getContext();

                String filename = "";
                if (width == null || height == null) {
                    filename = ql.get(i)[1] + " " + ql.get(i)[2] + ".png";
                } else {
                    filename = ql.get(i)[1] + " " + ql.get(i)[2] + " " + width
                            + " " + height + ".png";
                }

                obrazyProduktow.add("/images/" + filename);

                //Sprawdź co: minut * sekund * milisekund
                int checkMinutes = 1 * 60 * 1000;

                Path path = Paths.get(servletContext.getRealPath("/") + "/images/"
                        + filename);
                BasicFileAttributes atts = null;

                //Wywoływanie zapisywania co czas checkMinutes
                if (!path.toFile().isFile() || (atts = Files.readAttributes(path,
                        BasicFileAttributes.class
                )) != null && atts.lastModifiedTime().toMillis()
                        + checkMinutes < System.currentTimeMillis()) {
                    b = (byte[]) ql.get(i)[0];

                    File file = new File(servletContext.getRealPath("/") + "/images/"
                            + filename);

                    //Skalowanie obrazu produktu przed zapisem i wczytaniem przez serwer
                    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(b));
                    if (height == null || width == null) {
                        bi = scaleBufferedImage(bi, 1, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    } else {
                        bi = scaleBufferedImage(bi, getScale(bi.getWidth(), bi.getHeight(),
                                width, height), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    }

                    try {
                        //Zapis obrazu do pliku
                        ImageIO.write(bi, "jpeg", file);
                    } catch (Exception e) {
                        System.out.println("Błąd zapisu tablicy bajtów do obrazu");
                    }
                }
            } catch (Exception e) {
                System.out.println("Błąd pobierania danych obrazów z bazy");
                e.printStackTrace();
            }
        }
        return obrazyProduktow;
    }

    public double getScale(int width, int height, int maxWidth, int maxHeight) {
        double widthScale = (double) maxWidth / width;
        double heightScale = (double) maxHeight / height;
        if (widthScale > heightScale) {
            return heightScale;
        } else {
            return widthScale;
        }
    }

    private static BufferedImage scaleBufferedImage(final BufferedImage firstBI, double scale, int type) {
        int heightSecond = (int) (firstBI.getHeight() * scale);
        int widthSecond = (int) (firstBI.getWidth() * scale);

        BufferedImage secondBI = new BufferedImage(widthSecond, heightSecond, firstBI.getType());

        AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, type);

        scaleOp.filter(firstBI, secondBI);

        return secondBI;
    }

    public void onAddUser() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (uzytkownik.getLogin().length() < 4) {
            FacesContext.getCurrentInstance().addMessage("myForm:newPassword", new FacesMessage(sdf.format(cal.getTime()) + " Login musi mieć co najmniej 1 znak"));
            return;
        }

        if (uzytkownik.getHaslo().length() < 6) {
            FacesContext.getCurrentInstance().addMessage("myForm:newPassword", new FacesMessage(sdf.format(cal.getTime()) + " Hasło musi mieć co najmniej 6 znaków"));
            return;
        }

        if (uzytkownik.getEmail().length() < 3) {
            FacesContext.getCurrentInstance().addMessage("myForm:newPassword", new FacesMessage(sdf.format(cal.getTime()) + " Email musi mieć co najmniej 3 znaki"));
            return;
        }

        Session session = MagazynHibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //Sprawdzenie czy użytkownik istnieje
            List<Uzytkownicy> uList = (List<Uzytkownicy>) (Object) WorkerBean.findObjectsByProperty("Uzytkownicy", "login", uzytkownik.getLogin());
            if (uList != null && uList.size() > 0) {
                throw new IllegalArgumentException("Uzytkownik already exists");
            }

            uzytkownik.setRodzajUzytkownika("user");
            session.save(uzytkownik);
            session.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage("myForm:newPassword", new FacesMessage(sdf.format(cal.getTime()) + " Dodano użytkownika"));
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println("Login zajęty");
            FacesContext.getCurrentInstance().addMessage("myForm:newPassword", new FacesMessage(sdf.format(cal.getTime()) + " Login zajęty"));
        } finally {
            session.close();
        }

    }

    public void getOpisyProduktowInGuestBean(Integer idOpisu, Integer idProduktu) {

        int errors = 0;
        if ((idOpisu == null || idOpisu == -1 || idOpisu == 0)) {
            errors += 1;
        } else {
            if (idProduktu != null && idProduktu != -1 && idProduktu != 0) {
                List<Opisyproduktow> opList = (List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu);
                List<Produkty> pList = (List<Produkty>) (Object) WorkerBean.findObjectsByProperty("Produkty", "idProduktu", idProduktu);
                if ((opList != null && pList != null) && (opList.size() > 0 && pList.size() > 0)) {
                    opisProduktu = Opisyproduktow.mergeWithProdukt(opList.get(0), pList.get(0));
                } else {
                    errors += 2;
                }
            } else {
                opisProduktu = ((List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu)).get(0);
            }
        }
    }

    public List<Opisyproduktow> getOpisyProduktowWithProduktyForSearchString(String searchString) {
        List<String> searchKeys = Arrays.asList("nazwa", "opis");//Lista z nazwami kluczy w bazie
        List<String> properties = new ArrayList<>();//Lista z nazwami parametrów stworzona z listy kluczy
        List<String> searchWords = Arrays.asList(searchString.split(" "));//Lista ze słowami po których szukać
        List<Opisyproduktow> allOpList = new ArrayList<>();
        Map<Integer, Opisyproduktow> mop = new HashMap();
        Map<Integer, Boolean> mPUsed = new HashMap();
        Map<Integer, Boolean> mopUsed = new HashMap();
        List<Object> values = new ArrayList<>();
        //Tworzenie wyszukiwania w Opisyproduktow dla nazwy i opisu
        for (String s1 : searchWords) {//Po każdym słowie
            for (int i = 0; i < searchKeys.size(); i++) {
                properties.add(searchKeys.get(i));
                values.add(s1);
            }
        }
        List<Opisyproduktow> opSearch = (List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperties("Opisyproduktow", properties, values, "OR", "LIKE");
        for (Opisyproduktow op : opSearch) {
            mop.put(op.getIdOpisu(), op);
            mopUsed.put(op.getIdOpisu(), false);
        }

        searchKeys = Arrays.asList("opis");
        properties = new ArrayList<>();
        values = new ArrayList<>();
        for (String s1 : searchWords) {
            for (int i = 0; i < searchKeys.size(); i++) {
                properties.add(searchKeys.get(i));
                values.add(s1);
            }
        }
        List<Produkty> pSearch = (List<Produkty>) (Object) WorkerBean.findObjectsByProperties("Produkty", properties, values, "OR", "LIKE");
        for (Produkty p : pSearch) {
            mopUsed.put(p.getOpisyproduktow().getIdOpisu(), false);
            mPUsed.put(p.getIdProduktu(), false);
        }

        //Tworzenie list do wyszukiwania opisu produktów z produktów
        properties = new ArrayList<>();
        values = new ArrayList<>();
        for (Produkty p : pSearch) {
            properties.add("idOpisu");
            values.add(p.getOpisyproduktow().getIdOpisu());
            System.out.println("");
        }

        List<Produkty> pList = null;
        if (properties.size() > 0) {
            pList = (List<Produkty>) (Object) WorkerBean.
                    findObjectsByProperties("Produkty", properties, values, "OR", "=");
        }
        if (pList != null) {
            Integer iTmp = null;
            for (Produkty pTmp : pList) {
                iTmp = pTmp.getOpisyproduktow().getIdOpisu();
                if (mPUsed.get(pTmp.getIdProduktu()) == false) {
                    System.out.println("Dodaje");
                    allOpList.add(((Opisyproduktow) mop.get(iTmp)).copy());
                    mPUsed.put(pTmp.getIdProduktu(), true);
                }
            }
        }

        //Dodawanie produktów z wyszukiwania i łączenie z opisami     
        if (properties.size() > 0) {
            Map mopForProdukty = new HashMap();

            List<Opisyproduktow> opListSearch = (List<Opisyproduktow>) (Object) WorkerBean.
                    findObjectsByProperties("Opisyproduktow", properties, values, "OR", "=");
            for (Opisyproduktow op : opListSearch) {
                mopForProdukty.put(op.getIdOpisu(), op);
            }

            Opisyproduktow opTmp = null;
            for (Produkty p : pSearch) {
                opTmp = ((Opisyproduktow) mopForProdukty.get(p.getOpisyproduktow().getIdOpisu())).copy();
                allOpList.add(Opisyproduktow.mergeWithProdukt(opTmp, p));
                mopUsed.put(opTmp.getIdOpisu(), true);
                mPUsed.put(opTmp.getIdProduktu(), true);
            }
        }

        //Dodawanie pozostałych produktow, gdy znaleziono pewne idOpisu
        properties = new ArrayList<>();
        values = new ArrayList<>();
        for (Opisyproduktow opTmp : opSearch) {
            if (mop.get(opTmp.getIdOpisu()) == null || mopUsed.get(opTmp.getIdOpisu()) == null
                    || mopUsed.get(opTmp.getIdOpisu()) == false) {
                properties.add("opisyproduktow.idOpisu");
                values.add(opTmp.getIdOpisu());
            }
        }

        List<Produkty> pList2 = (List<Produkty>) (Object) WorkerBean.
                findObjectsByProperties("Produkty", properties, values, "OR", "=");
        if (pList2 != null) {
            for (Produkty p : pList2) {
                allOpList.add(Opisyproduktow.mergeWithProdukt(((Opisyproduktow) mop.get(p.getOpisyproduktow().getIdOpisu())).copy(), p));
                mopUsed.put(p.getOpisyproduktow().getIdOpisu(), true);
            }
        }

        //Dodawanie pozostałych opisów
        for (Map.Entry<Integer, Boolean> entryUsed : mopUsed.entrySet()) {
            if (entryUsed.getValue() == false) {
                allOpList.add((Opisyproduktow) mop.get(entryUsed.getKey()));
            }
        }

        return allOpList;
    }

    public String redirectToZnalezioneProdukty(String searchString) {
        System.out.println("Redirect:" + searchString);
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return "/magazyn/guest/znalezione_produkty.xhtml?faces-redirect=true&searchString=" + searchString;
    }

    public String addOpisProduktu(Opisyproduktow opisProduktu) {//Tutaj info o dodaniu
        Session session = MagazynHibernateUtil.getSessionFactory().openSession();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime());
        try {
            session.beginTransaction();
            session.save(opisProduktu);
            session.getTransaction().commit();
            session.close();
            FacesContext.getCurrentInstance().addMessage("DodajOgolnyOpisForm:WorkerPanel",
                    new FacesMessage(time + "<br/>Dodano ogólny produkt: " + opisProduktu.getNazwa()));
        } catch (Exception e) {
            session.getTransaction().rollback();
            if (session != null) {
                session.close();
            }
            FacesContext.getCurrentInstance().addMessage("DodajOgolnyOpisForm:WorkerPanel",
                    new FacesMessage(time + "<br/>Błąd dodawania: " + opisProduktu.getNazwa()));
        }
        return "Poprawnie dodano opis produktu";
        //FacesContext.getCurrentInstance().addMessage("DodajOgolnyOpisForm", new FacesMessage("Dodano opis produktu"));
    }

    public void deleteImage(String imageId, int idProduktu) {//Id produktu potrzebne do odświeżenia strony
        System.out.println("Image id to delete:" + imageId);
        int whichNotParsed = 0;
        String[] ids = null;
        try {
            ids = imageId.split(" ");
            if (ids.length != 2) {
                throw new IndexOutOfBoundsException();
            }
        } catch (Exception e) {
            whichNotParsed += 1;
        }
        int idObrazka = -1, idOpisu = -1;
        System.out.println("IdProduktu in deleteImage:" + idProduktu);
        if (whichNotParsed == 0) {
            try {
                idOpisu = Integer.parseInt(ids[0]);
                if (WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu).size() <= 0) {
                    throw new IndexOutOfBoundsException();
                }
            } catch (Exception e) {
                whichNotParsed += 2;
            }
        }

        if (whichNotParsed == 0) {
            try {
                idObrazka = Integer.parseInt(ids[1]);
                if (WorkerBean.findObjectsByProperty("Obrazyproduktow op", "op.id.idObrazka", idObrazka).size() <= 0) {
                    throw new IndexOutOfBoundsException();
                }
            } catch (Exception e) {
                whichNotParsed += 4;
            }
        }

        if (whichNotParsed > 0) {//Wyświetlanie błędów
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String time = sdf.format(cal.getTime());

            String errorsString = "";
            if (whichNotParsed >= 4) {
                errorsString += " Błędne id Obrazka";
                whichNotParsed -= 4;
            }
            if (whichNotParsed >= 2) {
                errorsString += " Błędne id Opisu";
                whichNotParsed -= 2;
            }
            if (whichNotParsed >= 1) {
                errorsString += " Błędny format danych";
                whichNotParsed -= 1;
            }

            FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                    new FacesMessage(time + " " + errorsString));
        } else {//Usuwanie obrazka width przypadku braku błędów
            Session session = MagazynHibernateUtil.getSessionFactory().openSession();
            String q = "";
            Transaction tx = session.beginTransaction();
            Query qMain = session.createQuery(q = "delete from Obrazyproduktow op where "
                    + "op.id.idOpisu = :idOpisu AND op.id.idObrazka = :idObrazka");
            qMain.setParameter("idOpisu", idOpisu);
            qMain.setParameter("idObrazka", idObrazka);
            int result = qMain.executeUpdate();
            tx.commit();
            session.flush();
            session.close();
            System.out.println("Delete result:" + result);
            System.out.println("query:" + q);
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String time = sdf.format(cal.getTime());
                FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                        new FacesMessage(time + " Usunięto obrazów: " + result));

                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);

                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()
                        + "?idOpisu=" + idOpisu + "&idProduktu=" + idProduktu);

            } catch (Exception e) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String time = sdf.format(cal.getTime());
                FacesContext.getCurrentInstance().addMessage("szczegolyForm:WorkerPanel",
                        new FacesMessage(time + " Błąd odświeżania strony"));
                System.out.println("Cannot Refresh the page in delete image");
            }
        }

    }

    public void uzytkownikDoKoszyka(Integer idOpisu, Integer idProduktu, String iloscDoKupienia, Integer userId) {
        System.out.println("GuestBean uzytkownikDoKoszyka" + idOpisu + " " + idProduktu + " " + iloscDoKupienia + " " + userId);

        boolean errors = false;
        //Podstawowa walidacja ilosci do kupienia oraz czy na kilogramy czy sztuki
        String message = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        message += sdf.format(cal.getTime()) + " ";
        BigDecimal iloscDoKupieniaBD = null;
        try {
            iloscDoKupieniaBD = new BigDecimal(iloscDoKupienia.replace(",", "."));
            Boolean hasDecimal = null;
            if (iloscDoKupieniaBD.signum() == 0 || iloscDoKupieniaBD.scale() <= 0
                    || iloscDoKupieniaBD.stripTrailingZeros().scale() <= 0) {
                hasDecimal = false;
            } else {
                hasDecimal = true;
            }
            //Tutaj dodać sprawdzanie czy na sztuki
            Boolean naSztuki = false;
            if (hasDecimal == true && naSztuki == true) {
                message += "Sprzedaż wyłącznie na sztuki";
                errors = true;
            }

        } catch (Exception e) {
            errors = true;
            message += "Błędna wartość ilosci do kupienia";
        }

        if (errors == false) {//Jeśli ilosc Do Kupienia jest prawidłowa
            //Sprawdzenie czy jest odpowiednia ilosc produktu i walidacja ilosci Do Kupienia
            List<Produkty> checkIloscProdukty = (List<Produkty>) (Object) WorkerBean.findObjectsByProperty("Produkty", "idProduktu", idProduktu);
            if (checkIloscProdukty.size() > 0
                    && checkIloscProdukty.get(0).getIlosc().compareTo(iloscDoKupieniaBD) >= 0) {
                Session session = null;
                Transaction tx = null;
                try {
                    session = MagazynHibernateUtil.getSessionFactory().openSession();
                    tx = session.beginTransaction();

                    //Sprawdzanie czy użytkownik ma już stan koszyka, czyli coś w koszyku
                    ArrayList<String> stanyProperties = new ArrayList<>();
                    stanyProperties.add("nazwaStanu");
                    stanyProperties.add("uzytkownicy.idUzytkownika");
                    ArrayList<Object> stanyValues = new ArrayList<>();
                    stanyValues.add("W koszyku");
                    stanyValues.add(userId);
                    List<Stany> obecneStany = (List<Stany>) (Object) WorkerBean.findObjectsByProperties("Stany", stanyProperties, stanyValues, "AND", "=");
                    Integer idStanu = null;
                    if (obecneStany == null || obecneStany.size() == 0) {//Tworzenie nowego stanu na koszyk dla uzytkownika
                        Stany stan = new Stany();
                        stan.setNazwaStanu("W koszyku");
                        List<Uzytkownicy> uzytkownicy = (List<Uzytkownicy>) (Object) WorkerBean.
                                findObjectsByProperty("Uzytkownicy", "idUzytkownika", userId);
                        if (uzytkownicy.size() > 0) {
                            Uzytkownicy obecnyUzytkownik = uzytkownicy.get(0);
                            stan.setUzytkownicy(obecnyUzytkownik);
                            stan.setDataRozpoczecia(Calendar.getInstance().getTime());
                            stan.setDataZakonczenia(Calendar.getInstance().getTime());
                            session.save(stan);
                            idStanu = stan.getIdStanu();
                        }
                    } else {
                        idStanu = obecneStany.get(0).getIdStanu();
                        obecneStany.get(0).setDataZakonczenia(Calendar.getInstance().getTime());
                    }

                    //Usuwanie kupionej ilosci z produktow
                    List<Produkty> pList = (List<Produkty>) (Object) WorkerBean.findObjectsByProperty(
                            session, "Produkty", "idProduktu", idProduktu);
                    if (pList != null && pList.size() > 0) {

                        //pList.get(0).setIlosc(pList.get(0).getIlosc().subtract(iloscDoKupieniaBD));
                        //Usuwanie ilosci produktu i zapisywanie zmienionego produktu
                        pList.get(0).setIlosc(pList.get(0).getIlosc().subtract(iloscDoKupieniaBD));
                        session.saveOrUpdate(pList.get(0));
                        //Dodawanie produktow do listy danego stanu / Dodawanie ilosci jesli produkt juz jest width koszyku
                        ArrayList<String> pSProperties = new ArrayList<>();
                        pSProperties.add("id.idProduktu");
                        pSProperties.add("id.idStanu");
                        ArrayList<Object> pSValues = new ArrayList<>();
                        pSValues.add(idProduktu);
                        pSValues.add(idStanu);
                        List<ProduktyStany> pSList = (List<ProduktyStany>) (Object) WorkerBean.findObjectsByProperties(session, "ProduktyStany", pSProperties, pSValues, "AND", "=");
                        ProduktyStany prStany;

                        //Ustawianie sumy wczesniejszej i aktualnej ilosci produktow
                        if (pSList != null && pSList.size() > 0) {
                            System.out.println("!= NULL && pSList.size() >0");
                            prStany = (ProduktyStany) session.merge(pSList.get(0));
                            prStany.setIlosc(prStany.getIlosc().add(iloscDoKupieniaBD));
                        } else {//Ustawianie tylko aktualnej ilosci produktow
                            prStany = new ProduktyStany();
                            prStany.setIlosc(iloscDoKupieniaBD);
                            prStany.setId(new ProduktyStanyId(idProduktu, idStanu));
                        }
                        session.saveOrUpdate(prStany);
                        tx.commit();

                        message += "Poprawnie dodano produkty do koszyka";
                    } else {
                        //Brak produktu
                        message += "Produkt usunieto";
                    }
                } catch (Exception e) {
                    message += "Błąd dodawania produktu do koszyka";
                    e.printStackTrace();
                    if (tx != null) {
                        tx.rollback();
                    }
                } finally {
                    if (session != null) {
                        session.close();
                    }
                }
            } else {
                if (checkIloscProdukty.size() <= 0) {
                    message += "Błędne id produktu";
                } else if (checkIloscProdukty.get(0).getIlosc().compareTo(iloscDoKupieniaBD) >= 0) {
                    message += "Niewystarczająca ilość produktu";
                }
            }
        }

        FacesContext.getCurrentInstance().addMessage("GuestForm:GuestPanel", new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Info", message));
    }

    //Subskrybowanie użytkownika na informacje o produkcie
    public void uzytkownikSubskrybuj(Integer idOpisu, Integer userId) {
        Session session = null;
        Transaction tx = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String message = sdf.format(cal.getTime()) + " ";
        boolean error = false;

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Opisyproduktow op = ((List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty(session, "Opisyproduktow", "idOpisu", idOpisu)).get(0);
            Uzytkownicy u = ((List<Uzytkownicy>) (Object) WorkerBean.findObjectsByProperty(session, "Uzytkownicy", "idUzytkownika", userId)).get(0);
            op.getUzytkownicies().add(u);
            u.getOpisyproduktows().add(op);
            session.saveOrUpdate(op);
            session.saveOrUpdate(u);
            tx.commit();
        } catch (Exception e) {
            error = true;
            message += "Błąd dodawania subskrybcji";
            System.out.println("GuestBean uzytkownikSubskrybuj() error!!!");
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        if (error == false) {
            message += "Zasubskrybowano produkt";
            FacesContext.getCurrentInstance().addMessage("GuestForm:GuestPanel", new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Info", message));
        } else {
            FacesContext.getCurrentInstance().addMessage("GuestForm:GuestPanel", new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Info", message));
        }

    }

    public List<Integer> getSubskrybcje(Integer idUzytkownika) {
        List<Integer> subskrybcje = new ArrayList<>();

        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            List<Uzytkownicy> uList = ((List<Uzytkownicy>) (Object) WorkerBean.
                    findObjectsByProperty(session, "Uzytkownicy", "idUzytkownika", idUzytkownika));
            Uzytkownicy u = null;
            if (uList.size() > 0) {
                u = uList.get(0);
            } else {
                System.out.println("GuestBean getSubskrybcje() Nie znaleziono użytkownika");
            }
            if (u != null) {
                Opisyproduktow op;
                for (Object o : u.getOpisyproduktows()) {
                    op = (Opisyproduktow) o;
                    System.out.println("1");
                    if (op == null) {
                        System.out.println("GuestBean getSubskrybcje(" + idUzytkownika + ") opNull");
                    }
                    subskrybcje.add(op.getIdOpisu());
                }
            }
            tx.commit();
        } catch (Exception e) {
            System.out.println("GuestBean getSubskrybcje(" + idUzytkownika + ") Error");
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return subskrybcje;
    }

    public Opisyproduktow getOpisyProduktow(Integer idOpisu) {
        System.out.println("IdOpisow w getOpisyProduktow" + idOpisu);

        if (idOpisu == null || idOpisu == 0 || idOpisu == -1) {
            return null;
        }

        return ((List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty("Opisyproduktow", "idOpisu", idOpisu)).get(0);
    }

    public List<Opisyproduktow> getOpisyProduktowForList(List<Integer> idOpisow) {
        System.out.println("IdOpisow w getOpisyProduktowForList" + idOpisow);

        if (idOpisow == null || idOpisow.size() == 0) {
            return null;
        }

        List<String> properties = new ArrayList<>();

        for (Integer i : idOpisow) {
            properties.add("idOpisu");
        }
        return (List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperties("Opisyproduktow", properties, (List<Object>) (Object) idOpisow, "OR", "=");
    }

    public List<Opisyproduktow> getKoszykOpisyproduktow(Integer userId) {
        System.out.println("GuestBean getKoszykOpisyproduktow");
        //Pobranie id produktów width koszyku ze stanów i z produktyStany

        List<Stany> stanyKoszyka = (List<Stany>) (Object) WorkerBean.findObjectsByProperties("Stany",
                new ArrayList<String>(Arrays.asList("nazwaStanu", "uzytkownicy.idUzytkownika")), new ArrayList<Object>(Arrays.asList("W koszyku", userId)), "AND", "=");
        if (stanyKoszyka == null || stanyKoszyka.size() < 1) {
            System.out.println("StanyKoszykaNuul lub <1");
            return null;
        }
        System.out.println("-1");
        //Pobranie idProduktów w koszyku razem z ilością
        List<ProduktyStany> psList = (ArrayList<ProduktyStany>) (Object) WorkerBean.findObjectsByProperty("ProduktyStany", "id.idStanu", stanyKoszyka.get(0).getIdStanu());
        System.out.println("0");

        //Pobranie produktow
        List<String> propertiesForProdukty = new ArrayList<>();
        List<Object> valuesForProdukty = new ArrayList<>();
        Map produktyIloscMap = new HashMap();
        if (psList == null || psList.size() < 1) {
            return null;
        }
        for (ProduktyStany ps : psList) {
            propertiesForProdukty.add("id.idProduktu");
            valuesForProdukty.add(ps.getId().getIdProduktu());
            produktyIloscMap.put(ps.getId().getIdProduktu(), ps.getIlosc());
        }
        if (psList == null || psList.size() < 1) {
            return null;
        }
        List<Produkty> produktyWKoszyku = (ArrayList<Produkty>) (Object) WorkerBean.findObjectsByProperties("Produkty", propertiesForProdukty, valuesForProdukty, "OR", "=");

        //Pobranie opisów dla produktów do mapy
        List<String> propertiesForOpisy = new ArrayList<>();
        List<Object> valuesForOpisy = new ArrayList<>();
        if (produktyWKoszyku == null || produktyWKoszyku.size() < 1) {
            return null;
        }
        for (Produkty p : produktyWKoszyku) {
            propertiesForOpisy.add("idOpisu");
            valuesForOpisy.add(p.getOpisyproduktow().getIdOpisu());
        }
        Map mop = new HashMap();
        for (Opisyproduktow op : (ArrayList<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperties(
                "Opisyproduktow", propertiesForOpisy, valuesForOpisy, "OR", "=")) {
            mop.put(op.getIdOpisu(), op);
        }

        //Połączenie produktów z opisami
        List<Opisyproduktow> finalOpisyProduktowList = new ArrayList<>();
        for (Produkty p : produktyWKoszyku) {
            p.setIlosc((BigDecimal) produktyIloscMap.get(p.getIdProduktu()));
            finalOpisyProduktowList.add(Opisyproduktow.mergeWithProdukt(
                    (Opisyproduktow) mop.get(p.getOpisyproduktow().getIdOpisu()), p));
        }

        return finalOpisyProduktowList;
    }

    //Pobieranie calkowitego kosztu width koszyku
    public BigDecimal getKosztCalkowity(List<Opisyproduktow> opList) {
        if (opList == null || opList.size() < 1) {
            return null;
        }
        BigDecimal kosztCalkowity = new BigDecimal("0.0");
        for (Opisyproduktow op : opList) {
            kosztCalkowity = kosztCalkowity.add(op.getIlosc().multiply(op.getCena()));
        }

        return kosztCalkowity;
    }

    //Usuwanie z substrybcji
    public void deleteSubskrybcja(Integer idUzytkownika, Integer idOpisu) {
        System.out.println("GuestBean DeleteSubskrybcja idOpisu:" + idOpisu + " from user: " + idUzytkownika);
        //Usuwać z użytkownika
        Session session = null;
        Transaction tx = null;

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Uzytkownicy u = ((List<Uzytkownicy>) (Object) WorkerBean.findObjectsByProperty(session, "Uzytkownicy", "idUzytkownika", idUzytkownika)).get(0);
            Opisyproduktow op = ((List<Opisyproduktow>) (Object) WorkerBean.findObjectsByProperty(session, "Opisyproduktow", "idOpisu", idOpisu)).get(0);

            //Opisyproduktow opLast = null;
            Opisyproduktow opTmp;
            for (Object o : u.getOpisyproduktows()) {
                opTmp = (Opisyproduktow) o;
                if (opTmp.getIdOpisu() == idOpisu) {
                    System.out.println("!!!Deleting Uzytkownik" + idOpisu + " " + idUzytkownika);
                    u.getOpisyproduktows().remove(opTmp);
                    break;
                }
            }

            Uzytkownicy uTmp = null;
            for (Object o : op.getUzytkownicies()) {
                uTmp = (Uzytkownicy) o;
                if (uTmp.getIdUzytkownika() == idUzytkownika) {
                    System.out.println("!!!Deleting Opis" + idOpisu + " " + idUzytkownika);
                    //uLast = uTmp;
                    op.getUzytkownicies().remove(uTmp);
                    break;
                }
            }
            session.saveOrUpdate(u);
            session.saveOrUpdate(op);
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

    public void deleteFromKoszyk(Integer userId, Integer idProduktu) {
        System.out.println("GuestBean DeleteFromKoszyk idProduktu:" + idProduktu + " from user: " + userId);
        Session session = null;
        Transaction tx = null;

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            List<Stany> sList = (List<Stany>) (Object) WorkerBean.findObjectsByProperties(session, "Stany", Arrays.asList("uzytkownicy.idUzytkownika", "nazwaStanu"), Arrays.asList(userId, "W koszyku"), "AND", "=");
            List<Produkty> pList = (List<Produkty>) (Object) WorkerBean.findObjectsByProperty(session, "Produkty", "idProduktu", idProduktu);
            if (sList != null && sList.size() > 0
                    && pList != null && pList.size() > 0) {
                List<ProduktyStany> psList = (List<ProduktyStany>) (Object) WorkerBean.findObjectsByProperties(session, "ProduktyStany", Arrays.asList("id.idProduktu", "id.idStanu"), Arrays.asList(pList.get(0).getIdProduktu(), sList.get(0).getIdStanu()), "AND", "=");

                //Zwracanie ilosci z koszyka
                Produkty pToHandBack = (Produkty) WorkerBean.findObjectByProperty(session, "Produkty", "idProduktu", idProduktu);
                if (pToHandBack != null) {
                    ProduktyStany psToBeHandedBack = psList.get(0);
                    pToHandBack.setIlosc(pToHandBack.getIlosc().add(
                            psToBeHandedBack.getIlosc()));
                    System.out.println(pToHandBack.getIlosc() + " ZWROT "
                            + psToBeHandedBack.getIlosc());
                    session.saveOrUpdate(pToHandBack);
                } else {
                    System.out.println(" BRAK ZWROT ");
                    if (pToHandBack == null) {
                        System.out.println("pToHandBackList == null");
                    } else {
                        System.out.println("pToHandBackList != null");
                    }
                }

                //Usuwanie powiązania produktów ze stanem
                sList.get(0).getProduktyStanies().remove(psList.get(0));
                pList.get(0).getProduktyStanies().remove(psList.get(0));

                session.saveOrUpdate(sList.get(0));
                session.saveOrUpdate(pList.get(0));

                session.delete(psList.get(0));

                if (sList.get(0).getProduktyStanies() == null || sList.get(0).getProduktyStanies().size() < 1) {
                    session.delete(sList.get(0));
                }
            }
            //System.out.println(sList.size() + "StanyId " + sList.get(0).getIdStanu() + " " + pList.size() +  " ProduktyId " + pList.get(0).getIdProduktu());

            tx.commit();
        } catch (Exception e) {
            System.out.println("DeleteFromKoszyk błąd");
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

    public void getPay(Integer userId) {
        System.out.println("GuestBean getPay from user: " + userId);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String messageOnSite = "";
        boolean errors = false;

        Session session = null;
        Transaction tx = null;

        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Stany s = ((List<Stany>) (Object) WorkerBean.findObjectsByProperties(session, "Stany",
                    Arrays.asList("uzytkownicy.idUzytkownika", "nazwaStanu"), Arrays.asList(userId, "W koszyku"), "AND", "=")).get(0);
            //Jeśli jest brak stanu to wyrzuci błąd pobranie .get(0) linie wyżej
            if (s.getAdresy() != null) {
                //Jest adres
            } else {
                //Wyświetlanie wiadomości o wpisaniu adresu w szczegóły zamówienia
                s.setDodatkoweInformacje(adres);
            }
            String subject = "Zamówienie o id: " + s.getIdStanu() + " oczekuje na płatność";
            String message = "Id\tNazwa\tIlość\n";
            Integer actualId = 1;
            ProduktyStany psTmp = null;
            Produkty pTmp = null;
            BigDecimal wholePrice = BigDecimal.ZERO;
            for (Object psOTmp : s.getProduktyStanies()) {
                psTmp = (ProduktyStany) psOTmp;
                pTmp = psTmp.getProdukty();

                if (pTmp.getCena() != null) {
                    wholePrice = wholePrice.add(pTmp.getCena().multiply(psTmp.getIlosc()));
                } else {
                    wholePrice = wholePrice.add(pTmp.getOpisyproduktow().getCena().multiply(psTmp.getIlosc()));
                }
                message += actualId++ + " " + ((Opisyproduktow) pTmp.getOpisyproduktow()).getNazwa() + " "
                        + psTmp.getIlosc() + "\n";

            }

            message += "\nPłatność na konto: 12 3456 7891 0111 2131 4151 6171 \nTytułem:Uzytkownik "
                    + s.getUzytkownicy().getLogin() + " Stan " + s.getIdStanu()
                    + "\n\nNa kwotę: " + wholePrice.floatValue();

            s.setNazwaStanu("Oczekuje na zapłatę");

            session.saveOrUpdate(s);

            tx.commit();

            boolean errorSendingMail = SendMailSSLBean.sendMailSSL(subject, message, s.getUzytkownicy().getEmail());

            if (errorSendingMail == false) {
                messageOnSite = sdf.format(cal.getTime()) + " Wysłano e-mail o płatności";
            } else {
                messageOnSite = sdf.format(cal.getTime()) + " Błąd wysłania e-mail o płatności";
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getPay błąd");
            if (tx != null) {
                tx.rollback();
            }
            errors = true;
            messageOnSite = sdf.format(cal.getTime()) + " Błąd zatwierdzania koszyka";
        } finally {
            if (session != null) {
                session.close();
            }
        }
        if (errors == false) {
            FacesContext.getCurrentInstance().addMessage("KoszykForm:KoszykDT",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", messageOnSite));
        } else {
            FacesContext.getCurrentInstance().addMessage("KoszykForm:KoszykDT",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", messageOnSite));
        }
    }

    public void emptyShoppingCart(Integer userId) {
        System.out.println("EmptyShoppingCart");
        Session session = null;
        Transaction tx = null;
        try {
            session = MagazynHibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            List<Stany> sList = (List<Stany>) (Object) WorkerBean.findObjectsByProperties(session, "Stany", Arrays.asList("uzytkownicy.idUzytkownika", "nazwaStanu"), Arrays.asList(userId, "W koszyku"), "AND", "=");

            if (sList == null) {
                System.out.println("sList == NULL");
            } else {
                if (sList.size() < 0) {
                    System.out.println("sList.size() < 0");
                }
            }
            if (sList != null && sList.size() > 0) {
                //zwracanie ilości produktów w koszyku do każdego z produktu
                ProduktyStany psTmp = null;
                Produkty pTmp = null;
                if (sList.get(0).getProduktyStanies() != null
                        && sList.get(0).getProduktyStanies().size() > 0) {
                    for (Object psOTmp : sList.get(0).getProduktyStanies()) {
                        psTmp = (ProduktyStany) psOTmp;
                        System.out.println("Usuwanie: " + psTmp.getProdukty().getIdProduktu() + " W ilości: " + psTmp.getIlosc());
                        //pTmp = (Produkty) WorkerBean.findObjectByProperty(session, "Produkty", "idProduktu", psTmp.getProdukty().getIdProduktu());
                        pTmp = (Produkty) session.merge(psTmp.getProdukty());

                        pTmp.setIlosc(pTmp.getIlosc().add(psTmp.getIlosc()));
                        session.saveOrUpdate(pTmp);
                        session.delete(psOTmp);
                    }

                    session.delete(sList.get(0));
                } else {
                    System.out.println("Error in GuestBean.emptyShoppingCart() sList.get(0).getProduktyStanie() == NULL || .size() < 1");
                }

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
}
