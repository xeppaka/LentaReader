package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import java.util.logging.{SimpleFormatter, StreamHandler, Logger}
import scala.util.parsing.json.JSON
import java.net.URLDecoder
import com.xeppaka.lentaruserver.Downloader
import scala.concurrent.{Await, Future, future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex.Match

abstract class LentaBody extends ItemBase {
  val items: List[ItemBase]

  override def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentabody>\n")
    items.foreach((item) => builder.append(item.toXml(indentInternal)))
    builder.append(s"$indent</lentabody>\n").toString()
  }
}

object LentaBody {
  val hrefPattern = "href=\"(.+?)\"".r

  def fixLinks(body: String): String = {
    val replacer = (v: Match) => {
      if (!v.group(1).startsWith("http")) {
        val sb = new StringBuilder(v.group(1))
        while (sb.size > 0 && (sb.charAt(0) == '/' || sb.charAt(0) == ':'))
          sb.deleteCharAt(0)

        "href=\"http://" + sb + "\""
      }
      else v.group(0)
    }

    hrefPattern.replaceAllIn(body, replacer)
  }
}