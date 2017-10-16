package com.dimsuz.yamm.baseui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler.ControllerChangeListener
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.dimsuz.yamm.R

abstract class BaseControllerActivity : AppCompatActivity() {
  data class Config(
    val activityShellLayoutId: Int = R.layout.activity_content_base_shell,
    val contentViewId: Int = R.id.top_content_frame,
    val controllerChangeListeners: List<ControllerChangeListener> = emptyList())

  protected lateinit var conductorRouter: Router
    private set

  open val config get() = Config()

  abstract fun createController(): Controller?

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val activityConfig = config

    setContentView(activityConfig.activityShellLayoutId)

    val container = findViewById<ViewGroup>(activityConfig.contentViewId)
    conductorRouter = Conductor.attachRouter(this, container, savedInstanceState)
    if (!conductorRouter.hasRootController()) {
      val controller = createController()
      if (controller != null) {
        conductorRouter.setRoot(RouterTransaction.with(controller))
      }
    }

    activityConfig.controllerChangeListeners.forEach(conductorRouter::addChangeListener)
  }

  override fun onBackPressed() {
    if (!conductorRouter.handleBack()) {
      super.onBackPressed()
    }
  }
}
