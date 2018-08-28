#include "discover_jni.hpp"

namespace {

    const static char *kJavaActivityClassPath = "com/zaren/HdhomerunSignalMeterLib/v2/discover/DiscoverDevices";

    jobject discoverDevices(JNIEnv *env,
                            jobject thiz) {
        return nullptr;
    }

    const static JNINativeMethod methods[] = {
            {"discoverDevices", "()Lcom/zaren/HdhomerunSignalMeterLib/data/HdhomerunDiscoverDeviceArray;", (void *) &discoverDevices},
    };
}

void discover::registerNatives(JNIEnv* env) {
    jclass activityClass = env->FindClass(kJavaActivityClassPath);
    env->RegisterNatives(activityClass, methods, sizeof(methods)/sizeof(methods[0]));
}
