package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 9/23/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */

class LentaBodyItemText(val text: String) extends ItemBase {
  override def toXml(): String = {
    return s"<text>$text</text>\n"
  }
}

object LentaBodyItemText {
  def apply(text: String): LentaBodyItemText = new LentaBodyItemText(text)
}
