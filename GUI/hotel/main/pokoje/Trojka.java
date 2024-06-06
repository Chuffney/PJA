package main.pokoje;

public class Trojka extends Pokoj {
    public Trojka(String typ, int noce) {
        super(typ, noce);
    }

    @Override
    public Rodzaj pobierzRodzaj() {
        return Rodzaj.TROJKA;
    }
}
