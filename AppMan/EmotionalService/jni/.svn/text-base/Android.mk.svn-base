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
LOCAL_MODULE := filter
LOCAL_SRC_FILES := filter.c

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hfd
LOCAL_SRC_FILES := hfd.c

include $(BUILD_SHARED_LIBRARY)