package com.ucs.chove2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


// https://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android

public class ShakeDetector implements SensorEventListener {

	// Minimum shake movement threshold
	private static final int SHAKE_THRESHOLD = 800;

	// Minimum time between two shake events (in milliseconds)
	private static final int SHAKE_INTERVAL = 1000;

	private long lastShakeTime;

	private OnShakeListener shakeListener;

	public interface OnShakeListener {
		void onShake();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		this.shakeListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			float acceleration = x * x + y * y + z * z;
			if (acceleration >= SHAKE_THRESHOLD) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastShakeTime >= SHAKE_INTERVAL) {
					lastShakeTime = currentTime;
					if (shakeListener != null) {
						shakeListener.onShake();
					}
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// No implementation needed
	}
}

