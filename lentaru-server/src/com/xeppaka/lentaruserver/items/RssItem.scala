package com.xeppaka.lentaruserver.items

import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.xml.Node
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/23/13
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
class RssItem(
//  val newsType: NewsType,
//  val rubrics: Rubrics,
  val guid: String,
  val title: String,
  val author: String,
  val link: String,
  val description: String,
  val pubDate: Long)
{
  override def toString() = s"RssItem[guid=$guid, title=$title, author=$author, link=$link, description=$description, pubDate=$pubDate]"
}

object RssItem {
  val rssDatePattern = "EEE, dd MMM yyyy HH:mm:ss Z"
  val dateFormat = new SimpleDateFormat(rssDatePattern, Locale.US)

  def apply(rssnode: Node): RssItem = {
    val guid = (rssnode \\ "guid").text
    val title = (rssnode \\ "title").text
    val author = (rssnode \\ "author").text
    val link = (rssnode \\ "link").text
    val description = (rssnode \\ "description").text
    val pubDate = dateFormat.parse((rssnode \\ "pubDate").text)

    new RssItem(guid, title, author, link, description, pubDate.getTime)
  }
}