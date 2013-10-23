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
  override def toString() = s"LentaNews[guid=$guid, title=$title, link=$link, description=$description, pubDate=$pubDate, body=$body]"
  def toXml(): String = {
    val builder = new StringBuilder("<lentanews>\n")
    builder.append(s"<guid>$guid</guid>\n")
    builder.append(s"<title>$title</title>\n")
    builder.append(s"<link>$link</link>\n")
    builder.append(s"<description>$description</description>\n")
    builder.append(s"<pubDate>$pubDate</pubDate>\n")
    builder.append(body.toXml())
    builder.append("</lentanews>\n").toString()
  }
}

object LentaNews {
  def apply(rssItem: RssItem): LentaNews = {
    val body = LentaBody.downloadNews(rssItem.link)
    new LentaNews(rssItem.guid, rssItem.title, rssItem.link, rssItem.description, rssItem.pubDate, body)
  }
}
