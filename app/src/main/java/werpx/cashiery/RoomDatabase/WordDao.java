package werpx.cashiery.RoomDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface WordDao {

    @Insert
    void insert(werpx.cashiery.RoomDatabase.mytable table);

    @Insert
    void inserthis(werpx.cashiery.RoomDatabase.historytable table);

    @Insert
    void insertpoint(pointtable table);



    @Query("DELETE FROM product")
    void deleteAll();

    @Query("DELETE FROM product WHERE ID = :id")
    abstract void deleterow(long id);

    @Query("UPDATE product SET pitemn = :value1 WHERE pbar = :bar")
    void updateproduct(long value1, long bar);

    @Insert
    void insertProductforlist(werpx.cashiery.RoomDatabase.Productltable product);

    @Query("SELECT * FROM products_table WHERE barcode = :bar")
    werpx.cashiery.RoomDatabase.Productltable findProduct(String bar);



    @Delete
    void deleteit(werpx.cashiery.RoomDatabase.mytable model);

    @Query("SELECT * from product ")
    LiveData<List<werpx.cashiery.RoomDatabase.mytable>> getAllWords();

    @Query("SELECT * from points ")
    LiveData<List<pointtable>> getAllpoints();




    @Query("SELECT * from history ")
    LiveData<List<werpx.cashiery.RoomDatabase.historytable>> getAllHis();

    @Query("DELETE FROM history")
    void deleteAllHis();

    @Query("SELECT COUNT(pid) FROM product")
    LiveData<Integer> getRowCount();


}