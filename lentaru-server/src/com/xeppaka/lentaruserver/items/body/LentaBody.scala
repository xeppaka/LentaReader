package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import scala.io.Source
import org.apache.commons.lang3.StringEscapeUtils
import java.io.IOException
import scala.util.{Success, Failure, Try}
import java.util.logging.{Level, Logger}

abstract class LentaBody(val items: Seq[ItemBase]) extends ItemBase {
  override def toXml(indent: String): String = {
    val indentInternal = indent + "  "
    val builder = new StringBuilder(s"$indent<lentabody>\n")
    items.foreach((item) => builder.append(item.toXml(indentInternal)))
    builder.append(s"$indent</lentabody>\n").toString()
  }
}

object LentaBody {
  val logger = Logger.getLogger(LentaBody.getClass.getName)

  val imageTitlePattern = "<div.+?itemprop=\"description\">(.+?)</div>".r
  val imageCreditsPattern = "<div.+?itemprop=\"author\">(.+?)</div>".r
  val newsBodyAsidePattern = "(?s)<aside .+?</aside>\\s*".r
  val newsBodyPattern = "(?s)<div.+?itemprop=\"articleBody\">(.+?)</div>".r

  def downloadNews(url: String): Option[LentaNewsBody] = {
    Try(Source.fromURL(url, "UTF-8").mkString) match {
      case Success(page) => Some(parseNews(page))
      case Failure(e) => {logger.log(Level.ALL, s"Error while loading page: $url", e); None}
    }
  }

  private def parseNews(page: String): LentaNewsBody = {
    val imageTitle = imageTitlePattern.findFirstIn(page) match {
      case Some(imageTitlePattern(title)) => title
      case None => ""
    }

    val imageCredits = imageCreditsPattern.findFirstIn(page) match {
      case Some(imageCreditsPattern(credits)) => credits.replaceAll("(<!--) | (-->)", "")
      case None => ""
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