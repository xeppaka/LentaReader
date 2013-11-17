package com.xeppaka.lentaruserver

import scala.language.implicitConversions

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
object Rubrics extends Enumeration {
  type Rubrics = RubricsValue

  class RubricsValue(val name: String, val path: String, val russianText: String) extends Val

  object RubricsValue {
    def apply(name: String, path: String, russianText: String) = new RubricsValue(name, path, russianText)
  }

  implicit def valueToRubricsValue(x: Value) = x.asInstanceOf[RubricsValue]

  val WORLD = RubricsValue("world", "/world", "Мир")
  val ROOT = RubricsValue("root", "", "")
  val RUSSIA = RubricsValue("russia", "/russia", "Россия")
//  val WORLD = RubricsValue("world", "/world", "Мир")
  val USSR = RubricsValue("ussr", "/ussr", "Бывший СССР")
  val ECONOMICS = RubricsValue("economics", "/economics", "Экономика")
  val SCIENCE = RubricsValue("science", "/science", "Наука и техника")
  val SPORT = RubricsValue("sport", "/sport", "Спорт")
  val CULTURE = RubricsValue("culture", "/culture", "Культура")
  val MEDIA = RubricsValue("media", "/media", "Интернет и СМИ")
  val LIFE = RubricsValue("life", "/life", "Жизнь")

  def getRubric(russianCategory: String): Rubrics = {
    values.find(item => valueToRubricsValue(item).russianText == russianCategory) match {
      case None => ROOT
      case Some(category) => category
    }
  }
}
