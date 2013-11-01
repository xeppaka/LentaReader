package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.items.body.LentaBody
import com.xeppaka.lentaruserver.CDataEscaper

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
    builder.append(s"$indentInternal<image>$image</image>\n")
    builder.append(s"$indentInternal<imageTitle>$imageTitle</imageTitle>\n")
    builder.append(s"$indentInternal<imageCredits>$imageCredits</imageCredits>\n")
    builder.append(s"$indentInternal<description>$description</description>\n")
    builder.append(s"$indentInternal<pubDate>$pubDate</pubDate>\n")
    builder.append(body.toXml(indentInternal))
    builder.append(s"$indent</lentanews>\n").toString()
  }
}

object LentaNews {
  def apply(rssItem: RssItem): Option[LentaNews] = {
    LentaBody.downloadNews(rssItem.link).map(newsBody => new LentaNews(rssItem.guid, rssItem.title, rssItem.link, rssItem.image, newsBody.imageTitle,
      newsBody.imageCredits, CDataEscaper.escapeText(rssItem.description), rssItem.pubDate, newsBody))
  }
}
