package main;

import main.pokoje.*;

import static main.pokoje.Pokoj.Rodzaj.*;
import static main.Platnosc.*;

public class BookingTest {

    // cena pokoi (o podanym typie) z koszyka
    static int cena(Koszyk k, String typ) {
        int suma = 0;
        Cennik cennik = Cennik.pobierzCennik();

        for (Pokoj pokoj : k) {
            if (pokoj.pobierzTyp().equals(typ))
                suma += cennik.wyznaczCene(pokoj);
        }
        return suma;
    }

    public static void main(String[] args) {    //done 24.04.2024 18:05

        // cennik
        Cennik cennik = Cennik.pobierzCennik();

        // dodawanie nowych cen do cennika
        cennik.dodaj(JEDYNKA, "standard", 4, 100, 80);    // pokój jednoosobowy typu standardowego kosztuje 100 zł/noc jeśli klient zarezerwuje mniej niż 4 noce,
        // w innym przypadku kosztuje 80 zł/noc

        cennik.dodaj(DWOJKA, "budzet", 140);        // pokój dwuosobowy typu budżetowego kosztuje 140 zł/noc niezależnie od liczby nocy

        cennik.dodaj(TROJKA, "standard", 300);        // pokój trzyosobowy typu standardowego kosztuje 300 zł/noc niezależnie od liczby nocy

        cennik.dodaj(RODZINA, "premium", 500);        // pokój rodzinny typu premium kosztuje 500 zł/noc niezależnie od liczby nocy

        // Klient ocean deklaruje kwotę 2200 zł na rezerwacje
        Klient ocean = new Klient("ocean", 2200);

        // Klient ocean dodaje do listy życzeń różne rodzaje pokoi:
        // 4 noce w pokoju jednoosobowym typu standardowego, 5 nocy w pokoju trzyosobowym typu standardowego,
        // 3 noce w pokoju dwuosobowym typu premium, 3 noce w pokoju dwuosobowym budżetowym
        ocean.dodaj(new Jedynka("standard", 4));
        ocean.dodaj(new Trojka("standard", 5));
        ocean.dodaj(new Dwojka("premium", 3));
        ocean.dodaj(new Dwojka("budzet", 2));

        // Lista życzeń klienta ocean
        ListaZyczen listaOceanu = ocean.pobierzListeZyczen();

        System.out.println("Lista życzeń klienta " + listaOceanu);

        // Przed płaceniem, klient przepakuje pokoje z listy życzeń do koszyka.
        // Możliwe, że na liście życzeń są pokoje niemające ceny w cenniku,
        // w takim przypadku nie trafiłyby do koszyka
        Koszyk koszykOceanu = ocean.pobierzKoszyk();
        ocean.przepakuj();

        // Co jest na liście życzeń klienta ocean
        System.out.println("Po przepakowaniu, lista życzeń klienta " + ocean.pobierzListeZyczen());

        // Co jest w koszyku klienta ocean
        System.out.println("Po przepakowaniu, koszyk klienta " + koszykOceanu);

        // Ile wynosi cena wszystkich pokoi typu standardowego w koszyku klienta ocean
        System.out.println("Pokoje standardowe w koszyku klienta ocean kosztowały: "
                + cena(koszykOceanu, "standard"));

        // Klient zapłaci...
        ocean.zaplac(KARTA);    // płaci kartą płatniczą, bez prowizji

        // Ile klientowi ocean zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi ocean zostało: " + ocean.pobierzPortfel() + " zł");

        // Mogło klientowi zabraknąć srodków, wtedy pokoje są odkładane,
        // w innym przypadku koszyk jest pusty po zapłaceniu
        System.out.println("Po zapłaceniu, koszyk klienta " + ocean.pobierzKoszyk());
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykOceanu);

        // Teraz przychodzi klient morze,
        // deklaruje 780 zł na rezerwacje
        Klient morze = new Klient("morze", 780);

        // Zarezerwował za dużo jak na tę kwotę
        morze.dodaj(new Jedynka("standard", 3));
        morze.dodaj(new Dwojka("budzet", 4));

        // Co klient morze ma na swojej liście życzeń
        System.out.println("Lista życzeń klienta " + morze.pobierzListeZyczen());

        // Przepakowanie z listy życzeń do koszyka,
        // może się okazać, że na liście życzeń są pokoje niemające ceny w cenniku,
        // w takim przypadku nie trafiłyby do koszyka
        Koszyk koszykMorza = morze.pobierzKoszyk();
        morze.przepakuj();

        // Co jest na liście życzeń morze
        System.out.println("Po przepakowaniu, lista życzeń klienta " + morze.pobierzListeZyczen());

        // A co jest w koszyku klienta morze
        System.out.println("Po przepakowaniu, koszyk klienta " + morze.pobierzKoszyk());

        // klient morze płaci
        morze.zaplac(PRZELEW);    // płaci przelewem, prowizja 10 zł

        // Ile klientowi morze zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi morze zostało: " + morze.pobierzPortfel() + " zł");

        // Co zostało w koszyku klienta morze (za mało pieniędzy miał)
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykMorza);

        morze.zwroc(DWOJKA, "budzet", 1);    // zwrot (do koszyka) 1 nocy pokoju dwuosobowego budżetowego z ostatniej transakcji

        // Ile klientowi morze zostało pieniędzy?
        System.out.println("Po zwrocie, klientowi morze zostało: " + morze.pobierzPortfel() + " zł");

        // Co zostało w koszyku klienta morze
        System.out.println("Po zwrocie, koszyk klienta " + koszykMorza);
    }
}
