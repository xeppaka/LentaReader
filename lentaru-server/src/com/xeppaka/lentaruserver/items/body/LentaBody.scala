package com.xeppaka.lentaruserver.items.body

import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAnyElement
import scala.annotation.meta.field
import scala.io.Source
import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.items.LentaItemBase
import com.xeppaka.lentaruserver.NewsType
import com.xeppaka.lentaruserver.NewsType.NewsType

class LentaBody(val items: Seq[_ <: LentaBodyItemBase]) extends LentaBodyItemBase {
  //override def toString =
  override def toXml(): String = {
    "123"
  }
}

object LentaBody {
  def downloadNews(url: String): LentaBody = {
    val page = Source.fromURL(url, "UTF-8").mkString
    new LentaBody(parseNews(page))
  }

  private def parseNews(page: String): Seq[LentaBodyItemBase] = {
    List(LentaBodyTextItem("test"))
  }
}