LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := opencv-prebuilt
LOCAL_SRC_FILES := ../pre-built/$(TARGET_ARCH_ABI)/libopencv_java3.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
OPENCV_LIB_TYPE := STATIC

include D:\project\opencv\OpenCV-3.1.0-android-sdk\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE := blurring
LOCAL_SRC_FILES := blurring.cpp
LOCAL_C_INCLUDES := D:\project\opencv\OpenCV-3.1.0-android-sdk\OpenCV-android-sdk\sdk\native\jni\include
LOCAL_SHARED_LIBRARIES := opencv-prebuilt
LOCAL_LDLIBS += -lm -llog
LOCAL_CFLAGS := -std=gnu++11

include $(BUILD_SHARED_LIBRARY)