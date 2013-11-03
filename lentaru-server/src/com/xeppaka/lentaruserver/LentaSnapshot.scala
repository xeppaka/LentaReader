package com.xeppaka.lentaruserver

import _root_.com.xeppaka.lentaruserver.NewsType.NewsType
import _root_.com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.{Node, XML}
import java.net.URL
import com.xeppaka.lentaruserver.items._
import java.io.{File, PrintWriter}
import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.Path
import scala.Some
import com.xeppaka.lentaruserver.items.RssSnapshot

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaSnapshot(val newsType: NewsType, val rubric: Rubrics, val items: List[LentaItem]) extends ItemBase {
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
          val nextfilename = dir.resolve(lastitem.pubDate.toString + ".xml")
          writeXmls(nextsnap, nextfilename)
        }
      }
    }

    writeXmls(this, dir.resolve(FileSystem.FILENAME_ROOT_SNAPSHOT))
  }

  def latestDate(): Long = {
    if (isEmpty) 0 else items.head.pubDate
  }

  def merge(other: LentaSnapshot): LentaSnapshot = {
    if (other.newsType != newsType || other.rubric != rubric)
      throw new IllegalArgumentException("Other snapshot has not the same news type and/or rubric")

    val newItems: List[LentaItem] = (other.items ::: items).sortWith(_.pubDate > _.pubDate)

    LentaSnapshot(newsType, rubric, newItems)
  }

  def isEmpty: Boolean = items.isEmpty

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaSnapshot])
      false

    val other = obj.asInstanceOf[LentaSnapshot]
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
}

object LentaSnapshot {
  /** Download full news from previously downloaded RSS snapshot
   *
   * @param rss is the previously downloaded RSS snapshot
   * @return LentaSnapshot instance filled with downloaded and parsed news
   */
  def download(rss: RssSnapshot): LentaSnapshot = {
    val lentaItems = rss.items.map(item => LentaItem(rss.newsType, item)).filter(item => item.isDefined).map(item => item.get)

    LentaSnapshot(rss.newsType, rss.rubric, lentaItems)
  }

  def apply(newsType: NewsType, rubrics: Rubrics, items: List[LentaItem]): LentaSnapshot = new LentaSnapshot(newsType, rubrics, items)
}
