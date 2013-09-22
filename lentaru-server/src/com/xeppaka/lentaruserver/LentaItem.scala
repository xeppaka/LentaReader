package com.xeppaka.lentaruserver

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType

@XmlAccessorType(XmlAccessType.FIELD)
trait LentaItem {
  var guid: String = null
  var title: String = null
  var pubDate: Long = 0
  var image: String = null
  var description: String = null

  override def toString() = {
    s"LentaItem[guid=$guid, title=$title, pubDate=$pubDate, image=$image, description=$description]" 
  }
}