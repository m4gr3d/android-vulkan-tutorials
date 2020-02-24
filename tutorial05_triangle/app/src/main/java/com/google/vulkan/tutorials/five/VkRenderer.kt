@file:JvmName("VkRenderer")
package com.google.vulkan.tutorials.five

import android.content.res.AssetManager
import android.view.Surface

class VkRenderer(private val assetManager : AssetManager) {
  fun onSurfaceCreated(surface : Surface) {
    nativeOnSurfaceCreated(assetManager, surface)
  }

  fun onSurfaceChanged(surface: Surface, width: Int, height: Int) {
    nativeOnSurfaceChanged(surface, width, height)
  }

  fun onDrawFrame() {
    nativeOnDrawFrame()
  }

  fun onDestroy() {
    nativeOnDestroy()
  }

  fun onPause() {
    nativeOnPause()
  }

  fun onResume() {
    nativeOnResume()
  }

  private external fun nativeOnSurfaceCreated(assetManager: AssetManager, surface: Surface)
  private external fun nativeOnSurfaceChanged(surface: Surface, width: Int, height: Int)
  private external fun nativeOnResume()
  private external fun nativeOnDrawFrame()
  private external fun nativeOnPause()
  private external fun nativeOnDestroy()
}