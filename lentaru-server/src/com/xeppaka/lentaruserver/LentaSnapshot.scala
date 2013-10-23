package com.xeppaka.lentaruserver

import _root_.com.xeppaka.lentaruserver.NewsType.NewsType
import _root_.com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.XML
import java.net.URL
import com.xeppaka.lentaruserver.items.{RssItem, LentaItemBase, LentaItem}

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaSnapshot(val newsType: NewsType, val rubrics: Rubrics, val items: Seq[LentaItem]) extends LentaItemBase {
  override def toString() = s"[newsType = $newsType, rubrics = $rubrics, items = $items]"
  override def toXml() = ""
}

object LentaSnapshot {
  val RSS_PATH = "/rss"
  val LENTA_URL = "http://www.lenta.ru"

  private def url(newsType: NewsType, rubric: Rubrics): String =
    LENTA_URL + RSS_PATH + newsType.path + rubric.path

  def download(newsType: NewsType): LentaSnapshot = {
    download(newsType, Rubrics.ROOT)
  }

  def download(newsType: NewsType, rubric: Rubrics): LentaSnapshot = {
    val xml = XML.load(new URL(url(newsType, rubric)))
    val rssRawItems = xml \\ "item"

    val rssItems = rssRawItems.foldLeft(List[RssItem]())((a, b) => RssItem(b) :: a)
    val lentaItems = rssItems.map((item) => LentaItem(newsType, item))

    LentaSnapshot(newsType, rubric, lentaItems)
  }

  def apply(newsType: NewsType, rubrics: Rubrics, items: Seq[LentaItem]): LentaSnapshot = new LentaSnapshot(newsType, rubrics, items)
}
