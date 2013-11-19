package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import scala.io.Source
import org.apache.commons.lang3.StringEscapeUtils
import java.io.IOException
import scala.util.{Success, Failure, Try}
import java.util.logging.{Level, Logger}
import scala.util.matching.Regex
import scala.util.parsing.json.JSON
import scala.xml.Utility
import java.net.{URLDecoder, URLEncoder, URL}

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
  val logger = Logger.getLogger(LentaBody.getClass.getName)

  val SEPARATOR_PLACEHOLDER = "[[SEP]]"
  val ASIDE_PLACEHOLDER = "[[ASIDE]]"
  val IFRAME_PLACEHOLDER = "[[IFRAME]]"
  val ASIDE = SEPARATOR_PLACEHOLDER + ASIDE_PLACEHOLDER + SEPARATOR_PLACEHOLDER
  val IFRAME = SEPARATOR_PLACEHOLDER + IFRAME_PLACEHOLDER + SEPARATOR_PLACEHOLDER

  val SEPARATOR_PLACEHOLDER_REGEX: String = "\\[\\[SEP\\]\\]"

  val imageTitlePattern = "<div.+?itemprop=\"description\">(.+?)</div>".r
  val imageCreditsPattern = "<div.+?itemprop=\"author\">.+?decodeURIComponent\\('(.+?)\'\\).+?</div>".r
  val newsBodyAsidePattern = "(?s)<aside .+?</aside>\\s*".r
  val newsBodyIframePattern = "(?s)<iframe.+?</iframe>\\s*".r
  val newsBodyPattern = "(?s)<div.+?itemprop=\"articleBody\">(.+?)</div>".r

  val asideGalleryPattern = "(?s)data-box=\"(.+?)\"".r
  val iframeUrlPattern = "src=[\'\"](.+?)[\'\"]".r

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
      case Some(imageCreditsPattern(credits)) => URLDecoder.decode(credits, "UTF-8")
      case None => ""
    }

    val asides = newsBodyAsidePattern.findAllMatchIn(page).map(item => parseAside(item.matched)).toList
    val iframes = newsBodyIframePattern.findAllMatchIn(page).map(item => parseIframe(item.matched)).toList

    val newsWithoutAside = newsBodyAsidePattern.replaceAllIn(page, ASIDE)
    val newsWithoutMedia = newsBodyIframePattern.replaceAllIn(newsWithoutAside, IFRAME)

    val newsBody = newsBodyPattern.findFirstIn(newsWithoutMedia) match {
      case Some(newsBodyPattern(body)) => {
        parseBody(body.split(SEPARATOR_PLACEHOLDER_REGEX).toList, asides, iframes)
      }
      case None => Nil
    }

    LentaNewsBody(imageTitle, imageCredits, newsBody)
  }

  private def parseBody(body: List[String], asides: List[Option[ItemBase]], iframes: List[Option[ItemBase]]): List[ItemBase] = {
    def parseBodyMergingText(body: List[String], prevTextItem: Option[LentaBodyItemText], asides: List[Option[ItemBase]], iframes: List[Option[ItemBase]]): List[ItemBase] = {
      body match {
        case Nil => Nil
        case x :: xs => x match {
          case ASIDE_PLACEHOLDER => asides.head match {
            case Some(item) => item :: parseBodyMergingText(body.tail, None, asides.tail, iframes)
            case None => parseBodyMergingText(body.tail, prevTextItem, asides.tail, iframes)
          }
          case IFRAME_PLACEHOLDER => iframes.head match {
            case Some(item) => item :: parseBodyMergingText(body.tail, None, asides, iframes.tail)
            case None => parseBodyMergingText(body.tail, prevTextItem, asides, iframes.tail)
          }
          case _ => {
            prevTextItem match {
              case Some(text) => {
                val newTextItem = text.merge(x)
                newTextItem :: parseBodyMergingText(body.tail, Some(newTextItem), asides, iframes)
              }
              case None => new LentaBodyItemText(x) :: parseBodyMergingText(body.tail, None, asides, iframes)
            }
          }
        }
      }
    }

    parseBodyMergingText(body, None, asides, iframes)
  }

  private def parseAside(aside: String): Option[ItemBase] = {
    if (aside.contains("b-inline-gallery-box")) {
      asideGalleryPattern.findFirstIn(aside) match {
        case Some(asideGalleryPattern(data)) => {
          parseImagesJson(data) match {
            case Some(images) => Some(LentaBodyItemGallery(images))
            case None => None
          }
        }
        case None => None
      }
    } else {
      None
    }
  }

  private def parseImagesJson(imagesJson: String): Option[List[LentaBodyItemImage]] = {
    JSON.parseFull(imagesJson.replaceAll("&quot;", "\"")) match {
      case Some(jsondata) => {
        jsondata match {
          case x: List[Map[String, Any]] => {
            val result = x.map(item => {
              val url = getJsonField(item, "original_url")

              url match {
                case Some(urldata) => Some(LentaBodyItemImage(urldata, getJsonField(item, "caption"), getJsonField(item, "credits")))
                case None => None
              }
            }).flatMap(item => item)

            if (result.isEmpty)
              None
            else
              Some(result)
          }
          case _ => None
        }
      }
      case None => None
    }
  }

  private def getJsonField(values: Map[String, Any], field: String): Option[String] = {
    values.get(field) match {
      case Some(v) => if (v == null) None else Some(v.toString)
      case None => None
    }
  }

  private def parseIframe(iframe: String): Option[ItemBase] = {
    iframeUrlPattern.findFirstIn(iframe) match {
      case Some(iframeUrlPattern(src)) => {
        if (src.contains("youtube"))
          Some(LentaBodyItemVideo(src.replace("//", ""), VideoType.YOUTUBE))
        else
          None
      }
      case None => None
    }
  }
}