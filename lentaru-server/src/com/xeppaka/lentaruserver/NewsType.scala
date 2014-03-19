package com.xeppaka.lentaruserver

import scala.language.implicitConversions

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
object NewsType extends Enumeration {
  type NewsType = NewsTypeValue

  class NewsTypeValue(val path: String) extends Val

  object NewsTypeValue {
    def apply(path: String) = new NewsTypeValue(path)
  }

  implicit def valueToNewsTypeValue(x: Value) = x.asInstanceOf[NewsTypeValue]

  val NEWS = NewsTypeValue("/news")
  val ARTICLE = NewsTypeValue("/articles")
//  val COLUMN = NewsTypeValue("/column")
//  val PHOTO = NewsTypeValue("/photo")
//  val VIDEO = NewsTypeValue("/video")
}
