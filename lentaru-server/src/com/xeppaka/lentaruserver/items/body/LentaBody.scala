package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import java.util.logging.{SimpleFormatter, StreamHandler, Logger}
import scala.util.parsing.json.JSON
import java.net.URLDecoder
import com.xeppaka.lentaruserver.Downloader
import scala.concurrent.{Await, Future, future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class LentaBody extends ItemBase {
  val items: List[ItemBase]

  override def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentabody>\n")
    items.foreach((item) => builder.append(item.toXml(indentInternal)))
    builder.append(s"$indent</lentabody>\n").toString()
  }
}