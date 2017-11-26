package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.presentation.baseui.MviView

interface Messages {
  interface View : MviView<ViewState>

  class ViewState
}