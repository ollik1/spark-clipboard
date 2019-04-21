package com.github.ollik1.clipboard

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

import scala.util.control.Exception.ultimately

/**
  * Allows testing clipboard contents. Not suitable for CI or any serious
  * stuff as the tests mutate a system-wide state.
  */
trait ClipboardFunctions {
  private val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard

  /**
    * Sets clipboard content to a given string, executes
    * given code block and restores the clipboard.
    */
  def withClipboard[T](str: String)(block: => T): T = {

    val contents = clipboard.getContents(null)

    ultimately {
      clipboard.setContents(contents, null)
    } {
      val sel = new StringSelection(str)
      clipboard.setContents(sel, sel)
      block
    }
  }
}
