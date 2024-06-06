package main.pokoje;

public class Jedynka extends Pokoj {
    public Jedynka(String typ, int noce) {
        super(typ, noce);
    }

    @Override
    public Rodzaj pobierzRodzaj() {
        return Rodzaj.JEDYNKA;
    }

}
