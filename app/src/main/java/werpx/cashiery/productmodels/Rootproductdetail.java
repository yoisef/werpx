package werpx.cashiery.productmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Rootproductdetail {

    @SerializedName("product")
    @Expose
    private werpx.cashiery.productmodels.Product product;

    public werpx.cashiery.productmodels.Product getProduct() {
        return product;
    }

    public void setProduct(werpx.cashiery.productmodels.Product product) {
        this.product = product;
    }

}