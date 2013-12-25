package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.XML
import java.net.URL
import com.xeppaka.lentaruserver.Lenta
import scala.util.{Success, Failure, Try}
import java.util.logging.{Level, Logger}

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 11/2/13
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */
class RssSnapshot(val newsType: NewsType, val rubric: Rubrics, val items: List[RssItem]) {
  def newerThan(date: Long): RssSnapshot = {
    val newItems = items.filter(_.pubDate > date)
    RssSnapshot(newsType, rubric, newItems)
  }

  def newerOrEqualThan(date: Long): RssSnapshot = {
    val newItems = items.filter(_.pubDate >= date)
    RssSnapshot(newsType, rubric, newItems)
  }

  def latestDate: Option[Long] = {
    items match {
      case Nil => None
      case x :: xs => Some(x.pubDate)
    }
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[RssSnapshot])
      false

    val other = obj.asInstanceOf[RssSnapshot]
    if (newsType != other.newsType || rubric != other.rubric || items != other.items)
      false

    true
  }

  override def hashCode(): Int = {
    var hash: Int = 1
    hash = hash * 31 + newsType.hashCode
    hash = hash * 31 + rubric.hashCode
    hash = hash * 31 + items.hashCode

    hash
  }

  def isEmpty(): Boolean = items.isEmpty
}

object RssSnapshot {
  val logger = Logger.getLogger(RssSnapshot.getClass.getName)
  val MAX_ITEMS = 40

  def downloadRss(newsType: NewsType, rubric: Rubrics): Option[RssSnapshot] = {
    val url = Lenta.url(newsType, rubric)

    Try {
      val xml = XML.load(new URL(url))
      val rawRssItems = xml \\ "item"

      val rssItems = rawRssItems.map(item => RssItem(item)).toList.take(MAX_ITEMS)

      RssSnapshot(newsType, rubric, rssItems)
    } match {
      case Success(v) => Some(v)
      case Failure(e) => { logger.log(Level.ALL, s"Error while loading page: $url", e); None }
    }
  }

  def apply(newsType: NewsType, rubric: Rubrics, rssItems: List[RssItem]): RssSnapshot = new RssSnapshot(newsType, rubric, rssItems)
}