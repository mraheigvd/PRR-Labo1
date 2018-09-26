package main.java;

public class LocalClock extends Clock {
    private int ecart;
    private int delai;

    @Override
    public int getTime() {
        return super.getTime() + ecart + delai;
    }

    public int getUncorrectedTime() {
        return super.getTime();
    }

    public void setEcart(int ecart) {
        this.ecart = ecart;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }
}
