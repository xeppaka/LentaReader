package com.xeppaka.lentaruserver

import java.net.URL

import scala.xml.XML

import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.XmlRootElement

object Main extends App {
  val xml = XML.load(new URL("http://lenta.ru/rss"))
  val items = (xml \\ "item").toArray
  val res = for (item <- items) yield LentaNewsItem.createItem(item)
  
  val nn = new LentaNews(res)
  
  val context = JAXBContext.newInstance(classOf[LentaNews], classOf[LentaNewsItem])
  context.createMarshaller().marshal(nn, System.out)
}