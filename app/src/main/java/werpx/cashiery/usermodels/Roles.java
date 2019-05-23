package werpx.cashiery.usermodels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Roles {

    @SerializedName("retailerAdmin")
    @Expose
    private List<werpx.cashiery.usermodels.RetailerAdmin> retailerAdmin = null;
    @SerializedName("retailer")
    @Expose
    private List<Object> retailer = null;

    public List<werpx.cashiery.usermodels.RetailerAdmin> getRetailerAdmin() {
        return retailerAdmin;
    }

    public void setRetailerAdmin(List<werpx.cashiery.usermodels.RetailerAdmin> retailerAdmin) {
        this.retailerAdmin = retailerAdmin;
    }

    public List<Object> getRetailer() {
        return retailer;
    }

    public void setRetailer(List<Object> retailer) {
        this.retailer = retailer;
    }

}