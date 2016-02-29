#include <jni.h>
#include "include/HelloJNI.h"

JNIEXPORT jstring JNICALL Java_io_github_instasketch_instasketch_fragments_DatabaseFragment_getMessage
        (JNIEnv *env, jobject thisObj) {
    return (*env)->NewStringUTF(env, "Hello from native code!");
}