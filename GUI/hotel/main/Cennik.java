package main;

import main.pokoje.Pokoj;

import java.util.HashMap;
import java.util.Map;

public class Cennik {
    record KonkretnyPokoj(Pokoj.Rodzaj rodzaj, String typ) {}
    record Cena(int noce, int cenaStandardowa, int cenaHurtowa) {}

    private static final Cennik instancja = new Cennik();

    private final Map<KonkretnyPokoj, Cena> cennik = new HashMap<>();

    private Cennik() {}

    public static Cennik pobierzCennik() {
        return instancja;
    }

    public void dodaj(Pokoj.Rodzaj rodzaj, String typ, int noce, int cenaStandardowa, int cenaHurtowa) {
        cennik.put(new KonkretnyPokoj(rodzaj, typ), new Cena(noce, cenaStandardowa, cenaHurtowa));
    }

    public void dodaj(Pokoj.Rodzaj rodzaj, String typ, int cena) {
        dodaj(rodzaj, typ, 0, cena, cena);
    }

    public int wyznaczCeneZaNoc(Pokoj pokoj) {
        Cena cena = cennik.get(new KonkretnyPokoj(pokoj.pobierzRodzaj(), pokoj.pobierzTyp()));
        if (cena == null)
            return -1;

        int zamowioneNoce = pokoj.pobierzNoce();
        return zamowioneNoce >= cena.noce ? cena.cenaHurtowa : cena.cenaStandardowa;
    }

    public int wyznaczCene(Pokoj pokoj) {
        return wyznaczCeneZaNoc(pokoj) * pokoj.pobierzNoce();
    }
}
