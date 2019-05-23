package werpx.cashiery.RoomLivedata;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Entity(tableName = "productstable")
public class producttable {


    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "localname")
    private String localname;



    @ColumnInfo(name = "pid")
    private String pid;




    @ColumnInfo(name = "isFavourit")
    private Integer isFavourit;



    @ColumnInfo(name = "price")
    private String price;

    @ColumnInfo(name = "barcode")
    private String barcode;

    @ColumnInfo(name = "description")
    private String description;



    @ColumnInfo(name = "imge")
    private String imge;

    @ColumnInfo(name = "category")
    private Integer category;




    public producttable(String name, String localname, String pid, Integer isFavourit, String price, String barcode, String description, String imge, Integer category) {

        this.name = name;
        this.localname = localname;
        this.pid = pid;
        this.isFavourit = isFavourit;
        this.price = price;
        this.barcode = barcode;
        this.description = description;
        this.imge = imge;
        this.category = category;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getIsFavourit() {
        return isFavourit;
    }

    public void setIsFavourit(Integer isFavourit) {
        this.isFavourit = isFavourit;
    }

    public String getLocalname() {
        return localname;
    }

    public void setLocalname(String localname) {
        this.localname = localname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getImge() {
        return imge;
    }

    public void setImge(String imge) {
        this.imge = imge;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }





}