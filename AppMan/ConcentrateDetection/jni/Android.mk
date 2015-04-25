LOCAL_PATH := $(call my-dir)

####################################################################
#Build Readfile
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

#Build Filter.
include $(CLEAR_VARS)
LOCAL_MODULE    := filter
LOCAL_SRC_FILES := filter.c
include $(BUILD_SHARED_LIBRARY)

#Build Power
include $(CLEAR_VARS)
LOCAL_MODULE    := power
LOCAL_SRC_FILES := power.c
include $(BUILD_SHARED_LIBRARY)

# Build SVM
include $(CLEAR_VARS)

LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_MODULE:= svm

LOCAL_SRC_FILES:= SVM/ba_svm.cpp \
SVM/train.cpp \
SVM/predict.cpp \
SVM/svm-train.cpp \
SVM/svm-predict.cpp \
SVM/svm.cpp
#LOCAL_SRC_FILES:=SVM/train.cpp
#LOCAL_SRC_FILES:=SVM/predict.cpp
#LOCAL_SRC_FILES:=SVM/svm-train.cpp
#LOCAL_SRC_FILES:=SVM/svm-predict.cpp
#LOCAL_SRC_FILES:=SVM/svm.cpp

LOCAL_LDLIBS:=-llog -lm

include $(BUILD_SHARED_LIBRARY)
#            build feature                                            #

#####################################################################
include $(CLEAR_VARS)

LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_MODULE:= feature

LOCAL_SRC_FILES:=feature/Feature.cpp

LOCAL_LDLIBS:=-llog -lm

include $(BUILD_SHARED_LIBRARY)
#            build write file                                            #

#####################################################################
include $(CLEAR_VARS)

LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_MODULE:= filedata

LOCAL_SRC_FILES:=filedata/filedata.cpp

LOCAL_LDLIBS:=-llog -lm

include $(BUILD_SHARED_LIBRARY)