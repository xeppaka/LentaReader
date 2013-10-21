package com.xeppaka.lentaruserver

import scala.xml.Node
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement

class LentaNewsItem extends LentaItem {
  //def body: LentaNewsBody = body

  override def toString() = {
    s"LentaNewsItem[guid=$guid, title=$title, pubDate=$pubDate, image=$image, description=$description]" 
  }
}

object LentaNewsItem {
  def apply(rssItem: Node): LentaNewsItem = {
    val newsItem = new LentaNewsItem()
    newsItem.guid = (rssItem \\ "guid").text
    newsItem.title = (rssItem \\ "title").text
    newsItem.description = (rssItem \\ "description").text
    newsItem
  }
}