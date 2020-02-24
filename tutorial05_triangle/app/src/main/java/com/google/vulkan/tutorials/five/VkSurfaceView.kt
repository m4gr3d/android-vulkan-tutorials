package com.google.vulkan.tutorials.five

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView

class VkSurfaceView internal constructor(context: Context?) : SurfaceView(context), VkThread.Callback, SurfaceHolder.Callback {

  private var renderer: VkRenderer? = null
  private val vkThread: VkThread

  companion object {
    fun checkState(expression: Boolean, errorMessage: Any) {
      check(expression) { errorMessage.toString() }
    }
  }

  init {
    isClickable = true
    holder.addCallback(this)
    vkThread = VkThread(this)
  }

  fun startRenderer(renderer: VkRenderer) {
    checkState(
      this.renderer == null, "setRenderer has already been called for this instance.")
    this.renderer = renderer
    vkThread.start()
  }

  override fun onVkThreadDestroy() {
    renderer?.onDestroy()
  }

  override fun onVkThreadSurfaceCreated() {
    renderer?.onSurfaceCreated(holder.surface)
  }

  override fun onVkThreadSurfaceChanged(width: Int, height: Int) {
    renderer?.onSurfaceChanged(holder.surface, width, height)
  }

  override fun onVkThreadDrawFrame() {
    renderer?.onDrawFrame()
  }

  override fun onVkThreadResume() {
    renderer?.onResume()
  }

  override fun onVkThreadPause() {
    renderer?.onPause()
  }

  fun queueOnVkThread(runnable: Runnable) {
    vkThread.queueEvent(runnable)
  }

  fun onResume() {
    vkThread.onResume()
  }

  fun onPause() {
    vkThread.onPause()
  }

  fun onDestroy() {
    vkThread.requestExitAndWait()
  }

  override fun surfaceCreated(holder: SurfaceHolder) {
    vkThread.surfaceCreated(holder)
  }

  override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    vkThread.surfaceChanged(width, height)
  }

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    vkThread.surfaceDestroyed()
  }
}