/*
 * hdhomerun_os_windows.h
 *
 * Copyright © 2006-2010 Silicondust USA Inc. <www.silicondust.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

#define _WINSOCKAPI_
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <wspiapi.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <signal.h>
#include <time.h>
#include <sys/types.h>
#include <sys/timeb.h>

#if defined(DLL_IMPORT)
#define LIBTYPE __declspec( dllexport )
#elif  defined(DLL_EXPORT)
#define LIBTYPE __declspec( dllimport )
#else
#define LIBTYPE
#endif

typedef int bool_t;
typedef signed __int8 int8_t;
typedef signed __int16 int16_t;
typedef signed __int32 int32_t;
typedef signed __int64 int64_t;
typedef unsigned __int8 uint8_t;
typedef unsigned __int16 uint16_t;
typedef unsigned __int32 uint32_t;
typedef unsigned __int64 uint64_t;
typedef void (*sig_t)(int);
typedef HANDLE pthread_t;
typedef HANDLE pthread_mutex_t;

#if !defined(va_copy)
#define va_copy(x, y) x = y
#endif

#define atoll _atoi64
#define strdup _strdup
#define strcasecmp _stricmp
#define fseeko _fseeki64
#define ftello _ftelli64
#define THREAD_FUNC_PREFIX DWORD WINAPI

#ifdef __cplusplus
extern "C" {
#endif

extern LIBTYPE uint32_t random_get32(void);
extern LIBTYPE uint64_t getcurrenttime(void);
extern LIBTYPE void msleep_approx(uint64_t ms);
extern LIBTYPE void msleep_minimum(uint64_t ms);

extern LIBTYPE int pthread_create(pthread_t *tid, void *attr, LPTHREAD_START_ROUTINE start, void *arg);
extern LIBTYPE int pthread_join(pthread_t tid, void **value_ptr);
extern LIBTYPE void pthread_mutex_init(pthread_mutex_t *mutex, void *attr);
extern LIBTYPE void pthread_mutex_lock(pthread_mutex_t *mutex);
extern LIBTYPE void pthread_mutex_unlock(pthread_mutex_t *mutex);

extern LIBTYPE bool_t hdhomerun_vsprintf(char *buffer, char *end, const char *fmt, va_list ap);
extern LIBTYPE bool_t hdhomerun_sprintf(char *buffer, char *end, const char *fmt, ...);

/*
 * The console output format should be set to UTF-8, however in XP and Vista this breaks batch file processing.
 * Attempting to restore on exit fails to restore if the program is terminated by the user.
 * Solution - set the output format each printf.
 */
extern LIBTYPE void console_vprintf(const char *fmt, va_list ap);
extern LIBTYPE void console_printf(const char *fmt, ...);

#ifdef __cplusplus
}
#endif
