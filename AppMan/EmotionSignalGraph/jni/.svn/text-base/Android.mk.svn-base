LOCAL_PATH:= $(call my-dir)

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


include $(CLEAR_VARS)
LOCAL_MODULE    := filter
LOCAL_SRC_FILES := ERP/filter.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := emotion
LOCAL_SRC_FILES := ERP/Emotion.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := hfd
LOCAL_SRC_FILES := ERP/hfd.c
include $(BUILD_SHARED_LIBRARY)

##----------SVM--------------------#
include $(CLEAR_VARS)
#LOCAL_C_INCLUDES += $(JNI_H_INCLUDE)
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_SRC_FILES := svm/samsung_svm.cpp \
    svm/train.cpp \
    svm/predict.cpp \
    svm/svm-train.cpp \
    svm/svm-predict.cpp \
    svm/svm.cpp

LOCAL_NDK_VERSION := 4
LOCAL_SDK_VERSION := 10
LOCAL_MODULE    := signal2
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)