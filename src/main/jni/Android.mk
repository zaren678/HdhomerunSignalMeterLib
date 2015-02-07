
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hdhomerun
LOCAL_SRC_FILES := hdhomerun_signalMeter.c
LOCAL_SRC_FILES += hdhomerun_channels.c
LOCAL_SRC_FILES += hdhomerun_channelscan.c
LOCAL_SRC_FILES += hdhomerun_control.c
LOCAL_SRC_FILES += hdhomerun_debug.c
LOCAL_SRC_FILES += hdhomerun_device.c
LOCAL_SRC_FILES += hdhomerun_device_selector.c
LOCAL_SRC_FILES += hdhomerun_discover.c
LOCAL_SRC_FILES += hdhomerun_os_posix.c
LOCAL_SRC_FILES += hdhomerun_pkt.c
LOCAL_SRC_FILES += hdhomerun_sock_posix.c
LOCAL_SRC_FILES += hdhomerun_video.c
LOCAL_SRC_FILES += logger.c

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
