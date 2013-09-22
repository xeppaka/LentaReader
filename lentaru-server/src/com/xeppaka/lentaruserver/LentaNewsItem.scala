package com.xeppaka.lentaruserver

import scala.xml.Node
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "news")
class LentaNewsItem extends LentaItem {
  override def toString() = {
    s"LentaNewsItem[guid=$guid, title=$title, pubDate=$pubDate, image=$image, description=$description]" 
  }
}

object LentaNewsItem {
  def createItem(node: Node): LentaNewsItem = {
    val newsItem = new LentaNewsItem()
    newsItem.guid = (node \\ "guid").text
    newsItem.title = (node \\ "title").text
    newsItem.description = (node \\ "description").text
    newsItem
  }
}