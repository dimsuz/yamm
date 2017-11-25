package com.dimsuz.yamm.core.log

interface Logger {
  fun debug(message: String)
  fun error(message: String)
  fun error(throwable: Throwable, message: String)

  // defined as part of Logger interface so that domain level classes could easily
  // get access to this function without having another dependency injected in
  // and without having to call some code referencing android functions (can break tests)
  fun checkMainThread()
}