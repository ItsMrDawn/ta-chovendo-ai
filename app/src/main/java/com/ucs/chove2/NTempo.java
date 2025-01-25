package com.ucs.chove2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class NTempo extends Service {
	private static final int NOTIFICATION_ID = 1;
	private static final String CHANNEL_ID = "Tempo";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Inicialização

		// Notif persistente
		startForeground(NOTIFICATION_ID, createNotification());

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private Notification createNotification() {


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
					"Tempo", NotificationManager.IMPORTANCE_DEFAULT);
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel);
		}

		// Build the notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable._03d)
				.setContentTitle("Flores da Cunha")
				.setContentText("Névoa - 13°C")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		return builder.build();
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}