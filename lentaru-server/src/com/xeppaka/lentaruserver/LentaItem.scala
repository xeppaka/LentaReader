package com.xeppaka.lentaruserver

abstract class LentaItem (
  val guid: String = null,
  val title: String = null,
  val pubDate: Long = 0,
  val image: String = null,
  val description: String = null,
  val body: LentaItemBody) extends LentaItemBase {

  override def toString() = s"LentaItem[guid=$guid, title=$title, pubDate=$pubDate, image=$image, description=$description]"
}