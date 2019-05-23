package werpx.cashiery.RoomDatabase;

public class productprices {

    String id ;

    String price;

    public productprices(String productid,String productprice)
    {
        this.id=productid;
        this.price=productprice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
