package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.XML
import java.util.logging.{SimpleFormatter, StreamHandler, Logger}
import com.xeppaka.lentaruserver.{Lenta, Downloader}
import scala.concurrent.{Await, Future, future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

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
    val newItems = items.filter(item => item.pubDate >= date)
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
  logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()))

  val MAX_ITEMS = 40

  def downloadRss(newsType: NewsType, rubric: Rubrics): Option[RssSnapshot] = {
    val url = Lenta.url(newsType, rubric)

    val f: Future[Option[RssSnapshot]] = future { Downloader.download(url).flatMap(v => {
        val xml = XML.loadString(v)
        val rawRssItems = xml \\ "item"
        val rssItems = rawRssItems.map(item => RssItem(item)).toList.take(MAX_ITEMS)

        Some(RssSnapshot(newsType, rubric, rssItems))
      })
    }

    Await.result(f, Duration(10, SECONDS))
  }

  def apply(newsType: NewsType, rubric: Rubrics, rssItems: List[RssItem]): RssSnapshot = new RssSnapshot(newsType, rubric, rssItems)
}