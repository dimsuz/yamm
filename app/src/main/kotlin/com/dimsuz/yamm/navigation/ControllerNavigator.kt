package com.dimsuz.yamm.navigation

import com.bluelinelabs.conductor.Router
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.*

class ControllerNavigator(private val conductorRouter: Router,
                          private val controllerFactory: ControllerFactory): Navigator {

  override fun applyCommand(command: Command) {
    when (command) {
      is Back -> conductorRouter.popCurrentController()
      is BackTo -> {
        conductorRouter.popToTag(command.screenKey)
          .also { popped -> check(popped, { "routing back to ${command.screenKey} failed, no such tag" }) }
      }
      is Forward -> {
        val controller = controllerFactory.createController(command.screenKey, command.transitionData)
        val transaction = controllerFactory.createPushTransaction(command.screenKey, command.transitionData, controller)
        conductorRouter.pushController(transaction)
      }
      is Replace -> {
        val controller = controllerFactory.createController(command.screenKey, command.transitionData)
        val transaction = controllerFactory.createReplaceTransaction(command.screenKey, command.transitionData, controller)
        conductorRouter.replaceTopController(transaction)
      }
    }
  }
}