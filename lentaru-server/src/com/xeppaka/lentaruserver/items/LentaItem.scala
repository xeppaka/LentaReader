package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.items.body.LentaBody
import com.xeppaka.lentaruserver.NewsType
import com.xeppaka.lentaruserver.NewsType.NewsType

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/21/13
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class LentaItem (
  val guid: String,
  val title: String,
  val link: String,
  val description: String,
  val pubDate: Long,
  val body: LentaBody) extends LentaItemBase
{
  override def toString() = s"LentaItem[guid=$guid, title=$title, link=$link, description=$description, pubDate=$pubDate, body=$body]"
}

object LentaItem {
  def apply(newsType: NewsType, rssItem: RssItem): LentaItem = {
    newsType match {
      case NewsType.NEWS => LentaNews(rssItem)
    }
  }
}