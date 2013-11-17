package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 11/16/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
case class LentaBodyItemImage(val url: String, val caption: Option[String], val credits: Option[String]) extends ItemBase {
  def toXml(indent: String): String = {
    val sb = new StringBuilder(indent)
    sb.append("<image url=\"").append(url).append("\" ")

    caption match {
      case Some(value) => sb.append("caption=\"").append(value).append("\" ")
      case None =>
    }

    credits match {
      case Some(value) => sb.append("credits=\"").append(value).append("\" ")
      case None =>
    }

    sb.append("/>\n").toString()
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + url.hashCode
    hash = hash * 31 + caption.hashCode
    hash = hash * 31 + credits.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaBodyItemImage])
      false
    else {
      val other = obj.asInstanceOf[LentaBodyItemImage]
      url == other.url && caption == other.caption && credits == other.credits
    }
  }
}
