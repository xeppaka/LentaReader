package com.xeppaka.lentaruserver

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/22/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
object Rubrics extends Enumeration {
  type Rubrics = RubricsValue

  class RubricsValue(val name: String, val path: String) extends Val {
    override def toString() = s"[name = $name, path = $path]"
  }

  object RubricsValue {
    def apply(name: String, path: String) = new RubricsValue(name, path)
  }

  val ROOT = RubricsValue("root", "")
  val RUSSIA = RubricsValue("russia", "/russia")
  val WORLD = RubricsValue("world", "/world")
  val USSR = RubricsValue("ussr", "/ussr")
  val ECONOMICS = RubricsValue("economics", "/economics")
  val SCIENCE = RubricsValue("science", "/science")
  val SPORT = RubricsValue("sport", "/sport")
  val CULTURE = RubricsValue("culture", "/culture")
  val MEDIA = RubricsValue("media", "/media")
  val LIFE = RubricsValue("life", "/life")
}
