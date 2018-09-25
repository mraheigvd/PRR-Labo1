package main.java;

public class LocalClock extends Clock {
    private int ecart;
    private int delai;

    public int getCorrectedTime() {
        return getTime() + ecart + delai;
    }

    public void setEcart(int ecart) {
        this.ecart = ecart;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }
}
