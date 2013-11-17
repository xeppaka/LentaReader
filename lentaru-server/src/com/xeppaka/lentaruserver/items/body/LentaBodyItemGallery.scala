package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 11/16/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
case class LentaBodyItemGallery(val images: List[LentaBodyItemImage]) extends ItemBase {
  def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<images>\n")
    images.foreach(image => builder.append(image.toXml(indentInternal)))
    builder.append(s"$indent</images>\n").toString()
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + images.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaBodyItemGallery])
      false
    else {
      val other = obj.asInstanceOf[LentaBodyItemGallery]
      images == other.images
    }
  }
}
