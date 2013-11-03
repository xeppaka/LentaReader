package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import com.xeppaka.lentaruserver.CDataEscaper

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 9/23/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */

class LentaBodyItemText(val text: String) extends ItemBase {
  override def toXml(indent: String): String = {
    val escaped = CDataEscaper.escapeText(text)
    return s"$indent<text>$escaped</text>\n"
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + text.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaBodyItemText])
      false

    val other = obj.asInstanceOf[LentaBodyItemText]
    if (text != other.text)
      false

    true
  }
}

object LentaBodyItemText {
  def apply(text: String): LentaBodyItemText = new LentaBodyItemText(text)
}
