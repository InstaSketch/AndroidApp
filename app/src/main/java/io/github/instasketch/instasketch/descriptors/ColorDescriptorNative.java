package io.github.instasketch.instasketch.descriptors;

import org.opencv.core.Mat;

/**
 * Created by transfusion on 16-2-29.
 */
public class ColorDescriptorNative {
    static {
        System.loadLibrary("myjni");
    }

    private native float[] getColorDesc(long matAddr);

    public float[] getDesc(Mat m){
        return getColorDesc(m.getNativeObjAddr());
    }
}
