package com.xeppaka.lentaruserver

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/30/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
object CDataEscaper {
  def escapeText(text: String): String = new StringBuilder("<![CDATA[").append(text).append("]]>").toString
}
