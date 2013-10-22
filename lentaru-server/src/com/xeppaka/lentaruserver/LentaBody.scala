package com.xeppaka.lentaruserver

import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAnyElement
import scala.annotation.meta.field
import scala.io.Source

class LentaBody(val items: Seq[_ <: LentaBodyItem]) extends LentaItemBase {
  override def toString =
  override def toXml(): String = {
    "123"
  }
}

object LentaBody {
  def createFromUrl(url: String): LentaBody = {
    val page = Source.fromURL(url, "UTF-8").mkString
    null
  }
}