package com.dimsuz.yamm.navigation

import android.app.Activity
import android.content.Intent
import com.bluelinelabs.conductor.Router
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class AppNavigator(private val activity: Activity,
                   conductorRouter: Router,
                   private val activityFactory: ActivityFactory,
                   controllerFactory: ControllerFactory) : Navigator {

  private val controllerNavigator = ControllerNavigatorHelper(conductorRouter, controllerFactory)

  override fun applyCommand(command: Command) {
    if (controllerNavigator.applyCommand(command)) {
      return
    }

    when (command) {
      is Forward -> {
        val intent = activityFactory.createIntent(activity, command.screenKey, command.transitionData)
        activity.startActivity(intent)
      }
      is Replace -> {
        val intent = activityFactory.createIntent(activity, command.screenKey, command.transitionData)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(intent)
      }
      is Back -> {
        activity.finish()
      }
      else -> {
        throw IllegalArgumentException("unsupported activity navigation command: $command")
      }
    }
  }

}