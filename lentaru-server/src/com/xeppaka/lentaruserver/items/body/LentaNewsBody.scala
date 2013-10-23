package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 10/23/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaNewsBody(val photoTitle: String, val photoCredits: String, items: Seq[ItemBase])
  extends LentaBody(items) {

}
