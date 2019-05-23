package werpx.cashiery;

import java.util.List;



public class list {

    private List<werpx.cashiery.RoomDatabase.mytable> listdetails;

    public list(List<werpx.cashiery.RoomDatabase.mytable> countryLangs) {
        this.listdetails = countryLangs;
    }

    public List<werpx.cashiery.RoomDatabase.mytable> getListdetails() {
        return listdetails;
    }

    public void setListdetails(List<werpx.cashiery.RoomDatabase.mytable> listdetails) {
        this.listdetails = listdetails;
    }



}