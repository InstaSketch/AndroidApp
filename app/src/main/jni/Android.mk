LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=STATIC

include /home/transfusion/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := myjni
LOCAL_SRC_FILES := HelloJNI.c TestMat.cpp ColorDescriptorInterface.cpp descriptors/ColorDescriptor.cpp

LOCAL_LDLIBS += -lm -llog

include $(BUILD_SHARED_LIBRARY)