package main.pokoje;

import main.Cennik;

public abstract class Pokoj implements Cloneable {
    private final String typ;
    private int noce;

    protected Pokoj(String typ, int noce) {
        this.typ = typ;
        this.noce = noce;
    }

    public int pobierzNoce() {
        return noce;
    }

    public void ustawNoce(int noce) {
        this.noce = noce;
    }

    public String pobierzTyp() {
        return typ;
    }

    public abstract Rodzaj pobierzRodzaj();

    @Override
    public String toString() {
        return pobierzRodzaj().toString().toLowerCase() + ", typ: " + typ + ", ile: " + noce + " noce, cena " + Cennik.pobierzCennik().wyznaczCeneZaNoc(this);
    }

    @Override
    public Pokoj clone() {
        try {
            return (Pokoj) super.clone();
        } catch (CloneNotSupportedException ignored) {
            throw new IllegalStateException();
        }
    }

    public enum Rodzaj {
        JEDYNKA,
        DWOJKA,
        TROJKA,
        RODZINA
    }
}
