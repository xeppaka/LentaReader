package com.xeppaka.lentaruserver

import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 10/11/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
class LentaRssHandler extends DefaultHandler {
  override def startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
    println("START ELEMENT: " + localName);
  }

  override def endElement(uri: String, localName: String, qName: String) {
    println("END ELEMENT: " + localName);
  }
}
