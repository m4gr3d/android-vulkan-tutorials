#include <android/log.h>
#include <jni.h>
#include "android/asset_manager_jni.h"
#include "android/native_window_jni.h"
#include "VulkanMain.hpp"

#define LOG_TAG "VulkanRenderer"

#define ALOG_ASSERT(_cond, ...) \
	if (!(_cond)) __android_log_assert("conditional", LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define ALOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)

extern "C" {
JNIEXPORT void JNICALL Java_com_google_vulkan_tutorials_five_VkRenderer_nativeOnSurfaceCreated(
    JNIEnv* env,
    jobject obj, jobject j_asset_manager, jobject j_surface) {

  AAssetManager *manager = AAssetManager_fromJava(env, j_asset_manager);
  ANativeWindow *window = ANativeWindow_fromSurface(env, j_surface);
  ALOGV("Initializing vulkan...");
  InitializeVulkan(manager, window);
}

JNIEXPORT void JNICALL Java_com_google_vulkan_tutorials_five_VkRenderer_nativeOnSurfaceChanged(
    JNIEnv* env,
    jobject obj,
    jobject j_surface,
    jint width,
    jint height) {

}

JNIEXPORT void JNICALL Java_com_google_vulkan_tutorials_five_VkRenderer_nativeOnResume(
    JNIEnv* env,
    jobject obj) {

}

JNIEXPORT void JNICALL Java_com_google_vulkan_tutorials_five_VkRenderer_nativeOnDrawFrame(
    JNIEnv* env,
    jobject obj) {
  if (IsVulkanReady()) {
    VulkanDrawFrame();
  } else {
    ALOGW("Vulkan is not read..");
  }
}

JNIEXPORT void JNICALL Java_com_google_vulkan_tutorials_five_VkRenderer_nativeOnPause(
    JNIEnv* env,
    jobject obj) {

}

JNIEXPORT void JNICALL Java_com_google_vulkan_tutorials_five_VkRenderer_nativeOnDestroy(
    JNIEnv* env,
    jobject obj) {
  ALOGV("Deleting vulkan...");
  DeleteVulkan();
}
}