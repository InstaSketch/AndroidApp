package io.github.instasketch.instasketch.descriptors;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by transfusion on 16-2-23.
 */

//DO NOT USE THIS CLASS IN FAVOR OF THE NATIVE CPP IMPLEMENTATION

public class ColorDescriptor {
    private int h_bins, s_bins, v_bins;

    public ColorDescriptor(int h_bins, int s_bins, int v_bins){
        this.h_bins = h_bins;
        this.s_bins = s_bins;
        this.v_bins = v_bins;
    }

    public Mat describe(Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

//        avoid resizing the image for now since we aren't as constrained as on the desktop with 10,000 images.

        Size s = image.size();
        int center_X = (int) (s.width / 2);
        int center_Y = (int) (s.height / 2);

//        List<List<Integer>> segments = new ArrayList<List<Integer>>();
        List<double[]> segments = new ArrayList<double[]>();
//        List<Integer> segment1 = new ArrayList<Integer>(4);
        double[] segment1 = new double[]{0, (double) center_X, 0, (double) center_Y};
        segments.add(segment1);

//        List<Integer> segment2 = new ArrayList<Integer>(4);
        double[] segment2 = new double[]{(double) center_X, s.width, 0, (double) center_Y};
        segments.add(segment2);

//        List<Integer> segment3 = new ArrayList<Integer>(4);
        double[] segment3 = new double[]{(double) center_X, s.width, (double) center_Y, s.height};
        segments.add(segment3);

//        List<Integer> segment4 = new ArrayList<Integer>(4);
        double[] segment4 = new double[]{0, (double) center_X, (double) center_Y, s.height};
        segments.add(segment4);

        int axesX = (int) (s.width * 0.75 / 2);
        int axesY = (int) (s.height * 0.75 / 2);

        Mat ellipMask = Mat.zeros((int) s.width, (int) s.height, CvType.CV_8UC1);

//        cv2.ellipse(ellipMask, (cX, cY), (axesX, axesY), 0, 0, 360, 255, -1)
        Core.ellipse(ellipMask, new Point(center_X, center_Y), new Size(axesX, axesY), 0, 0, 360, new Scalar(0, 0, 0), -1);

        List<Mat> features = new ArrayList<Mat>();
        Mat hist;

        for (double[] segment : segments) {
//            cornerMask = np.zeros(image.shape[:2], dtype = "uint8")
            Mat cornerMask = Mat.zeros((int) s.width, (int) s.height, CvType.CV_8UC1);

//            cv2.rectangle(cornerMask, (startX, startY), (endX, endY), 255, -1)
            Core.rectangle(cornerMask, new Point(segment[0], segment[2]), new Point(segment[1], segment[3]), new Scalar(0, 0, 0), -1);

            Core.subtract(cornerMask, ellipMask, cornerMask);

            hist = histogram(image, cornerMask);

            features.add(hist);
        }

        hist = histogram(image, ellipMask);
        features.add(hist);

        Mat finalHist = new Mat();
        Core.hconcat(features, finalHist);
        return finalHist;
    }

    public Mat histogram(Mat image, Mat mask){
        List<Mat> imageList = new ArrayList<Mat>();
        imageList.add(image);

        Mat histogram = new Mat();
        Imgproc.calcHist(imageList, new MatOfInt(0,1,2), mask, histogram, new MatOfInt(h_bins, s_bins, v_bins), new MatOfFloat(0, 180, 0, 256, 0, 256));
        Core.normalize(histogram, histogram);
        histogram.reshape(0,1);

        return histogram;

    }



}
