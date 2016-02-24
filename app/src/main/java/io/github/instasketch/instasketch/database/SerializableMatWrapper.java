package io.github.instasketch.instasketch.database;

import android.util.Log;

import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by transfusion on 16-2-21.
 */
public class SerializableMatWrapper implements Serializable {
//    We can't simply store nativeObj of the Mat class because it is merely a pointer to some internal native structure.

    private Mat m;

    public SerializableMatWrapper(Mat m) {
        this.m = m;
    }

    public Mat getMat() {
        return m;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
//        note: long because either
        long nBytes = m.total() * m.elemSize();
        byte[] bytes = new byte[(int) nBytes];
        m.get(0, 0, bytes);

        out.defaultWriteObject();
        out.writeObject(bytes);
        out.writeInt(m.rows());
        out.writeInt(m.cols());
        out.writeInt(m.type());
    }

    private void readObject(ObjectInputStream aStream) throws IOException, ClassNotFoundException {
        byte[] data = (byte[]) aStream.readObject();
        int height = aStream.readInt();
        int width = aStream.readInt();
        int type = aStream.readInt();

        m = new Mat(height, width, type);
        m.put(0, 0, data);
    }

    public byte[] toByteArray(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.close();

            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();

            return buf;
        } catch(IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }

}