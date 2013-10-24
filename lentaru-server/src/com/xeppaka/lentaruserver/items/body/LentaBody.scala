package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.{LentaNews, ItemBase}
import scala.io.Source

abstract class LentaBody(val items: Seq[ItemBase]) extends ItemBase {
  override def toXml(): String = {
    val builder = new StringBuilder("<lentabody>\n")
    items.foreach((item) => builder.append(item.toXml()))
    builder.append("</lentabody>\n").toString()
  }
}

object LentaBody {
  val imageTitlePattern = "<div.+?itemprop=\"description\">(.+?)</div>".r
  val imageCreditsPattern = "<div.+?itemprop=\"author\">(.+?)</div>".r
  val newsBodyAsidePattern = "(?s)<aside .+?</aside>\\s*".r
  val newsBodyPattern = "(?s)<div.+?itemprop=\"articleBody\">(.+?)</div>".r

  def downloadNews(url: String): LentaBody = {
    val page = Source.fromURL(url, "UTF-8").mkString
    parseNews(page)
  }

  private def parseNews(page: String): LentaBody = {
    val imageTitle = imageTitlePattern.findFirstIn(page) match {
      case Some(imageTitlePattern(title)) => title
      case None => null
    }

    val imageCredits = imageCreditsPattern.findFirstIn(page) match {
      case Some(imageCreditsPattern(credits)) => credits
      case None => null
    }

    val newsBodyWithoutAside = newsBodyAsidePattern.replaceAllIn(page, "[ASIDE]\n")
    val newsBody = newsBodyPattern.findFirstIn(newsBodyWithoutAside) match {
      case Some(newsBodyPattern(body)) => List(LentaBodyItemText(body))
      case None => Nil
    }

    LentaNewsBody(imageTitle, imageCredits, newsBody)
  }
}