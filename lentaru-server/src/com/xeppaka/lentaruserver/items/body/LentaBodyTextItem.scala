package com.xeppaka.lentaruserver.items.body

import javax.xml.bind.annotation.{XmlAccessorType, XmlRootElement, XmlAccessType}

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 9/23/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */

class LentaBodyTextItem(val text: String) extends LentaBodyItemBase {
  override def toXml(): String = {
    return s"<text>$text</text>"
  }
}

object LentaBodyTextItem {
  def apply(text: String): LentaBodyTextItem = new LentaBodyTextItem(text)
}
