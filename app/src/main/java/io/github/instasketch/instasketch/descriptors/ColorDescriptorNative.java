package io.github.instasketch.instasketch.descriptors;

import org.opencv.core.Mat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.*;

/**
 * Created by transfusion on 16-2-29.
 */
public class ColorDescriptorNative {
    private int h_bins, s_bins, v_bins;

    static {
        System.loadLibrary("myjni");
    }

    private native float[] getColorDesc(long matAddr, int h_bins, int s_bins, int v_bins);

    public native float chiSquared(float[] hist1, int hist1_size, float[] hist2, int hist2_size);

    public ColorDescriptorNative(int h_bins, int s_bins, int v_bins){
        this.h_bins = h_bins;
        this.s_bins = s_bins;
        this.v_bins = v_bins;
    }

    public float[] getDesc(Mat m){
        return getColorDesc(m.getNativeObjAddr(), this.h_bins, this.s_bins, this.v_bins);
    }

    /*public byte[] serializeFloatArr(float[] desc) throws IOException {
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
    }*/

    public static byte[] serializeFloatArr(float floatArray[]) {
        byte byteArray[] = new byte[floatArray.length*4];

// wrap the byte array to the byte buffer
        ByteBuffer byteBuf = ByteBuffer.wrap(byteArray);

// create a view of the byte buffer as a float buffer
        FloatBuffer floatBuf = byteBuf.asFloatBuffer();

// now put the float array to the float buffer,
// it is actually stored to the byte array
        floatBuf.put (floatArray);

        return byteArray;
    }


    public static float[] deserializeFloatArr(byte byteArray[]) {
        float floatArray[] = new float[byteArray.length/4];

// wrap the source byte array to the byte buffer
        ByteBuffer byteBuf = ByteBuffer.wrap(byteArray);

// create a view of the byte buffer as a float buffer
        FloatBuffer floatBuf = byteBuf.asFloatBuffer();

// now get the data from the float buffer to the float array,
// it is actually retrieved from the byte array
        floatBuf.get (floatArray);

        return floatArray;
    }

}
