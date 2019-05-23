package werpx.cashiery.RoomLivedata;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface CashieryDao {

    @Insert
    void insert(menuproduct table);

    @Delete
    void deleteit(menuproduct model);

    @Query("SELECT * from menuproduct ")
    LiveData<List<menuproduct>> getAllmenuproduct();
//
@Insert
void insertproduct(producttable table);

    @Delete
    void deleteproduct(producttable model);

    @Query("SELECT * from producttable ")
    LiveData<List<producttable>> getAllproducts();
    //



}