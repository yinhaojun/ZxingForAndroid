/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yinjunzi.zxinglibrary.embedded.zxing.client.android;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.yinjunzi.zxinglibrary.R;

/**
 * Manages beeps and vibrations.
 */
public final class BeepManager {

    private static final String TAG = BeepManager.class.getSimpleName();

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    private final Context context;

    private boolean beepEnabled = true;
    private boolean vibrateEnabled = false;

    private SoundPool soundPool;
    private int beepSound;

    public BeepManager(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // We do not keep a reference to the Activity itself, to prevent leaks
        this.context = activity.getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        }

        beepSound = soundPool.load(context, R.raw.zxing_beep, 1);
    }

    public boolean isBeepEnabled() {
        return beepEnabled;
    }

    /**
     * Call updatePrefs() after setting this.
     *
     * If the device is in silent mode, it will not beep.
     *
     * @param beepEnabled true to enable beep
     */
    public void setBeepEnabled(boolean beepEnabled) {
        this.beepEnabled = beepEnabled;
    }

    public boolean isVibrateEnabled() {
        return vibrateEnabled;
    }

    /**
     * Call updatePrefs() after setting this.
     *
     * @param vibrateEnabled true to enable vibrate
     */
    public void setVibrateEnabled(boolean vibrateEnabled) {
        this.vibrateEnabled = vibrateEnabled;
    }

    public synchronized void playBeepSoundAndVibrate() {
        if (beepEnabled) {
            playBeepSound();
        }
        if (vibrateEnabled) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    public void playBeepSound() {
        soundPool.play(beepSound, BEEP_VOLUME, BEEP_VOLUME, 1, 0, 1);
    }
}