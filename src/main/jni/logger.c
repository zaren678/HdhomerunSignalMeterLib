#include "hdhomerun_os.h"
#include "logger.h"

#define MAX_BUFFER 1024

static JavaVM* gJavaVM;

void setLoggerVM( JavaVM* _gJavaVM)
{
   gJavaVM = _gJavaVM;
}

void my_log_print(const android_LogPriority level, const char* format, ...)
{
   int status;
   JNIEnv *env;
   bool_t isAttached = FALSE;
   char buffer[MAX_BUFFER];
         
   va_list args;
   va_start (args, format);
   vsnprintf (buffer,MAX_BUFFER,format, args);
   va_end (args);
   
   status = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
   if(status < 0) 
   {
      LOGE("callback_handler: failed to get JNI environment, "
             "assuming native thread");
      status = (*gJavaVM)->AttachCurrentThread(gJavaVM,&env, NULL);
      if(status < 0) 
      {
         LOGE("callback_handler: failed to attach "
                 "current thread");
         return;
      }
      isAttached = TRUE;
   }
   
   jclass loggerClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/util/Timber");
   
   if(loggerClass == NULL)
   {
      return;
   }
   
   jmethodID logMethodId = NULL;
   
   switch(level)
   {
      case ANDROID_LOG_VERBOSE:
         logMethodId = (*env)->GetStaticMethodID(env,loggerClass,"v","(Ljava/lang/String;)V");
      break;
      
      case ANDROID_LOG_DEBUG:
         logMethodId = (*env)->GetStaticMethodID(env,loggerClass,"d","(Ljava/lang/String;)V");
      break;
      
      case ANDROID_LOG_INFO:
         logMethodId = (*env)->GetStaticMethodID(env,loggerClass,"i","(Ljava/lang/String;)V");
      break;
      
      case ANDROID_LOG_WARN:
         logMethodId = (*env)->GetStaticMethodID(env,loggerClass,"w","(Ljava/lang/String;)V");
      break;
      
      case ANDROID_LOG_ERROR:
         logMethodId = (*env)->GetStaticMethodID(env,loggerClass,"e","(Ljava/lang/String;)V");
      break;
      
      default:
         //don't do anything here, this is an unhandled log level and null with cause just a return
      break;
   }
   
   if(logMethodId == NULL)
   {
      return;
   }
     
   jstring jstr =  (*env)->NewStringUTF(env, buffer);
   (*env)->CallStaticVoidMethod(env, loggerClass, logMethodId, jstr);
   
   (*env)->DeleteLocalRef(env, jstr); /* no longer needs jstr */
   (*env)->DeleteLocalRef(env,loggerClass);
   
   if(isAttached)
   {
      (*gJavaVM)->DetachCurrentThread(gJavaVM);
   }
}

