package com.xeppaka.lentaruserver

import java.net.URL

import scala.xml.XML
import javax.xml.bind.JAXBContext


object Main extends App {
  val xml = XML.load(new URL("http://lenta.ru/rss"))

//  val spf: SAXParserFactory = SAXParserFactory.newInstance()
//  val sp: SAXParser = spf.newSAXParser()
//  val xr: XMLReader = sp.getXMLReader()
//  xr.setContentHandler(new LentaRssHandler)
//  xr.parse()

  val items = (xml \\ "item").toArray
  val res = for (item <- items) yield LentaNewsItem(item)

  val nn = new LentaNews(res)

  val context = JAXBContext.newInstance(classOf[LentaNews], classOf[LentaNewsItem])
  context.createMarshaller().marshal(nn, System.out)

  val t: Test = new Test("123")
}