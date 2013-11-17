package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 10/23/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
case class LentaNewsBody(val imageTitle: String, val imageCredits: String, items: List[ItemBase])
  extends LentaBody {

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + imageTitle.hashCode
    hash = hash * 31 + imageCredits.hashCode
    hash = hash * 31 + items.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaNewsBody])
      false
    else {
      val other = obj.asInstanceOf[LentaNewsBody]
      imageTitle == other.imageTitle && imageCredits == other.imageCredits && items == other.items
    }
  }
}
