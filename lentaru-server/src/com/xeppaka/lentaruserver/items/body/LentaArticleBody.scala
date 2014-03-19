package com.xeppaka.lentaruserver.items.body

import com.xeppaka.lentaruserver.items.ItemBase
import java.util.logging.{StreamHandler, Logger, SimpleFormatter}
import scala.concurrent._
import com.xeppaka.lentaruserver.Downloader
import scala.concurrent.duration._
import scala.Some
import java.net.URLDecoder
import scala.util.parsing.json.JSON
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 10/23/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
case class LentaArticleBody(secondTitle: String, imageTitle: String, imageCredits: String, items: List[ItemBase])
  extends LentaBody {

  override def hashCode(): Int = {
    var hash = 1
    hash = hash * 31 + secondTitle.hashCode
    hash = hash * 31 + imageTitle.hashCode
    hash = hash * 31 + imageCredits.hashCode
    hash = hash * 31 + items.hashCode

    hash
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[LentaArticleBody])
      false
    else {
      val other = obj.asInstanceOf[LentaArticleBody]
      secondTitle == other.secondTitle && imageTitle == other.imageTitle && imageCredits == other.imageCredits && items == other.items
    }
  }
}

object LentaArticleBody {
  val logger = Logger.getLogger(LentaArticleBody.getClass.getName)
  logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()))

  val SEPARATOR_PLACEHOLDER = "[[SEP]]"
  val ASIDE_PLACEHOLDER = "[[ASIDE]]"
  val IFRAME_PLACEHOLDER = "[[IFRAME]]"
  val ASIDE = SEPARATOR_PLACEHOLDER + ASIDE_PLACEHOLDER + SEPARATOR_PLACEHOLDER
  val IFRAME = SEPARATOR_PLACEHOLDER + IFRAME_PLACEHOLDER + SEPARATOR_PLACEHOLDER

  val SEPARATOR_PLACEHOLDER_REGEX: String = "\\[\\[SEP\\]\\]"

  val articleSecondTitlePattern = "<h\\d.+?class=\"b-topic__rightcol\">(.+?)</h2>".r;
  val imageTitlePattern = "<div.+?itemprop=\"description\">(.+?)</div>".r
  val imageCreditsPattern = "<div.+?itemprop=\"author\">.+?decodeURIComponent\\('(.+?)\'\\).+?</div>".r
  val newsBodyAsidePattern = "(?s)<aside.+?</aside>\\s*".r
  val newsBodyIframePattern = "(?s)<iframe.+?</iframe>\\s*".r
  val newsBodyPattern = "(?s)<div[^<>]+?itemprop=\"articleBody\">(.+?)</div>".r
  val newsBodyStartPattern = "(?s)<div[^<>]+?itemprop=\"articleBody\">".r
  val divPattern = "</?div>?".r

  val asideGalleryPattern = "(?s)data-box=\"(.+?)\"".r
  val iframeUrlPattern = "src=[\'\"](.+?)[\'\"]".r

  def downloadArticle(url: String): Option[LentaArticleBody] = {
    val f: Future[Option[LentaArticleBody]] = future { Downloader.download(url).flatMap(f => Some(parseNews(f))) }

    try {
      Await.result(f, Duration(10, SECONDS))
    } catch {
      case e: Exception => None
    }
  }

  def parseNews(page: String): LentaArticleBody = {
    val secondTitle = articleSecondTitlePattern.findFirstIn(page) match {
      case Some(articleSecondTitlePattern(title)) => title
      case None => ""
    }

    val imageTitle = imageTitlePattern.findFirstIn(page) match {
      case Some(imageTitlePattern(title)) => LentaBody.fixLinks(title)
      case None => ""
    }

    val imageCredits = imageCreditsPattern.findFirstIn(page) match {
      case Some(imageCreditsPattern(credits)) =>
        try {
          LentaBody.fixLinks(URLDecoder.decode(credits, "UTF-8"))
        } catch {
          case ex: Exception  => ""
        }
      case None => ""
    }

    findBody(page) match {
      case Some(body) =>
        val asides = newsBodyAsidePattern.findAllMatchIn(body).map(item => parseAside(item.matched)).toList
        val iframes = newsBodyIframePattern.findAllMatchIn(body).map(item => parseIframe(item.matched)).toList

        val newsWithoutAside = newsBodyAsidePattern.replaceAllIn(body, ASIDE)
        val newsWithoutMedia = newsBodyIframePattern.replaceAllIn(newsWithoutAside, IFRAME)
        val newsForParsing = LentaBody.fixLinks(newsWithoutMedia)

        val newsItems = parseBody(newsForParsing.split(SEPARATOR_PLACEHOLDER_REGEX).toList, asides, iframes)
        LentaArticleBody(secondTitle, imageTitle, imageCredits, newsItems)
      case None => LentaArticleBody(secondTitle, imageTitle, imageCredits, List[ItemBase]())
    }
  }

  private def findBody(page: String): Option[String] = {
    def findBodyCoundDiv(from: Int, divs: Int): Int = {
      if (divs == 0 && from <= page.length)
        from
      else if (from > page.length)
        page.length
      else {
        val substr = page.substring(from)
        divPattern.findFirstMatchIn(substr) match {
          case Some(mt) =>
            if (mt.matched.startsWith("</"))
              findBodyCoundDiv(from + mt.end, divs - 1)
            else
              findBodyCoundDiv(from + mt.end, divs + 1)
          case None => from
        }
      }
    }

    newsBodyStartPattern.findFirstMatchIn(page) match {
      case Some(mt) =>
        val end = findBodyCoundDiv(mt.end, 1) - 6 // 6 is the size of </div>
        if (end <= 0 || end <= mt.end)
          None
        else
          Some(page.substring(mt.end, end))
      case None => None
    }
  }

  private def parseBody(body: List[String], asides: List[Option[ItemBase]], iframes: List[Option[ItemBase]]): List[ItemBase] = {
    def parseBodyMergingText(body: List[String], mergedTextItem: Option[LentaBodyItemText], asides: List[Option[ItemBase]], iframes: List[Option[ItemBase]]): List[ItemBase] = {
      body match {
        case Nil => mergedTextItem match {
          case Some(textItem) => textItem :: Nil
          case None => Nil
        }
        case x :: xs => x match {
          case ASIDE_PLACEHOLDER => asides.head match {
            case Some(item) => mergedTextItem match {
              case Some(text) => text :: item :: parseBodyMergingText(body.tail, None, asides.tail, iframes)
              case None => item :: parseBodyMergingText(body.tail, None, asides.tail, iframes)
            }
            case None => parseBodyMergingText(body.tail, mergedTextItem, asides.tail, iframes)
          }
          case IFRAME_PLACEHOLDER => iframes.head match {
            case Some(item) => mergedTextItem match {
              case Some(text) => text :: item :: parseBodyMergingText(body.tail, None, asides, iframes.tail)
              case None => item :: parseBodyMergingText(body.tail, None, asides.tail, iframes)
            }
            case None => parseBodyMergingText(body.tail, mergedTextItem, asides, iframes.tail)
          }
          case _ =>
            mergedTextItem match {
              case Some(textItem) => parseBodyMergingText(body.tail, Some(textItem.merge(x)), asides, iframes)
              case None => parseBodyMergingText(body.tail, Some(new LentaBodyItemText(x)), asides, iframes)
            }
        }
      }
    }

    parseBodyMergingText(body, None, asides, iframes)
  }

  private def parseAside(aside: String): Option[ItemBase] = {
    if (aside.contains("b-inline-gallery-box")) {
      asideGalleryPattern.findFirstIn(aside) match {
        case Some(asideGalleryPattern(data)) =>
          parseImagesJson(data) match {
            case Some(images) => Some(LentaBodyItemGallery(images))
            case None => None
          }
        case None => None
      }
    } else {
      None
    }
  }

  private def parseImagesJson(imagesJson: String): Option[List[LentaBodyItemImage]] = {
    JSON.parseFull(imagesJson.replaceAll("&quot;", "\"")) match {
      case Some(jsondata) =>
        jsondata match {
          case x: List[Map[String, Any]] =>
            val result = x.map(item => {
              val preview_url = getJsonField(item, "preview_url")
              val original_url = getJsonField(item, "original_url")

              if (original_url.isDefined && preview_url.isDefined)
                Some(LentaBodyItemImage(preview_url.get, original_url.get, getJsonField(item, "caption"), getJsonField(item, "credits")))
              else
                None
            }).flatMap(item => item)

            if (result.isEmpty)
              None
            else
              Some(result)
          case _ => None
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
      case Some(iframeUrlPattern(src)) =>
        if (src.contains("youtube"))
          Some(LentaBodyItemVideo(src.replace("//", ""), VideoType.YOUTUBE))
        else
          None
      case None => None
    }
  }
}