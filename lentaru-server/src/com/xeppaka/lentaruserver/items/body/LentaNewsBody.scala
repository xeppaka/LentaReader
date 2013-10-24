package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 10/23/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaNewsBody(val imageTitle: String, val imageCredits: String, items: Seq[ItemBase])
  extends LentaBody(items) {

}

object LentaNewsBody {
  def apply(imageTitle: String, imageCredits: String, items: Seq[ItemBase]) = new LentaNewsBody(imageTitle, imageCredits, items)
}