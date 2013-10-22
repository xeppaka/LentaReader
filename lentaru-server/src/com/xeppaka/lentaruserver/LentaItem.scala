package com.xeppaka.lentaruserver

import scala.xml.Node

case class LentaItem (
  val guid: String,
  val title: String = null,
  val pubDate: Long = 0,
  val image: String = null,
  val description: String = null,
  val link: String = null,
  val body: LentaBody) extends LentaItemBase {

  override def toString() = s"LentaItem[guid=$guid, title=$title, pubDate=$pubDate, image=$image, description=$description]"
  override def toXml() = ""
}

object LentaItem {
  def createFromRssItem(node: Node): LentaItem = {
    val guid = (node \\ "guid").text
    val title = (node \\ "title").text
    val description = (node \\ "description").text
    val link = (node \\ "link").text

    new LentaItem(guid, title, 0, null, description, link, null)
  }
}