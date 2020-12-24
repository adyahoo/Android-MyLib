package id.ac.cobalogin.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import id.ac.cobalogin.DashboardActivity;
import id.ac.cobalogin.ListBookActivity;
import id.ac.cobalogin.MainActivity;
import id.ac.cobalogin.R;

public class FirebaseReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //checking if message contain data or not n will display notif by getting title n messages key from data
        if(remoteMessage.getData().size() > 0){
            showNotif(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }

        //check if message contain notification
        if(remoteMessage.getNotification() != null){
            showNotif(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    //method for custom notif design
    private RemoteViews getCustomDesign(String title, String message){
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.notifTitle, title);
        remoteViews.setTextViewText(R.id.notifMessage, message);
        remoteViews.setImageViewResource(R.id.notifIcon, R.mipmap.icon_mylib_round);
        return remoteViews;
    }

    //method for displaying notif with title n messages
    public void showNotif(String title, String message){
        //create intent to open page if we click notif
        Intent intent = new Intent(this, ListBookActivity.class);
        String channel = "my_lib_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel)
                .setSmallIcon(R.mipmap.icon_mylib_round)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        //if android os bigger or equal to jelly bean, use the custom notif
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder = builder.setContent(getCustomDesign(title, message));
        }else{
            builder = builder.setContentTitle(title).setContentText(message).setSmallIcon(R.mipmap.icon_mylib_round);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //if android version bigger or equal to oreo, display notif from channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channel, "my_lib", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());

    }
}
