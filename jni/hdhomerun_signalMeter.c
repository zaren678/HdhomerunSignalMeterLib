#include <string.h>
#include "hdhomerun.h"
#include "com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice.h"
#include "com_zaren_HdhomerunSignalMeterLib_data_DiscoverTask.h"

JavaVM *gJavaVM;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
   JNIEnv *env;
   gJavaVM = vm;
   LOGI("JNI_OnLoad called");
   if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) 
   {
      LOGE("Failed to get the environment using GetEnv()");
      return -1;
   }
   //hClass instance caching 12bi
   //hNative function registration 13i
   
   setLoggerVM( gJavaVM);
   
   LOGI("OnLoad finished");
   
   return JNI_VERSION_1_4;
}

jobject
Java_com_zaren_HdhomerunSignalMeterLib_data_DiscoverTask_discover( JNIEnv* env,
                                                  jobject thiz )
{
	struct hdhomerun_discover_device_t discover_array[10];
   int num_found = 0;
   int i = 0;
   int j = 0;

   jclass discoverDeviceArrayClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/HdhomerunDiscoverDeviceArray");
   jmethodID constructorId = (*env)->GetMethodID(env,discoverDeviceArrayClass,"<init>","()V");
   jmethodID insertId = (*env)->GetMethodID(env,discoverDeviceArrayClass,"insert","(IJJI)V");   
   jobject retObj = (*env)->NewObject(env, discoverDeviceArrayClass, constructorId);

	num_found = hdhomerun_discover_find_devices_custom(0, HDHOMERUN_DEVICE_TYPE_TUNER, HDHOMERUN_DEVICE_ID_WILDCARD, discover_array, 10);

	MY_LOGD("discover(): num_found %d",num_found );
	
	if(num_found == -1)
	{
	   MY_LOGD("Error discovering devices");
		jmethodID setErrorId = (*env)->GetMethodID(env,discoverDeviceArrayClass,"setError","()V");
	   (*env)->CallVoidMethod(env,retObj, setErrorId );				
	}
		
	for(i=0; i<num_found; i++)
	{
		MY_LOGD("Found %d devices, IP:%s, ID: %X, type: %d",
			num_found,
			ipAddressToString(discover_array[0].ip_addr),
			discover_array[0].device_id,
			discover_array[0].device_type);

		 for(j=0; j<discover_array[i].tuner_count; j++)
		 {
			(*env)->CallVoidMethod(env, retObj, insertId,
					(jint)discover_array[i].ip_addr,
					(jlong)discover_array[i].device_type,
					(jlong)discover_array[i].device_id,
					(jint)j);
		 }
	}
	
	return retObj;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIcreateNewDevice
  (JNIEnv * env, jobject thiz, jlong deviceId_val, jlong ipAddr_val, jint tuner_val)
{
   struct hdhomerun_device_t* device;

   MY_LOGD("C: createNewDevice");
   
   device = hdhomerun_device_create((uint32_t)deviceId_val,
                           (uint32_t)ipAddr_val,
                           (unsigned int)tuner_val,
                           NULL);

   MY_LOGD("New device created: %p", device);

   return (jint) device;
}

JNIEXPORT void JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIdestroy
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   MY_LOGD("C: destroy device");
   hdhomerun_device_destroy(device);
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIsetChannelMap
  (JNIEnv * env, jobject thiz, jint cPointer, jstring channelMapStr)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   const char *channelMap = (*env)->GetStringUTFChars(env,channelMapStr, 0);
   int retVal = 0;

   MY_LOGD("Set Channel Map: pointer: %p, channelMap %s", device, channelMap);
   retVal = hdhomerun_device_set_tuner_channelmap(device, channelMap);

   (*env)->ReleaseStringUTFChars(env, channelMapStr, channelMap);

   return (jint)retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIsetTunerChannel
  (JNIEnv * env, jobject thiz, jint cPointer, jstring channelStr)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   const char *channel = (*env)->GetStringUTFChars(env,channelStr, 0);
   int retVal = 0;

   MY_LOGD("C: setTunerChannel");

   retVal = hdhomerun_device_set_tuner_channel(device, channel);

   (*env)->ReleaseStringUTFChars(env, channelStr, channel);

   return (jint)retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIsetTunerVChannel
  (JNIEnv * env, jobject thiz, jint cPointer, jstring channelStr)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   const char *channel = (*env)->GetStringUTFChars(env,channelStr, 0);
   int retVal = 0;

   MY_LOGD("C: setTunerVChannel");

   retVal = hdhomerun_device_set_tuner_vchannel(device, channel);

   (*env)->ReleaseStringUTFChars(env, channelStr, channel);

   return (jint)retVal;
}

JNIEXPORT jobject JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerStatus
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   struct hdhomerun_tuner_status_t tuner_status;
   int status = 0;

   MY_LOGV("C: getTunerStatus");

   status = hdhomerun_device_get_tuner_status(device, NULL, &tuner_status);
  
   jclass TunerStatusClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/TunerStatus");
   jmethodID constructorId = (*env)->GetMethodID(env,TunerStatusClass,"<init>","(Ljava/lang/String;Ljava/lang/String;ZZZJJJJJI)V");

   jobject retObj = (*env)->NewObject(env, TunerStatusClass, constructorId,
                        (*env)->NewStringUTF(env, tuner_status.channel),
                        (*env)->NewStringUTF(env, tuner_status.lock_str),
                        (jboolean)tuner_status.signal_present,
                        (jboolean)tuner_status.lock_supported,
                        (jboolean)tuner_status.lock_unsupported,
                        (jlong)tuner_status.signal_strength,
                        (jlong)tuner_status.signal_to_noise_quality,
                        (jlong)tuner_status.symbol_error_quality,
                        (jlong)tuner_status.raw_bits_per_second,
                        (jlong)tuner_status.packets_per_second,
						(jint)status);
   return retObj;

}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIupdateTunerStatus
  (JNIEnv * env, jobject thiz, jint cPointer, jobject tunerStatus)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   struct hdhomerun_tuner_status_t tuner_status;
   int status = 0;
   jclass TunerStatusClass;
   jmethodID setAllFieldsId;

   MY_LOGV("C: updateTunerStatus");

   status = hdhomerun_device_get_tuner_status(device, NULL, &tuner_status);

   TunerStatusClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/TunerStatus");
   setAllFieldsId = (*env)->GetMethodID(env,TunerStatusClass,"setAllFields","(Ljava/lang/String;Ljava/lang/String;ZZZJJJJJI)V");
   (*env)->CallVoidMethod(env, tunerStatus, setAllFieldsId,
            (*env)->NewStringUTF(env, tuner_status.channel),
             (*env)->NewStringUTF(env, tuner_status.lock_str),
             (jboolean)tuner_status.signal_present,
             (jboolean)tuner_status.lock_supported,
             (jboolean)tuner_status.lock_unsupported,
             (jlong)tuner_status.signal_strength,
             (jlong)tuner_status.signal_to_noise_quality,
             (jlong)tuner_status.symbol_error_quality,
             (jlong)tuner_status.raw_bits_per_second,
             (jlong)tuner_status.packets_per_second,
             (jint)status);

   return status;
}

JNIEXPORT jstring JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetSupported
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   char supportedBuf[100];
   char* supported = supportedBuf;
   int status = 0;

   MY_LOGD("C: getSupported");

   status = hdhomerun_device_get_supported(device, NULL, &supported);
   
   if(status == 1)
   {
		return (*env)->NewStringUTF(env, supported);
   }
   else if( status == 0)
   {
	   return (*env)->NewStringUTF(env, "rejected");
   }
   else
   {
	   return (*env)->NewStringUTF(env, "Network failure");
   }
}

JNIEXPORT jstring JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetChannelMap
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   char channelMapBuf[100];
   char* channelMap = channelMapBuf;
   int status = 0;

   MY_LOGD("C: getChannelMap");

   status = hdhomerun_device_get_tuner_channelmap(device, &channelMap);

   if(status == 1)
   {
		return (*env)->NewStringUTF(env, channelMap);
   }
   else if( status == 0)
   {
	   return (*env)->NewStringUTF(env, "rejected");
   }
   else
   {
	   return (*env)->NewStringUTF(env, "Network failure");
   }
   
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerStreamInfo
  (JNIEnv * env, jobject thiz, jint cPointer, jobject streamInfo)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 1;
   char buffer[1000];
   char* streamNfo = buffer;
   
   jclass jniStringsClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/JniString");
   jmethodID setStringId = (*env)->GetMethodID(env,jniStringsClass,"setString","(Ljava/lang/String;)V");
   
   MY_LOGD("C: getTunerStreamInfo");
   
   memset(buffer,0,sizeof(char)*1000);
   
   retVal = hdhomerun_device_get_tuner_streaminfo(device, &streamNfo);
   
   (*env)->CallVoidMethod(env, streamInfo, setStringId,
                         (*env)->NewStringUTF(env, streamNfo));
   
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIwaitForLock
  (JNIEnv *env, jobject thiz, jint cPointer, jobject tunerStatus)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 1;
   struct hdhomerun_tuner_status_t cTunerStatus;
   jclass TunerStatusClass;
   jmethodID setAllFieldsId;
   
   MY_LOGD("C: waitForLock");
   
   retVal = hdhomerun_device_wait_for_lock(device, &cTunerStatus);
   
   if( (retVal > 0) && (cTunerStatus.lock_supported == TRUE))
   {
      /* Wait for symbol quality = 100%. */
	   uint64_t timeout = getcurrenttime() + 5000;
	   while (1) 
      {
	     retVal = hdhomerun_device_get_tuner_status(device, NULL, &cTunerStatus);
	     if (retVal <= 0)
        {
		    break;// retVal;
		  }

		  if (cTunerStatus.symbol_error_quality == 100)
        {
          retVal = 1;
		    break;// 1;
		  }

		  if (getcurrenttime() >= timeout)
        {
          retVal = 1;
		    break;//return 1;
		  }

	     	msleep_approx(250);
	   }
   }

   MY_LOGD("C: waitForLock lock_Supported %d, lock_unsupported %d, signal_present %d",
                      cTunerStatus.lock_supported,
                      cTunerStatus.lock_unsupported,
                      cTunerStatus.signal_present);
   
   TunerStatusClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/TunerStatus");
   setAllFieldsId = (*env)->GetMethodID(env,TunerStatusClass,"setAllFields","(Ljava/lang/String;Ljava/lang/String;ZZZJJJJJI)V");
   (*env)->CallVoidMethod(env, tunerStatus, setAllFieldsId,
            (*env)->NewStringUTF(env, cTunerStatus.channel),
             (*env)->NewStringUTF(env, cTunerStatus.lock_str),
             (jboolean)cTunerStatus.signal_present,
             (jboolean)cTunerStatus.lock_supported,
             (jboolean)cTunerStatus.lock_unsupported,
             (jlong)cTunerStatus.signal_strength,
             (jlong)cTunerStatus.signal_to_noise_quality,
             (jlong)cTunerStatus.symbol_error_quality,
             (jlong)cTunerStatus.raw_bits_per_second,
             (jlong)cTunerStatus.packets_per_second,
             (jint)retVal);
             
   return retVal;
}

JNIEXPORT jstring JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetModel
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   return (*env)->NewStringUTF(env,hdhomerun_device_get_model_str(device));
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIcreateChannelList
  (JNIEnv * env, jobject thiz, jstring channelMapStr, jobject channelList)
{
   const char *channelMap = (*env)->GetStringUTFChars(env,channelMapStr, 0);
   int retVal = 0;
   jobject javaEntry;
   jobject javaEntryNew;
   jobject javaEntryPrev;
   
   jclass channelListClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/ChannelList");
   jmethodID channelListSetHeadId = (*env)->GetMethodID(env,channelListClass,"setHead","(Lcom/zaren/HdhomerunSignalMeterLib/data/ChannelEntry;)V");
   jmethodID channelListSetTailId = (*env)->GetMethodID(env,channelListClass,"setTail","(Lcom/zaren/HdhomerunSignalMeterLib/data/ChannelEntry;)V");
   jmethodID channelListFindMinAndMaxId = (*env)->GetMethodID(env,channelListClass,"findMaxAndMin", "()V");
   
   jclass channelEntryClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/ChannelEntry");
   jmethodID channelEntryConstructorId = (*env)->GetMethodID(env,channelEntryClass,"<init>","(Ljava/lang/String;II)V");
   jmethodID channelEntrySetNextId = (*env)->GetMethodID(env,channelEntryClass,"setNext","(Lcom/zaren/HdhomerunSignalMeterLib/data/ChannelEntry;)V");
   jmethodID channelEntrySetPrevId = (*env)->GetMethodID(env,channelEntryClass,"setPrev","(Lcom/zaren/HdhomerunSignalMeterLib/data/ChannelEntry;)V");
   jmethodID channelEntryGetNextId = (*env)->GetMethodID(env,channelEntryClass,"getNext","()Lcom/zaren/HdhomerunSignalMeterLib/data/ChannelEntry;");
   jmethodID channelEntryGetPrevId = (*env)->GetMethodID(env,channelEntryClass,"getPrev","()Lcom/zaren/HdhomerunSignalMeterLib/data/ChannelEntry;");
   
   MY_LOGD("C: createChannelList %s", channelMap);
   
   struct hdhomerun_channel_list_t *channel_list_ptr;
   
   channel_list_ptr = hdhomerun_channel_list_create(channelMap);
   if (!channel_list_ptr) 
   {
	   return 0;
	}
   
   struct hdhomerun_channel_entry_t *entry = hdhomerun_channel_list_first(channel_list_ptr);
   if(entry)
   {
      MY_LOGV("C: adding head entry: num %d freq %d", entry->channel_number, entry->frequency);
      //This is the first one, allocate it and set it as the head
      javaEntryPrev = (*env)->NewObject(env, channelEntryClass, channelEntryConstructorId,
                                        (*env)->NewStringUTF(env, entry->name),
                                        (jint)entry->frequency,
                                        (jint)entry->channel_number);
              
      (*env)->CallVoidMethod(env, channelList, channelListSetHeadId, javaEntryPrev);
      
      entry = hdhomerun_channel_list_next(channel_list_ptr, entry);
   }
   
   while (entry) 
   {
      MY_LOGV("C: adding entry: num %d freq %d", entry->channel_number, entry->frequency);
      
      jstring nameString = (*env)->NewStringUTF(env, entry->name);
      
      //allocate the new entry
      javaEntryNew = (*env)->NewObject(env, channelEntryClass, channelEntryConstructorId,
                                   nameString,
                                   (jint)entry->frequency,
                                   (jint)entry->channel_number);
                                   
      //set prev->next = this
      (*env)->CallVoidMethod(env,javaEntryPrev, channelEntrySetNextId, javaEntryNew);
      
      //set this->prev = prev
      (*env)->CallVoidMethod(env,javaEntryNew, channelEntrySetPrevId, javaEntryPrev);
      
      //release the reference to prev
      (*env)->DeleteLocalRef(env, javaEntryPrev);
      
      //prev = this
      javaEntryPrev = javaEntryNew;
      
      entry = hdhomerun_channel_list_next(channel_list_ptr, entry);
      
      //get rid of the references to the name string so the local reference table doesn't get too big
      (*env)->DeleteLocalRef(env, nameString);
      
      if(!entry)
      {
         //this is the last one, set it as the tail
         (*env)->CallVoidMethod(env, channelList, channelListSetTailId, javaEntryNew);
      }
   }
   
   MY_LOGD("C: done adding entries");
   //get the max and min
   (*env)->CallVoidMethod(env,channelList,channelListFindMinAndMaxId);
   
   (*env)->ReleaseStringUTFChars(env, channelMapStr, channelMap);

   //free up the C channel list
   hdhomerun_channel_list_destroy(channel_list_ptr);
   
   return 1;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIupdateTunerVStatus
  (JNIEnv * env, jobject thiz, jint cPointer, jobject tunerVStatus)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   
   struct hdhomerun_tuner_vstatus_t tuner_vstatus;
   int status = 0;
   jclass TunerVStatusClass;
   jmethodID setAllFieldsId;

   MY_LOGV("C: updateTunerVStatus");

   status = hdhomerun_device_get_tuner_vstatus(device, NULL, &tuner_vstatus);

   TunerVStatusClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/TunerVStatus");
   setAllFieldsId = (*env)->GetMethodID(env,TunerVStatusClass,"setAllFields","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZI)V");
   (*env)->CallVoidMethod(env, tunerVStatus, setAllFieldsId,
             (*env)->NewStringUTF(env, tuner_vstatus.vchannel),
             (*env)->NewStringUTF(env, tuner_vstatus.name),
             (*env)->NewStringUTF(env, tuner_vstatus.auth),
             (*env)->NewStringUTF(env, tuner_vstatus.cci),
             (*env)->NewStringUTF(env, tuner_vstatus.cgms),
             (jboolean)tuner_vstatus.not_subscribed,
             (jboolean)tuner_vstatus.not_available,
             (jboolean)tuner_vstatus.copy_protected,
             (jint)status);
             
   return (jint)status;
}


JNIEXPORT jobject JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerVStatus
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   struct hdhomerun_tuner_vstatus_t tuner_vstatus;
   int status = 0;

   MY_LOGV("C: getTunerStatus");

   status = hdhomerun_device_get_tuner_vstatus(device, NULL, &tuner_vstatus);
  
   jclass TunerVStatusClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/TunerVStatus");
   jmethodID constructorId = (*env)->GetMethodID(env,TunerVStatusClass,"<init>","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZI)V");

   jobject retObj = (*env)->NewObject(env, TunerVStatusClass, constructorId,
                        (*env)->NewStringUTF(env, tuner_vstatus.vchannel),
                        (*env)->NewStringUTF(env, tuner_vstatus.name),
                        (*env)->NewStringUTF(env, tuner_vstatus.auth),
                        (*env)->NewStringUTF(env, tuner_vstatus.cci),
                        (*env)->NewStringUTF(env, tuner_vstatus.cgms),
                        (jboolean)tuner_vstatus.not_subscribed,
                        (jboolean)tuner_vstatus.not_available,
                        (jboolean)tuner_vstatus.copy_protected,
                        (jint)status);
   return retObj;
}

JNIEXPORT jstring JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetFirmwareVersion
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   char firmwareBuf[100];
   char* firmwarePtr = firmwareBuf;
   uint32_t version_num = 0;
   
   MY_LOGD("C: JNIgetFirmwareVersion");
   
   int count = 0;
   while (1) 
   {
		if (hdhomerun_device_get_version(device, &firmwarePtr, &version_num) >= 0) 
      {
			break;
		}

		count++;
		if (count > 10) 
      {
         return (*env)->NewStringUTF(env, "");			
		}

		msleep_minimum(250);
	}
   
   return (*env)->NewStringUTF(env, firmwarePtr);
}

JNIEXPORT jstring JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetLockkeyOwner
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   char ownerBuf[100];
   char* ownerPtr = ownerBuf;
   
   MY_LOGD("C: JNIgetLockkeyOwner");
   
   hdhomerun_device_get_tuner_lockkey_owner(device, &ownerPtr);
   
   return (*env)->NewStringUTF(env, ownerPtr);
}

JNIEXPORT jstring JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerTarget
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   char targetBuf[100];
   char* targetPtr = targetBuf;
   
   MY_LOGD("C: JNIgetTunerTarget");
   
   hdhomerun_device_get_tuner_target(device, &targetPtr);
   
   return (*env)->NewStringUTF(env, targetPtr);
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIsetVar
  (JNIEnv * env, jobject thiz, jint cPointer, jstring javaVarString, jstring javaValueString)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   const char *varString = (*env)->GetStringUTFChars(env,javaVarString, 0);
   const char *valueString = (*env)->GetStringUTFChars(env,javaValueString, 0);
   
   char pValueBuf[100];
   char* pValuePtr = pValueBuf;
   
   char pErrorBuf[100];
   char* pErrorPtr = pErrorBuf;
   
   MY_LOGD("C: JNIsetVar, %s %s\n",varString, valueString);
   
   retVal = hdhomerun_device_set_var(device, varString, valueString, &pValuePtr, &pErrorPtr);
   
   MY_LOGD("C: JNIsetVar, return %d, pValue %s, pError %s\n",retVal, pValuePtr, pErrorPtr);
   
   
   (*env)->ReleaseStringUTFChars(env, javaVarString, varString);
   (*env)->ReleaseStringUTFChars(env, javaValueString, valueString);
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNItunerLockeyRequest
  (JNIEnv * env, jobject thiz, jint cPointer, jobject javaErrorString)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   char buffer[100];
   char* error = buffer;
   
   jclass jniStringsClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/JniString");
   jmethodID setStringId = (*env)->GetMethodID(env,jniStringsClass,"setString","(Ljava/lang/String;)V");
   
   MY_LOGD("C: tunerLockeyRequest");
   
   memset(buffer,0,sizeof(char)*100);
   
   retVal = hdhomerun_device_tuner_lockkey_request(device, &error);
   
   (*env)->CallVoidMethod(env, javaErrorString, setStringId,
                         (*env)->NewStringUTF(env, error));
                         
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNItunerLockeyRelease
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   MY_LOGD("C: tunerLockeyRelease");
   
   retVal = hdhomerun_device_tuner_lockkey_release(device);
                         
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNItunerLockeyForce
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   MY_LOGD("C: tunerLockeyForce");
   
   retVal = hdhomerun_device_tuner_lockkey_force(device);
                         
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerProgram
  (JNIEnv * env, jobject thiz, jint cPointer, jobject javaProgString)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   char buffer[100];
   char* program = buffer;
   
   jclass jniStringsClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/JniString");
   jmethodID setStringId = (*env)->GetMethodID(env,jniStringsClass,"setString","(Ljava/lang/String;)V");
   
   MY_LOGD("C: getTunerProgram");
   
   memset(buffer,0,sizeof(char)*100);
   
   retVal = hdhomerun_device_get_tuner_program(device, &program);
   
   (*env)->CallVoidMethod(env, javaProgString, setStringId,
                         (*env)->NewStringUTF(env, program));
                         
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIsetTunerProgram
  (JNIEnv * env, jobject thiz, jint cPointer, jstring progStr)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   const char *prog = (*env)->GetStringUTFChars(env,progStr, 0);
   int retVal = 0;

   MY_LOGD("C: setTunerProgram: pointer: %p, program %s", device, prog);
   retVal = hdhomerun_device_set_tuner_program(device, prog);

   (*env)->ReleaseStringUTFChars(env, progStr, prog);

   return (jint)retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIsetTunerTarget
  (JNIEnv * env, jobject thiz, jint cPointer, jstring targetStr)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   const char *target = (*env)->GetStringUTFChars(env,targetStr, 0);
   int retVal = 0;

   MY_LOGD("C: setTunerTarget: pointer: %p, target %s", device, target);
   retVal = hdhomerun_device_set_tuner_target(device, target);

   (*env)->ReleaseStringUTFChars(env, targetStr, target);

   return (jint)retVal;
}

JNIEXPORT void JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIstreamStop
  (JNIEnv * env, jobject thiz, jint cPointer)
{
   struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;

   MY_LOGD("C: streamStop: pointer: %p", device);
   hdhomerun_device_stream_stop(device);
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerChannel
  (JNIEnv * env, jobject thiz, jint cPointer, jobject javaChannelString)
{
	struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   char buffer[100];
   char* channel = buffer;
   
   jclass jniStringsClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/JniString");
   jmethodID setStringId = (*env)->GetMethodID(env,jniStringsClass,"setString","(Ljava/lang/String;)V");
   
   MY_LOGD("C: getTunerChannel");
   
   memset(buffer,0,sizeof(char)*100);
   
   retVal = hdhomerun_device_get_tuner_channel(device, &channel);
   
   (*env)->CallVoidMethod(env, javaChannelString, setStringId,
                         (*env)->NewStringUTF(env, channel));
                         
   return retVal;
}

JNIEXPORT jint JNICALL Java_com_zaren_HdhomerunSignalMeterLib_data_HdhomerunDevice_JNIgetTunerVChannel
  (JNIEnv * env, jobject thiz, jint cPointer, jobject javaVChannelStr)
{
	struct hdhomerun_device_t* device = (struct hdhomerun_device_t*)cPointer;
   int retVal = 0;
   
   char buffer[100];
   char* channel = buffer;
   
   jclass jniStringsClass = (*env)->FindClass(env,"com/zaren/HdhomerunSignalMeterLib/data/JniString");
   jmethodID setStringId = (*env)->GetMethodID(env,jniStringsClass,"setString","(Ljava/lang/String;)V");
   
   MY_LOGD("C: getTunerVChannel");
   
   memset(buffer,0,sizeof(char)*100);
   
   retVal = hdhomerun_device_get_tuner_vchannel(device, &channel);
   
   (*env)->CallVoidMethod(env, javaVChannelStr, setStringId,
                         (*env)->NewStringUTF(env, channel));
                         
   return retVal;
}
