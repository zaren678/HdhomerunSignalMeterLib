#include <jni.h>
extern "C" {
#include "logger.h"
}

JavaVM *gJavaVM;

jint JNI_OnLoad(JavaVM* vm, void* /*reserved*/)
{
   JNIEnv *env;
   gJavaVM = vm;
   LOGI("JNI_OnLoad called");
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)
   {
      LOGE("Failed to get the environment using GetEnv()");
      return -1;
   }
   //hClass instance caching 12bi
   //hNative function registration 13i
   
   setLoggerVM( gJavaVM);
   
   LOGI("OnLoad finished");
   
   return JNI_VERSION_1_6;
}


