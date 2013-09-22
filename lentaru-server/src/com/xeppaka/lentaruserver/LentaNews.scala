package com.xeppaka.lentaruserver

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAnyElement
import javax.xml.bind.annotation.XmlRootElement
import scala.annotation.meta.field

@XmlRootElement(name = "lentanews")
@XmlAccessorType(XmlAccessType.FIELD)
class LentaNews(@(XmlAnyElement @field) val newslist: Array[_ <: LentaItem]) {
  def this() = this(Array.empty[LentaItem])
}