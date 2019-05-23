package werpx.cashiery.RoomDatabase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "points")
public class pointtable {

    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "point")
    private String point;



    public pointtable(String point)
    {
        this.point=point;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
