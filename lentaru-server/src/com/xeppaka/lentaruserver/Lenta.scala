package com.xeppaka.lentaruserver

import _root_.com.xeppaka.lentaruserver.NewsType.NewsType
import _root_.com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.XML
import scala.xml.Node
import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */

class Lenta(val newsType: NewsType, val rubrics: Rubrics, val items: Seq[LentaItem]) extends LentaItemBase {
  override def toString() = s"[newsType = $newsType, rubrics = $rubrics, items = $items]"
  override def toXml() = ""
}

object Lenta {
  val RSS_PATH = "/rss"
  val LENTA_URL = "http://www.lenta.ru"

  private def url(newsType: NewsType, rubric: Rubrics): String =
    LENTA_URL + RSS_PATH + newsType.path + rubric.path

  def download(newsType: NewsType): Lenta = {
    download(newsType, Rubrics.ROOT)
  }

  def download(newsType: NewsType, rubric: Rubrics): Lenta = {
    val xml = XML.load(new URL(url(newsType, rubric)))
    val rssitems = xml \\ "item"

    val items = rssitems.foldLeft(List[LentaItem]())((a, b) => LentaItem.createFromRssNode(b) :: a)
    Lenta(newsType, rubric, items)
  }

  def apply(newsType: NewsType, rubrics: Rubrics, items: Seq[LentaItem]) = new Lenta(newsType, rubrics, items)
}
