package com.google.vulkan.tutorials.five

import android.os.Bundle
import android.support.v4.app.FragmentActivity

class VulkanSampleActivity : FragmentActivity() {

  companion object {
    init {
      System.loadLibrary("vktuts")
    }
  }

  private val vkSurfaceView : VkSurfaceView by lazy {
    VkSurfaceView(this)
  }

  private val renderer : VkRenderer by lazy {
    VkRenderer(resources.assets)
  }

  override fun onCreate(bundle: Bundle?) {
    super.onCreate(bundle)

    setContentView(vkSurfaceView)
    vkSurfaceView.startRenderer(renderer)
  }

  override fun onResume() {
    super.onResume()
    vkSurfaceView.onResume()
  }

  override fun onPause() {
    super.onPause()
    vkSurfaceView.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    vkSurfaceView.onDestroy()
  }
}