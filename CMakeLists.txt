# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             hdhomerun

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/logger.c
             src/main/cpp/jni_load.cpp
             src/main/cpp/legacy/hdhomerun_device_jni.c
             src/main/cpp/libhdhomerun/hdhomerun_channels.c
             src/main/cpp/libhdhomerun/hdhomerun_channelscan.c
             src/main/cpp/libhdhomerun/hdhomerun_control.c
             src/main/cpp/libhdhomerun/hdhomerun_debug.c
             src/main/cpp/libhdhomerun/hdhomerun_device.c
             src/main/cpp/libhdhomerun/hdhomerun_device_selector.c
             src/main/cpp/libhdhomerun/hdhomerun_discover.c
             src/main/cpp/libhdhomerun/hdhomerun_os_posix.c
             src/main/cpp/libhdhomerun/hdhomerun_pkt.c
             src/main/cpp/libhdhomerun/hdhomerun_sock_posix.c
             src/main/cpp/libhdhomerun/hdhomerun_video.c
             )

target_include_directories( hdhomerun PRIVATE src/main/cpp )

find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )

# Links your native library against one or more other native libraries.
target_link_libraries( # Specifies the target library.
             hdhomerun

             # Links the log library to the target library.
             ${log-lib} )