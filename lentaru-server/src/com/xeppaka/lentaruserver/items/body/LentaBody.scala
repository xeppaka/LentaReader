package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.{LentaNews, ItemBase}
import scala.io.Source

abstract class LentaBody(val items: Seq[ItemBase]) extends ItemBase {
  override def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentabody>\n")
    items.foreach((item) => builder.append(item.toXml(indentInternal)))
    builder.append(s"$indent</lentabody>\n").toString()
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

//    val newsAsides = newsBodyAsidePattern.findAllMatchIn(page)
//    newsAsides.foreach(item => println(item))

    //val newsBodyWithoutAside = newsBodyAsidePattern.replaceAllIn(page, "[ASIDE]\n")
    val newsBodyWithoutAside = newsBodyAsidePattern.replaceAllIn(page, "")
    val newsBody = newsBodyPattern.findFirstIn(newsBodyWithoutAside) match {
      case Some(newsBodyPattern(body)) => List(LentaBodyItemText(body))
      case None => Nil
    }

    LentaNewsBody(imageTitle, imageCredits, newsBody)
  }
}