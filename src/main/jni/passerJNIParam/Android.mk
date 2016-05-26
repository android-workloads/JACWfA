LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
LOCAL_MODULE := libpasserJNIParam
LOCAL_SRC_FILES := passerJNIParam.cpp
LOCAL_LDLIBS := -llog
LOCAL_CFLAGS += -fno-permissive

include $(BUILD_SHARED_LIBRARY)
