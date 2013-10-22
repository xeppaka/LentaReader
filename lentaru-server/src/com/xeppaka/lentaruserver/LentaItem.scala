package com.xeppaka.lentaruserver

import scala.xml.Node

class LentaItem (
  val guid: String,
  val title: String,
  val pubDate: Long,
  val image: String,
  val description: String,
  val link: String,
  val body: LentaBody) extends LentaItemBase {

  override def toString() = s"LentaItem[guid=$guid, title=$title, pubDate=$pubDate, image=$image, description=$description, link=$link, body=$body]"
  override def toXml() = ""
}

object LentaItem {
  def createFromRssNode(node: Node): LentaItem = {
    val guid = (node \\ "guid").text
    val title = (node \\ "title").text
    val description = (node \\ "description").text
    val link = (node \\ "link").text

    new LentaItem(guid, title, 0, null, description, link, null)
  }
}