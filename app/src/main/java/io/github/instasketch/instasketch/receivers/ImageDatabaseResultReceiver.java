package io.github.instasketch.instasketch.receivers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by transfusion on 16-2-29.
 */
@SuppressLint("ParcelCreator")
public class ImageDatabaseResultReceiver extends ResultReceiver{
//    https://github.com/codepath/android_guides/wiki/Starting-Background-Services

    private Receiver receiver;

    public ImageDatabaseResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }


}
