package com.dimsuz.yamm.baseui

import android.support.annotation.IdRes
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.dimsuz.yamm.BuildConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ResettableSupport {
  val refManager: ResettableReferencesManager
  var bindPropsRootView: View?
}

interface ResettableProperty {
  fun reset()
}

// Initial idea taken from https://github.com/bluelinelabs/Conductor/issues/234#issuecomment-282500119
// and mixed with suggestions from ni@

/**
 * Manages the list of references to reset. This is useful in Conductor's controllers to automatically clear
 * references in [Controller.onDestroyView]. References scoped to the view's lifecycle must be cleared
 * because Conductor's [Controller]s survive configuration changes
 */
class ResettableReferencesManager {
  private var delegates: MutableList<ResettableProperty>? = null

  fun register(delegate: ResettableProperty) {
    if (delegates == null) {
       delegates = mutableListOf<ResettableProperty>()
    }
    if (BuildConfig.DEBUG) {
      check(delegates?.contains(delegate) == false, { "attempt to register same delegate twice" })
    }
    delegates?.add(delegate)
  }

  fun reset() {
    delegates?.forEach(ResettableProperty::reset)
  }
}

/**
 * Kotlin delegate to automatically clear (nullify) strong view references.
 */
class BindView<in R, out T : Any>(@IdRes private val viewId: Int) : ReadOnlyProperty<R, T>, ResettableProperty
where R : ResettableSupport, R : Controller
{
  private var isRegistered = false
  private var value: T? = null

  override operator fun getValue(thisRef: R, property: KProperty<*>): T {
    ensureRegistered(thisRef)
    if (value == null) {
      value = findById(thisRef, property)
    }

    return value!!
  }

  private fun ensureRegistered(thisRef: R) {
    if (!isRegistered) {
      thisRef.refManager.register(this)
      isRegistered = true
    }
  }

  private fun findById(thisRef: R, property: KProperty<*>): T {
    val rootView = thisRef.bindPropsRootView ?: throw RuntimeException("root view not initialized, try accessing views in initializeView()")
    @Suppress("UNCHECKED_CAST")
    return rootView.findViewById<View>(viewId) as? T ?: throw RuntimeException("failed to find view by id for property ${property.name}")
  }

  override fun reset() {
    value = null
  }
}

private class Resettable<in R, T : Any> : ReadWriteProperty<R, T>, ResettableProperty
where R : ResettableSupport, R : Controller
{
  private var isRegistered = false

  private var value: T? = null

  override operator fun getValue(thisRef: R, property: KProperty<*>): T {
    return value ?: throw UninitializedPropertyAccessException(property.name)
  }

  override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
    ensureRegistered(thisRef)
    this.value = value
  }

  private fun ensureRegistered(thisRef: R) {
    if (!isRegistered) {
      thisRef.refManager.register(this)
      isRegistered = true
    }
  }

  override fun reset() {
    value = null
  }
}