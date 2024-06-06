package main.pokoje;

public class Rodzina extends Pokoj {
    public Rodzina(String typ, int noce) {
        super(typ, noce);
    }

    @Override
    public Rodzaj pobierzRodzaj() {
        return Rodzaj.RODZINA;
    }
}
