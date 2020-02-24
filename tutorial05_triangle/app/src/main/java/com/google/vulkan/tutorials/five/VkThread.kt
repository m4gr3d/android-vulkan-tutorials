package com.google.vulkan.tutorials.five

import android.util.Log
import android.view.SurfaceHolder
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class VkThread(callback: Callback) : Thread() {

  /** Callback interface to make VulkanRenderer calls on this thread.  */
  interface Callback {
    fun onVkThreadDestroy()
    fun onVkThreadSurfaceCreated()
    fun onVkThreadSurfaceChanged(width: Int, height: Int)
    fun onVkThreadDrawFrame()
    fun onVkThreadResume()
    fun onVkThreadPause()
  }

  companion object {
    private val TAG = VkThread::class.java.simpleName
  }

  private val callbackRef: WeakReference<Callback> = WeakReference(callback)

  /** Events queued to be run at the top of the loop.  */
  private val vkThreadEventQueue = ArrayList<Runnable>()

  /** Sync object to control access to private members of [VkThread].  */
  private val vkThreadLock = ReentrantLock()
  private val vkThreadCondition = vkThreadLock.newCondition()

  private var shouldExit = false
  private var exited = false
  private var resumed = false
  private var rendererInitialized = false
  private var rendererResumed = false
  private var hasSurface = false
  private var width = 0
  private var height = 0

  override fun run() {
    // Run function on this thread is modeled after GLSurfaceView's GLThread.
    try {
      while (true) {
        var event: Runnable? = null
        vkThreadLock.withLock {
          while (true) {
            // Code path for exiting the thread.
            if (shouldExit) {
              callbackRef.get()?.onVkThreadDestroy()
              return
            }
            // Check for events in the synchronized block, then break to execute them outside so
            // that we're not holding the lock anymore allowing for lifecycle vars to be changed
            // and new events to be queued.
            if (vkThreadEventQueue.isNotEmpty()) {
              event = vkThreadEventQueue.removeAt(0)
              break
            }
            if (readyToDraw()) {
              if (!rendererResumed) {
                val callback = callbackIfValid
                if (!rendererInitialized) {
                  rendererInitialized = true
                  callback.onVkThreadSurfaceCreated()
                }
                rendererResumed = true
                callback.onVkThreadResume()
                callback.onVkThreadSurfaceChanged(width, height)
              }

              // Break out of the synchronized block so we can draw without blocking
              // lifecycle/surface events and event queuing.
              break
            } else if (rendererResumed) {
              // If we aren't ready to draw but are resumed, that means we either lost a surface
              // or the app was paused.
              rendererResumed = false
              callbackIfValid.onVkThreadPause()
            }
            // We only reach this position if we both aren't ready to draw and have no events. All
            // event queueing and state changing needs to go through methods with synchronized
            // blocks on glThreadManager that will then notify the thread when they're done.
            vkThreadCondition.await()
          }
        }

        // Run events and draws outside of sync block
        if (event != null) {
          event!!.run()
          continue
        }

        // Draw only when there aren't any events to deal with
        callbackIfValid.onVkThreadDrawFrame()
      }
    } catch (interruption: InterruptedException) {
      // Just exit
    } catch (e: IllegalStateException) {
      Log.i(TAG, e.message)
    } finally {
      threadExiting()
    }
  }

  private val callbackIfValid: Callback
    get() = callbackRef.get()
      ?: throw IllegalStateException("Failed to acquire callback reference. Exiting thread.")

  private fun readyToDraw() = hasSurface && resumed

  private fun threadExiting() {
    vkThreadLock.withLock {
      exited = true
      vkThreadCondition.signalAll()
    }
  }

  fun queueEvent(event: Runnable) {
    vkThreadLock.withLock {
      vkThreadEventQueue.add(event)
      vkThreadCondition.signalAll()
    }
  }

  fun requestExitAndWait() {
    vkThreadLock.withLock {
      shouldExit = true
      vkThreadCondition.signalAll()
      while (!exited) {
        try {
          Log.i(TAG, "Waiting on exit")
          vkThreadCondition.await()
        } catch (ex: InterruptedException) {
          currentThread().interrupt()
        }
      }
    }
  }

  fun onPause() {
    vkThreadLock.withLock {
      resumed = false
      vkThreadCondition.signalAll()
    }
  }

  fun onResume() {
    vkThreadLock.withLock {
      resumed = true
      vkThreadCondition.signalAll()
    }
  }

  fun surfaceCreated(holder: SurfaceHolder?) {
    // This is a no op because surface creation will always be followed by surfaceChanged()
    // which provide all the needed information.
  }

  fun surfaceChanged(w: Int, h: Int) {
    vkThreadLock.withLock {
      hasSurface = true
      width = w
      height = h
      vkThreadCondition.signalAll()
    }
  }

  fun surfaceDestroyed() {
    vkThreadLock.withLock {
      hasSurface = false
      vkThreadCondition.signalAll()
    }
  }

}