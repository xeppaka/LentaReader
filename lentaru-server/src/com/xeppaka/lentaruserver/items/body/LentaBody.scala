package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import scala.io.Source

abstract class LentaBody(val items: Seq[ItemBase]) extends ItemBase {
  override def toXml(): String = {
    val builder = new StringBuilder("<lentabody>\n")
    items.foreach((item) => builder.append(item.toXml()))
    builder.append("</lentabody>\n").toString()
  }
}

object LentaBody {
  def downloadNews(url: String): LentaBody = {
    val page = Source.fromURL(url, "UTF-8").mkString
    new LentaNewsBody("123", "456", parseNews(page))
  }

  private def parseNews(page: String): Seq[ItemBase] = {
    List(LentaBodyItemText("test"))
  }
}