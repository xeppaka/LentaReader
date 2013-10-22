package com.xeppaka.lentaruserver

import java.net.URL

import scala.xml.XML
import javax.xml.bind.JAXBContext


object Main extends App {
  val nn = Lenta.download(NewsType.NEWS)

  println(nn)
}