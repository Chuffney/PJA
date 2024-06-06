package main.pokoje;

public class Dwojka extends Pokoj {
    public Dwojka(String typ, int noce) {
        super(typ, noce);
    }

    @Override
    public Rodzaj pobierzRodzaj() {
        return Rodzaj.DWOJKA;
    }
}
