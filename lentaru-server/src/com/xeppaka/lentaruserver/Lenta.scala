package com.xeppaka.lentaruserver

import com.xeppaka.lentaruserver.NewsType._
import com.xeppaka.lentaruserver.Rubrics._

/**
 * Created with IntelliJ IDEA.
 * User: nnm
 * Date: 11/2/13
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
object Lenta {
  /* Lenta.ru constants */
  val RSS_PATH = "/rss"
  val LENTA_URL = "http://www.lenta.ru"

  /** Create URL to RSS from news type and rubric
    *
    * @param newsType is the type of news
    * @param rubric is the rubric
    * @return URL to RSS as a String
    */
  def url(newsType: NewsType, rubric: Rubrics): String =
    LENTA_URL + RSS_PATH + newsType.path + rubric.path
}
