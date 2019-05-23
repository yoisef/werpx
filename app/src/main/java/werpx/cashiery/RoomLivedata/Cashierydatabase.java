package werpx.cashiery.RoomLivedata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {menuproduct.class, producttable.class}, version = 1,exportSchema = false)
public abstract class Cashierydatabase extends RoomDatabase {
    private static Cashierydatabase INSTANCE;

    public static Cashierydatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), Cashierydatabase.class, "CashieryLiveData")
                            .build();
        }
        return INSTANCE;
    }

    public abstract CashieryDao casheryDao();
}
