package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import java.net.{URLDecoder, URLEncoder}
import java.net
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 11/16/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
case class LentaBodyItemImage(val preview_url: String, val original_url: String, val caption: Option[String], val credits: Option[String]) extends ItemBase {
  def toXml(indent: String): String = {
    val sb = new StringBuilder(indent)
    sb.append("<image preview_url=\"").append(preview_url).append("\" ")
    sb.append("original_url=\"").append(original_url).append("\" ")

    caption match {
      case Some(value) => sb.append("caption=\"").append(StringEscapeUtils.escapeXml(StringEscapeUtils.unescapeXml(value))).append("\" ")
      case None =>
    }

    credits match {
      case Some(value) => sb.append("credits=\"").append(StringEscapeUtils.escapeXml(StringEscapeUtils.unescapeXml(value))).append("\" ")
      case None =>
    }

    sb.append("/>\n").toString()
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + preview_url.hashCode
    hash = hash * 31 + original_url.hashCode
    hash = hash * 31 + caption.hashCode
    hash = hash * 31 + credits.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaBodyItemImage])
      false
    else {
      val other = obj.asInstanceOf[LentaBodyItemImage]
      preview_url == other.preview_url && original_url == other.original_url && caption == other.caption && credits == other.credits
    }
  }
}
