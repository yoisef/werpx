package werpx.cashiery;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class Firebasemsg extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String,String> map= remoteMessage.getData();
            String order=map.get("order");
            switch (order)
            {
                case "create":{

                    Log.e("order",map.get("details"));
                    String mresonse = map.get("details");
                    JSONObject uploadcond = null;
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String id = uploadcond.getString("id");
                        Log.e("order",map.get("details")+id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String bar = uploadcond.getString("bar");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String price = uploadcond.getString("price");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String image = uploadcond.getString("image");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                }

                case "update":{


                    String mresonse = map.get("details");
                    JSONObject uploadcond = null;
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String price = uploadcond.getString("price");
                        Log.e("order",map.get("details")+price);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String image = uploadcond.getString("image");
                        Log.e("order",map.get("details")+image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String name = uploadcond.getString("image");
                        Log.e("order",map.get("details")+name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String localname = uploadcond.getString("image");
                        Log.e("order",map.get("details")+localname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        uploadcond = new JSONObject(mresonse);
                        String localname = uploadcond.getString("barcode");
                        Log.e("order",map.get("details")+localname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                }

                case "delete":{

                    String mresonse = map.get("details");
                    JSONObject uploadcond = null;

                    try {
                        uploadcond = new JSONObject(mresonse);
                        String id = uploadcond.getString("id");
                        Log.e("order",map.get("details")+id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }



        }





       // remoteMessage.getNotification().getTitle()
       // remoteMessage.getNotification().getBody()+string

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.iconlogo)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());





    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);

    }
}

