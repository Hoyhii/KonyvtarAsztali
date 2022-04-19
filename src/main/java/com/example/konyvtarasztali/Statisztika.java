package com.example.konyvtarasztali;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Statisztika {
    static List<Konyv> konyvek;
    public static void main(String[] args) {
        try {
            Beolvas();
            if (konyvek.isEmpty()){
                throw new Exception("Nincs könyv az adatbázisban");
            }
            moreThan500();
            moreThan500Stream();
            olderThan1950();
            olderThan1950Stream();
            longestBook();
            longestBookStream();
            mostPublishedBooks();
            mostPublishedBooksStream();
            hanyszorKolcsonzott();
        } catch (SQLException e) {
            System.out.println("Hiba történt az adatbázis kapcsolat kialakításakor.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private static void hanyszorKolcsonzott() {
        Scanner sc =  new Scanner(System.in);
        System.out.println("Adjon meg egy könyv címet: ");
        String title = sc.nextLine();
        hanyszorKolcsonzott(title);
        hanyszorKolcsonzottStream(title);
    }

    private static void hanyszorKolcsonzott(String title) {
        int ind = 0;
        while(ind < konyvek.size() && !konyvek.get(ind).getTitle().equals(title)){
            ind++;
        }
        if (ind == konyvek.size()){
            System.out.println("Nincs ilyen könyv");
        }else{
            System.out.printf("A megadott könyv %d-szor lett kikölcsönözve.",konyvek.get(ind).getRental_count());
        }
    }
    private static void hanyszorKolcsonzottStream(String title) {
        Optional<Konyv> optionalKonyv = konyvek.stream().filter(konyv -> konyv.getTitle().equals(title)).findFirst();
        if (optionalKonyv.isEmpty()){
            System.out.println("Nincs ilyen könyv");
        }else{
            System.out.printf("A megadott könyv %d-szor lett kikölcsönözve.",optionalKonyv.get().getRental_count());
        }
    }

    private static void mostPublishedBooksStream() {
        String mostBooksOwned = konyvek.stream().collect(Collectors.groupingBy(Konyv::getAuthor, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
        System.out.println("A legtöbb könyvvel rendelkező szerző: " + mostBooksOwned);
    }

    private static void mostPublishedBooks() {
        Map<String, Long> szerzok = new HashMap<>();
        for (Konyv konyv: konyvek) {
            szerzok.putIfAbsent(konyv.getAuthor(), 0L);
            long ujErtek = szerzok.get(konyv.getAuthor()) + 1;
            szerzok.put(konyv.getAuthor(), ujErtek);
        }
        Map.Entry<String, Long> mostBooksOwned = null;
        for (Map.Entry<String, Long> szerzo : szerzok.entrySet()){
            if (mostBooksOwned == null || mostBooksOwned.getValue() < szerzo.getValue()) {
                mostBooksOwned = szerzo;
            }
        }
        System.out.println("A legtöbb könyvvel rendelkező szerző: " + mostBooksOwned.getKey());
    }

    private static void longestBookStream() {
        Konyv leghosszabb = konyvek.stream().max(Comparator.comparingInt(Konyv::getPage_count)).get();
        System.out.printf("A leghosszabb könyv: \n" +
                        "\tSzerző: %s\n" +
                        "\tCím: %s\n" +
                        "\tKiadás éve: %d\n" +
                        "\tOLdalszám: %d\n",leghosszabb.getAuthor(),
                leghosszabb.getTitle(),
                leghosszabb.getPublish_year(),
                leghosszabb.getPage_count());
    }

    private static void longestBook() {
        Konyv leghosszabb = konyvek.get(0);
        for (int i = 1; i < konyvek.size(); i++){
            if (konyvek.get(i).getPage_count() > leghosszabb.getPage_count()){
                leghosszabb = konyvek.get(i);
            }
        }
        System.out.printf("A leghosszabb könyv: \n" +
                "\tSzerző: %s\n" +
                "\tCím: %s\n" +
                "\tKiadás éve: %d\n" +
                "\tOLdalszám: %d\n",leghosszabb.getAuthor(),
                leghosszabb.getTitle(),
                leghosszabb.getPublish_year(),
                leghosszabb.getPage_count());
    }

    private static void olderThan1950Stream() {
        boolean yes = konyvek.stream().anyMatch(konyv -> konyv.getPublish_year() < 1950); //Mintha nem lenne jó
        System.out.printf("%s 1950-nél régebbi könyv\n", yes ? "Van" : "Nincs");

    }

    private static void olderThan1950() {
        int ind = 0;
        while(ind < konyvek.size() && konyvek.get(ind).getPublish_year() >= 1950){
            ind++;
        }
        boolean yes = ind < konyvek.size();
        System.out.printf("%s 1950-nél régebbi könyv\n", yes ? "Van" : "Nincs");
    }

    private static void moreThan500Stream(){
        long db = konyvek.stream().filter(konyv -> konyv.getPage_count() > 500).count();
        System.out.println("500 oldalnál hosszabb könyvek száma: " + db);
    }

    private static void moreThan500() {
        int db = 0;
        for (Konyv konyv: konyvek
             ) {
            if (konyv.getPage_count() > 500){
                db++;
            }
        }
        System.out.println("500 oldalnál hosszabb könyvek száma: " + db);
    }

    private static void Beolvas() throws SQLException {
        KonyvtarDB db = new KonyvtarDB();
        konyvek = db.getKonyvek();
    }
}
