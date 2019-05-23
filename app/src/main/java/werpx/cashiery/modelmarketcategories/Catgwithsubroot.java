package werpx.cashiery.modelmarketcategories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Catgwithsubroot {

    @SerializedName("category")
    @Expose
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}