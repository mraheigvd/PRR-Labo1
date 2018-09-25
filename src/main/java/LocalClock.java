package main.java;

public class LocalClock extends Clock {
    private long ecart;
    private long delai;

    @Override
    public long getTime() {
        return super.getTime() + ecart + delai;
    }

    public void setEcart(long ecart) {
        this.ecart = ecart;
    }

    public void setDelai(long delai) {
        this.delai = delai;
    }
}
