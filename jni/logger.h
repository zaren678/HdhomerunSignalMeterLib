#include <android/log.h>
#include <jni.h>
#define TAG "hdhomerunSignalMeter"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , TAG,__VA_ARGS__)

//The ## in front of the __VA_ARGS__ will eat the comma if there are no arguments to be formatted, just a string
#define MY_LOGV(str,...) my_log_print (ANDROID_LOG_VERBOSE,str,##__VA_ARGS__);
#define MY_LOGD(str,...) my_log_print (ANDROID_LOG_DEBUG  ,str,##__VA_ARGS__);
#define MY_LOGI(str,...) my_log_print (ANDROID_LOG_INFO   ,str,##__VA_ARGS__);
#define MY_LOGW(str,...) my_log_print (ANDROID_LOG_WARN   ,str,##__VA_ARGS__);
#define MY_LOGE(str,...) my_log_print (ANDROID_LOG_ERROR  ,str,##__VA_ARGS__);

void setLoggerVM( JavaVM* _gJavaVM);

void my_log_print(const android_LogPriority level, const char* format, ...);
