#include <dirent.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <memory.h>
#include <platform/api/shell.h>

static void throw_exception(JNIEnv *env, const char *message)
{
    (*env)->ThrowNew(env, (*env)->FindClass(env, "com/github/ma1co/openmemories/adb/NativeException"), message);
}

JNIEXPORT void Java_com_github_ma1co_openmemories_adb_Shell_nativeExec(JNIEnv *env, jclass clazz, jstring command)
{
    const char *command_ptr = (*env)->GetStringUTFChars(env, command, NULL);
    int err = shell_exec_async(command_ptr);
    if (err)
        throw_exception(env, "shell_exec_async failed");
    (*env)->ReleaseStringUTFChars(env, command, command_ptr);
}

JNIEXPORT jint Java_com_github_ma1co_openmemories_adb_Procfs_nativeFindProcess(JNIEnv *env, jclass clazz, jbyteArray command)
{
    jsize length = (*env)->GetArrayLength(env, command);
    jbyte *command_ptr = (*env)->GetByteArrayElements(env, command, NULL);

    DIR *dp = opendir("/proc");
    int result = -1;
    struct dirent *ep;
    while ((ep = readdir(dp))) {
        if (ep->d_type == DT_DIR) {
            int pid = atoi(ep->d_name);
            if (pid) {
                char fn[255];
                snprintf(fn, 255, "/proc/%s/cmdline", ep->d_name);
                FILE *f = fopen(fn, "rb");
                if (f) {
                    char buf[length + 1];
                    int num = fread(buf, 1, (size_t) (length + 1), f);
                    fclose(f);
                    if (num == length && !memcmp(buf, command_ptr, (size_t) length)) {
                        result = pid;
                        break;
                    }
                }
            }
        }
    }
    closedir(dp);

    (*env)->ReleaseByteArrayElements(env, command, command_ptr, 0);

    return result;
}
