package main;

import main.pokoje.Pokoj;

import static main.Platnosc.*;

public class Klient {
    private int portfel;

    private final ListaZyczen listaZyczen;
    private final Koszyk koszyk;
    private KolekcjaPokoi ostatniaTransakcja;

    public Klient(String imie, int portfel) {
        this.portfel = portfel;
        listaZyczen = new ListaZyczen(imie);
        koszyk = new Koszyk(imie);
        ostatniaTransakcja = new KolekcjaPokoi();
    }

    public void dodaj(Pokoj pokoj) {
        listaZyczen.dodaj(pokoj);
    }

    public ListaZyczen pobierzListeZyczen() {
        return listaZyczen;
    }

    public Koszyk pobierzKoszyk() {
        return koszyk;
    }

    public int pobierzPortfel() {
        return portfel;
    }

    public void przepakuj() {
        Cennik cennik = Cennik.pobierzCennik();
        koszyk.wyczysc();
        for (Pokoj pokoj : listaZyczen.clone()) {
            if (cennik.wyznaczCeneZaNoc(pokoj) < 0)
                continue;

            koszyk.dodaj(pokoj);
            listaZyczen.usun(pokoj);
        }
    }

    public void zaplac(Platnosc platnosc) {
        int pozostaleSrodki = portfel;
        Cennik cennik = Cennik.pobierzCennik();
        KolekcjaPokoi transakcja = new KolekcjaPokoi();

        if (platnosc == PRZELEW)
            pozostaleSrodki -= PROWIZJA;

        for (Pokoj pokoj : koszyk.clone()) {
            int kwota = cennik.wyznaczCene(pokoj);
            if (kwota > pozostaleSrodki)
                continue;

            pozostaleSrodki -= kwota;
            koszyk.usun(pokoj);
            transakcja.dodaj(pokoj);
        }
        if (!transakcja.czyPusto()) {  //dokonano transakcji
            portfel = pozostaleSrodki;
            ostatniaTransakcja = transakcja;
        }
    }

    public void zwroc(Pokoj.Rodzaj rodzaj, String typ, int noce) {
        if (noce < 1) return;

        Cennik cennik = Cennik.pobierzCennik();

        for (Pokoj pokoj : ostatniaTransakcja) {
            if (pokoj.pobierzRodzaj() != rodzaj || !typ.equals(pokoj.pobierzTyp()))
                continue;

            noce = Math.min(noce, pokoj.pobierzNoce());

            int zaplacono = cennik.wyznaczCene(pokoj);
            pokoj.ustawNoce(pokoj.pobierzNoce() - noce);    //ile nocy pozostanie po zwrocie
            int nowaCena = cennik.wyznaczCene(pokoj);

            portfel += zaplacono - nowaCena;
            if (pokoj.pobierzNoce() == 0)
                ostatniaTransakcja.usun(pokoj);

            Pokoj zwroconyPokoj = pokoj.clone();
            zwroconyPokoj.ustawNoce(noce);
            koszyk.dodaj(zwroconyPokoj);
            return;
        }
    }
}
