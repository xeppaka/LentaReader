package com.xeppaka.lentaruserver

import _root_.com.xeppaka.lentaruserver.NewsType.NewsType
import _root_.com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.{Node, XML}
import java.net.URL
import com.xeppaka.lentaruserver.items.{ItemBase, RssItem, LentaItem}
import java.io.{File, PrintWriter}
import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaSnapshot(val newsType: NewsType, val rubric: Rubrics, val items: Seq[LentaItem]) extends ItemBase {
  override def toString() = s"[newsType=$newsType, rubrics=$rubric, items=$items]"

  def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"""$indent<lentasnapshot type="$newsType" rubric="$rubric">\n""")
    items.foreach((item) => builder.append(item.toXml(indentInternal)))
    builder.append(s"$indent</lentasnapshot>\n").toString()
  }

  def dropLastItem(): Option[(LentaSnapshot, LentaItem)] = {
    if (items.isEmpty)
      None
    else {
      val flitems = items.splitAt(items.length - 1)
      new Some((LentaSnapshot(newsType, rubric, flitems._1), flitems._2.head))
    }
  }

  def writeXml(fileName: Path) = {
    val out = new PrintWriter(fileName.toString, "UTF-8")
    try {
      out.println(toXml())
    } finally {
      out.close()
    }
  }

  def writeXmlSet(dir: Path) = {
    def writeXmls(cursnap: LentaSnapshot, filename: Path): Unit = {
      cursnap.writeXml(filename)
      val nextsnappair = cursnap.dropLastItem()
      nextsnappair match {
        case None => Unit
        case Some((nextsnap, lastitem)) => {
          val nextfilename = FileSystem.createFullPath(dir.toString, newsType, rubric, lastitem.pubDate.toString + ".xml")
          writeXmls(nextsnap, nextfilename)
        }
      }
    }

    writeXmls(this, FileSystem.createFullPath(dir.toString, newsType, rubric))
  }

  def isEmpty: Boolean = items.isEmpty
}

object LentaSnapshot {
  /* Lenta.ru constants */
  val RSS_PATH = "/rss"
  val LENTA_URL = "http://www.lenta.ru"

  /** Create URL to RSS from news type and rubric
    *
    * @param newsType is the type of news
    * @param rubric is the rubric
    * @return URL to RSS as a String
    */
  private def url(newsType: NewsType, rubric: Rubrics): String =
    LENTA_URL + RSS_PATH + newsType.path + rubric.path

  /** Download news with provided type and rubric default to ROOT
    *
    * @param newsType is the type of news
    * @return LentaSnapshot instance filled with downloaded and parsed pages
    */
  def download(newsType: NewsType): LentaSnapshot = {
    download(newsType, Rubrics.ROOT)
  }

  /** Download news with provided type and rubric
   *
   * @param newsType is the type of news
   * @param rubric is the rubric
   * @return LentaSnapshot instance filled with downloaded and parsed pages
   */
  def download(newsType: NewsType, rubric: Rubrics): LentaSnapshot = {
    val xml = XML.load(new URL(url(newsType, rubric)))
    val rssItems = xml \\ "item"

    val lentaItems = rssItems.map(item => LentaItem(newsType, RssItem(item))).filter(item => item.isDefined).map(item => item.get)

    LentaSnapshot(newsType, rubric, lentaItems)
  }

  def apply(newsType: NewsType, rubrics: Rubrics, items: Seq[LentaItem]): LentaSnapshot = new LentaSnapshot(newsType, rubrics, items)
}
