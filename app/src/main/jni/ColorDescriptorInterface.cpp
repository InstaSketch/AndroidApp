#include "include/ColorDescriptorInterface.h"
#include "include/descriptors/ColorDescriptor.h"
#include <jni.h>

JNIEXPORT jfloatArray JNICALL
    Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_getColorDesc(JNIEnv *env,
                                                                                       jobject instance,
                                                                                       jlong matAddr, jint h_bins, jint s_bins, jint v_bins){
    cv::Mat& m = *(cv::Mat*) matAddr;
//    ColorDescriptor desc(8,12,5);

    std::vector<float> vec = ColorDescriptor::describe(m, h_bins, s_bins, v_bins);
    jfloatArray result;
    result = env->NewFloatArray(vec.size());
    env->SetFloatArrayRegion(result, 0, vec.size(), &vec[0]);
    return result;
}

JNIEXPORT jfloatArray JNICALL
Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_getSketchColorDesc(JNIEnv *env,
                                                                                      jobject instance,
                                                                                      jlong matAddr, jint h_bins, jint s_bins, jint v_bins, jint threshold){
    cv::Mat& m = *(cv::Mat*) matAddr;
//    ColorDescriptor desc(8,12,5);

    std::vector<float> vec = ColorDescriptor::describe_sketch(m, h_bins, s_bins, v_bins, threshold);
    jfloatArray result;
    result = env->NewFloatArray(vec.size());
    env->SetFloatArrayRegion(result, 0, vec.size(), &vec[0]);
    return result;
}

JNIEXPORT jfloat JNICALL
    Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_chiSquared(JNIEnv *env, jobject instance,
                                                                                          jfloatArray hist1, jint hist1_size,
                                                                                          jfloatArray hist2, jint hist2_size) {

    jfloat* hist1_arr = (*env).GetFloatArrayElements(hist1, 0);
    jfloat* hist2_arr = (*env).GetFloatArrayElements(hist2, 0);
    jfloat distance = ColorDescriptor::compare_chi_squared(hist1_arr, hist1_size, hist2_arr, hist2_size);
    (*env).ReleaseFloatArrayElements(hist1, hist1_arr, 0);
    (*env).ReleaseFloatArrayElements(hist2, hist2_arr, 0);
    return distance;
}

JNIEXPORT jfloat JNICALL
        Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_bhattacharyya(JNIEnv *env, jobject instance,
                                                                                               jfloatArray hist1, jint hist1_size,
                                                                                               jfloatArray hist2, jint hist2_size){
    jfloat* hist1_arr = (*env).GetFloatArrayElements(hist1, 0);
    jfloat* hist2_arr = (*env).GetFloatArrayElements(hist2, 0);
    jfloat distance = ColorDescriptor::compare_bhattacharyya(hist1_arr, hist1_size, hist2_arr, hist2_size);
    (*env).ReleaseFloatArrayElements(hist1, hist1_arr, 0);
    (*env).ReleaseFloatArrayElements(hist2, hist2_arr, 0);
    return distance;
}

JNIEXPORT jfloat JNICALL
Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_intersect(JNIEnv *env, jobject instance,
                                                                                       jfloatArray hist1, jint hist1_size,
                                                                                       jfloatArray hist2, jint hist2_size){
    jfloat* hist1_arr = (*env).GetFloatArrayElements(hist1, 0);
    jfloat* hist2_arr = (*env).GetFloatArrayElements(hist2, 0);
    jfloat distance = ColorDescriptor::compare_intersect(hist1_arr, hist1_size, hist2_arr, hist2_size);
    (*env).ReleaseFloatArrayElements(hist1, hist1_arr, 0);
    (*env).ReleaseFloatArrayElements(hist2, hist2_arr, 0);
    return distance;
}