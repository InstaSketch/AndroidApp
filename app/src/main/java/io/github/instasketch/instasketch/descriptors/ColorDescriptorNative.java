package io.github.instasketch.instasketch.descriptors;

import org.opencv.core.Mat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    public byte[] serializeFloatArr(float[] desc) throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bas);
        for (float f : desc) {
            ds.writeFloat(f);
        }
        return bas.toByteArray();
    }

    public float[] deserializeFloatArr(byte[] buffer) throws IOException{
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        DataInputStream dis = new DataInputStream(bis);
        float[] fArr = new float[buffer.length / 4];  // 4 bytes per float
        for (int i = 0; i < fArr.length; i++) {
            fArr[i] = dis.readFloat();
        }
        return fArr;
    }
}
