package main;

import main.pokoje.Pokoj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KolekcjaPokoi implements Iterable<Pokoj>, Cloneable {
    private final String klient;
    private final List<Pokoj> lista = new ArrayList<>();

    public KolekcjaPokoi(String klient) {
        this.klient = klient;
    }

    public KolekcjaPokoi() {
        this.klient = "";
    }

    public void dodaj(Pokoj pokoj) {
        lista.add(pokoj);
    }

    public void usun(Pokoj pokoj) {
        lista.remove(pokoj);
    }

    public void wyczysc() {
        lista.clear();
    }

    public boolean czyPusto() {
        return lista.isEmpty();
    }

    @Override
    public String toString() {
        if (lista.isEmpty())
            return klient + ": -- pusto";

        StringBuilder sb = new StringBuilder(klient);
        sb.append(':');

        for (Pokoj pokoj : lista) {
            sb.append('\n').append(pokoj.toString());
        }

        sb.append('\n');
        return sb.toString();
    }

    @Override
    public Iterator<Pokoj> iterator() {
        return lista.iterator();
    }

    @Override
    public KolekcjaPokoi clone() {
        KolekcjaPokoi clone = new KolekcjaPokoi(klient);
        clone.lista.addAll(lista);
        return clone;
    }
}
