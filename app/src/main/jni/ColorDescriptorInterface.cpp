#include "include/ColorDescriptorInterface.h"
#include "include/descriptors/ColorDescriptor.h"
#include <jni.h>

JNIEXPORT jfloatArray JNICALL
    Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_getColorDesc(JNIEnv *env,
                                                                                       jobject instance,
                                                                                       jlong matAddr){
    cv::Mat& m = *(cv::Mat*) matAddr;
    ColorDescriptor desc(8,12,5);

    std::vector<float> vec = desc.describe(m);
    jfloatArray result;
    result = env->NewFloatArray(vec.size());
    env->SetFloatArrayRegion(result, 0, vec.size(), &vec[0]);
    return result;
}