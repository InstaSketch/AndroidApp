#include <jni.h>
/* Header for class io_github_instasketch_instasketch_fragments_DatabaseFragment */

#ifndef _Included_io_github_instasketch_instasketch_fragments_DatabaseFragment
#define _Included_io_github_instasketch_instasketch_fragments_DatabaseFragment
#ifdef __cplusplus
extern "C" {
#endif


/*
 * Class:     io_github_instasketch_instasketch_fragments_DatabaseFragment
 * Method:    getMessage
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jfloatArray JNICALL
        Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_getColorDesc(JNIEnv *env,
                                                                                 jobject instance,
                                                                                 jlong matAddr, jint h_bins, jint s_bins, jint v_bins);

JNIEXPORT jfloat JNICALL
Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_chiSquared(JNIEnv *env, jobject instance,
                                                                                    jfloatArray hist1, jint hist1_size,
                                                                                    jfloatArray hist2, jint hist2_size);

JNIEXPORT jfloat JNICALL
        Java_io_github_instasketch_instasketch_descriptors_ColorDescriptorNative_bhattacharyya(JNIEnv *env, jobject instance,
                                                                                            jfloatArray hist1, jint hist1_size,
                                                                                            jfloatArray hist2, jint hist2_size);

#ifdef __cplusplus
}
#endif
#endif