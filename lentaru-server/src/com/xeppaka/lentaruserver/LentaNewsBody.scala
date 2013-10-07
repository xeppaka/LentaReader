package com.xeppaka.lentaruserver

import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAnyElement
import scala.annotation.meta.field
import scala.io.Source

@XmlRootElement(name = "body")
@XmlAccessorType(XmlAccessType.FIELD)
class LentaNewsBody(@(XmlAnyElement @field) val bodyitems: Array[_ <: LentaBodyItem]) {
  
}

object LentaNewsBody {
  def create(url: String) = {
    val page = Source.fromURL(url, "UTF-8").mkString
    "rrr".r.findAllIn(page)
  }
}