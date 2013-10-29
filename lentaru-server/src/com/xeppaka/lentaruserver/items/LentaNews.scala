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
  image: String,
  imageTitle: String,
  imageCredits: String,
  description: String,
  pubDate: Long,
  body: LentaBody) extends LentaItem(guid, title, link, image, imageTitle, imageCredits, description, pubDate, body)
{
  override def toString() = s"LentaNews[guid=$guid, title=$title, link=$link, description=$description, pubDate=$pubDate, body=$body]"
  def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentanews>\n")
    builder.append(s"$indentInternal<guid>$guid</guid>\n")
    builder.append(s"$indentInternal<title>$title</title>\n")
    builder.append(s"$indentInternal<link>$link</link>\n")
    builder.append(s"$indentInternal<link>$image</link>\n")
    builder.append(s"$indentInternal<link>$imageTitle</link>\n")
    builder.append(s"$indentInternal<link>$imageCredits</link>\n")
    builder.append(s"$indentInternal<description>$description</description>\n")
    builder.append(s"$indentInternal<pubDate>$pubDate</pubDate>\n")
    builder.append(body.toXml(indentInternal))
    builder.append(s"$indent</lentanews>\n").toString()
  }
}

object LentaNews {
  def apply(rssItem: RssItem): LentaNews = {
    val body = LentaBody.downloadNews(rssItem.link)
    new LentaNews(rssItem.guid, rssItem.title, rssItem.link, rssItem.image, body.imageTitle, body.imageCredits, rssItem.description, rssItem.pubDate, body)
  }
}
