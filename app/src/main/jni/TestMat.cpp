#include <jni.h>
#include "include/TestMat.h"
#include <opencv2/opencv.hpp>

JNIEXPORT void JNICALL
Java_io_github_instasketch_instasketch_fragments_DatabaseFragment_getMat(JNIEnv *env,
jobject instance,
        jlong emptyMatAddr) {

//    dereference the pointer passed in
    cv::Mat& m = *(cv::Mat*) emptyMatAddr;
    cv::Mat x(4, 5, CV_8UC1, cv::Scalar(23) );
    m = x;

}