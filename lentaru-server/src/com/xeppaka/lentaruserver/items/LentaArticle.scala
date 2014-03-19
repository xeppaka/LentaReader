package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.items.body.{LentaArticleBody, LentaBody}
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
class LentaArticle(
  guid: String,
  title: String,
  val secondTitle: String,
  link: String,
  image: String,
  imageTitle: String,
  imageCredits: String,
  description: String,
  val author: String,
  pubDate: Long,
  rubric: Rubrics,
  body: LentaBody) extends LentaNews(guid, title, link, image, imageTitle, imageCredits, description, pubDate, rubric, body)
{
  override def toString() = s"LentaArticle[guid=$guid, title=$title, link=$link, description=$description, pubDate=$pubDate, rubric = $rubric, body=$body]"
  override def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentaarticle>\n")

    builder.append(s"$indentInternal<guid>$guid</guid>\n")
    builder.append(s"$indentInternal<title>${StringEscapeUtils.escapeXml(title)}</title>\n")
    builder.append(s"$indentInternal<secondTitle>${StringEscapeUtils.escapeXml(secondTitle)}</secondTitle>\n")
    builder.append(s"$indentInternal<link>$link</link>\n")
    builder.append(s"$indentInternal<image>$image</image>\n")
    builder.append(s"$indentInternal<imageTitle>${StringEscapeUtils.escapeXml(imageTitle)}</imageTitle>\n")
    builder.append(s"$indentInternal<imageCredits>${StringEscapeUtils.escapeXml(imageCredits)}</imageCredits>\n")
    builder.append(s"$indentInternal<description>${CDataEscaper.escapeText(description)}</description>\n")
    builder.append(s"$indentInternal<author>$author</author>\n")
    builder.append(s"$indentInternal<pubDate>$pubDate</pubDate>\n")
    builder.append(s"$indentInternal<rubric>$rubric</rubric>\n")
    builder.append(body.toXml(indentInternal))
    builder.append(s"$indent</lentaarticle>\n").toString
  }

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + super.hashCode
    hash = hash * 31 + secondTitle.hashCode
    hash = hash * 31 + author.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaArticle])
      false

    val other = obj.asInstanceOf[LentaArticle]
    if (guid != other.guid || title != other.title || secondTitle != other.secondTitle || link != other.link || image != other.image ||
    imageTitle != other.imageTitle || imageCredits != other.imageCredits || description != other.description || author != other.author ||
    pubDate != other.pubDate || body != other.body)
      false

    true
  }
}

object LentaArticle {
  def apply(rssItem: RssItem): Option[LentaArticle] = {
    LentaArticleBody.downloadArticle(rssItem.link).map(articleBody => new LentaArticle(rssItem.guid, rssItem.title, articleBody.secondTitle, rssItem.link, rssItem.image, articleBody.imageTitle,
      articleBody.imageCredits, rssItem.description, rssItem.author, rssItem.pubDate, rssItem.rubric, articleBody))
  }
}
