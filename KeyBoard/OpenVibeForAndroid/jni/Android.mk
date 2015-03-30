LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := OpenVibeForAndroid
LOCAL_SRC_FILES := OpenVibeForAndroid.cpp

include $(BUILD_SHARED_LIBRARY)
###
################
include $(CLEAR_VARS)
LOCAL_MODULE := FeatureAggregator
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libFeatureAggregator.so
include $(PREBUILT_SHARED_LIBRARY)
##########
include $(CLEAR_VARS)

LOCAL_C_INCLUDES := $(LOCAL_PATH)
OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
include /home/tien/OpenCV-2.4.9-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := Filter
LOCAL_SRC_FILES := Filter.cpp

LOCAL_LDLIBS +=  -llog -ldl
include $(BUILD_SHARED_LIBRARY)
################
include $(CLEAR_VARS)
LOCAL_MODULE := baseShareLibrary
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libhid.so
#LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libhidapi-hidraw.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := main

# Add your application source files here...
LOCAL_SRC_FILES := main.c
LOCAL_SHARED_LIBRARIES := baseShareLibrary
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS := -llog 

include $(BUILD_SHARED_LIBRARY)