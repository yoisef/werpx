package werpx.cashiery.RoomLivedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class Cashieryviewmodel extends AndroidViewModel {

    private final LiveData<List<menuproduct>> menuList;
    private final LiveData<List<producttable>> productList;

    private Cashierydatabase appDatabase;

    public Cashieryviewmodel(Application application) {
        super(application);

        appDatabase = Cashierydatabase.getDatabase(this.getApplication());

        menuList= appDatabase.casheryDao().getAllmenuproduct();
        productList=appDatabase.casheryDao().getAllproducts();
    }

//menu
    public void insertmenuitem(menuproduct product) {
        appDatabase.casheryDao().insert(product);
    }
    public LiveData<List<menuproduct>> getmenu() {
        return menuList;
    }

    public void deleteItemmenuitem(menuproduct menupro) {
        new deleteAsyncTask(appDatabase).execute(menupro);
    }

    private static class deleteAsyncTask extends AsyncTask<menuproduct, Void, Void> {

        private Cashierydatabase db;

        deleteAsyncTask(Cashierydatabase  appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final  menuproduct... params) {
            db.casheryDao().deleteit(params[0]);
            return null;
        }

    }
//
    //producttable

    public void insertproductitem(producttable product) {
        appDatabase.casheryDao().insertproduct(product);
    }
    public LiveData<List<producttable>> getproducts() {
        return productList;
    }

    public void deleteproductuitem(producttable menupro) {
        new deleteproductAsyncTask(appDatabase).execute(menupro);
    }

    private static class deleteproductAsyncTask extends AsyncTask<producttable, Void, Void> {

        private Cashierydatabase db;

        deleteproductAsyncTask(Cashierydatabase  appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final producttable... params) {
            db.casheryDao().deleteproduct(params[0]);
            return null;
        }

    }
}