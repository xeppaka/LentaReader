package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import com.xeppaka.lentaruserver.items.body.VideoType.VideoType

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 11/16/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
case class LentaBodyItemVideo(val url: String, val kind: VideoType) extends ItemBase {
  def toXml(indent: String): String = {
    val sb = new StringBuilder(indent)
    sb.append("<video url=\"").append(url).append("\" type=\"").append(kind).append("\" />\n").toString
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + url.hashCode
    hash = hash * 31 + kind.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaBodyItemVideo])
      false
    else {
      val other = obj.asInstanceOf[LentaBodyItemVideo]
      url == other.url && kind == other.kind
    }
  }
}
