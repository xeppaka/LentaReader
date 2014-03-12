package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.items.body.{LentaNewsBody, LentaBody}
import com.xeppaka.lentaruserver.CDataEscaper
import org.apache.commons.lang3.StringEscapeUtils
import com.xeppaka.lentaruserver.Rubrics.Rubrics

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
  rubric: Rubrics,
  body: LentaBody) extends LentaItem(guid, title, link, image, imageTitle, imageCredits, description, pubDate, rubric, body)
{
  override def toString() = s"LentaNews[guid=$guid, title=$title, link=$link, description=$description, pubDate=$pubDate, rubric = $rubric, body=$body]"
  def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentanews>\n")

    builder.append(s"$indentInternal<guid>$guid</guid>\n")
    builder.append(s"$indentInternal<title>${StringEscapeUtils.escapeXml(title)}</title>\n")
    builder.append(s"$indentInternal<link>$link</link>\n")
    builder.append(s"$indentInternal<image>$image</image>\n")
    builder.append(s"$indentInternal<imageTitle>${StringEscapeUtils.escapeXml(imageTitle)}</imageTitle>\n")
    builder.append(s"$indentInternal<imageCredits>${StringEscapeUtils.escapeXml(imageCredits)}</imageCredits>\n")
    builder.append(s"$indentInternal<description>${CDataEscaper.escapeText(description)}</description>\n")
    builder.append(s"$indentInternal<pubDate>$pubDate</pubDate>\n")
    builder.append(s"$indentInternal<rubric>$rubric</rubric>\n")
    builder.append(body.toXml(indentInternal))
    builder.append(s"$indent</lentanews>\n").toString
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + guid.hashCode
    hash = hash * 31 + title.hashCode
    hash = hash * 31 + link.hashCode
    hash = hash * 31 + image.hashCode
    hash = hash * 31 + imageTitle.hashCode
    hash = hash * 31 + imageCredits.hashCode
    hash = hash * 31 + description.hashCode
    hash = hash * 31 + pubDate.hashCode
    hash = hash * 31 + body.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaNews])
      false

    val other = obj.asInstanceOf[LentaNews]
    if (guid != other.guid || title != other.title || link != other.link || image != other.image ||
    imageTitle != other.imageTitle || imageCredits != other.imageCredits || description != other.description ||
    pubDate != other.pubDate || body != other.body)
      false

    true
  }
}

object LentaNews {
  def apply(rssItem: RssItem): Option[LentaNews] = {
    LentaNewsBody.downloadNews(rssItem.link).map(newsBody => new LentaNews(rssItem.guid, rssItem.title, rssItem.link, rssItem.image, newsBody.imageTitle,
      newsBody.imageCredits, rssItem.description, rssItem.pubDate, rssItem.rubric, newsBody))
  }
}
