package com.dimsuz.yamm.util

import toothpick.Scope

inline fun <reified T> Scope.instance() = this.getInstance(T::class.java)