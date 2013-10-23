package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.items.body.LentaBody

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/23/13
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaNews(
  guid: String,
  title: String,
  link: String,
  description: String,
  pubDate: Long,
  body: LentaBody) extends LentaItem(guid, title, link, description, pubDate, body)
{
  def toXml(): String = "123"
}

object LentaNews {
  def apply(rssItem: RssItem): LentaNews = {
    val body = LentaBody.downloadNews(rssItem.link)
    new LentaNews(rssItem.guid, rssItem.title, rssItem.link, rssItem.description, rssItem.pubDate, body)
  }
}
