package com.xeppaka.lentaruserver

import scala.util.{Failure, Success, Try}
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import com.xeppaka.lentaruserver.exceptions.StatusCodeException
import java.io.{InputStreamReader, BufferedReader}
import org.apache.http.util.EntityUtils
import java.util.logging.{SimpleFormatter, StreamHandler, Logger, Level}
import org.apache.http.impl.client.HttpClients

/**
 * Created by nnm on 1/16/14.
 */
object Downloader {
  val logger = Logger.getLogger(Downloader.getClass.getName)
  logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()))

  val client = HttpClients.createDefault()

  def download(url: String): Option[String] = {
    Try {
      val httpGet = new HttpGet(url)
      httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:12.0) Gecko/20100101 Firefox/21.0")
      val response = client.execute(httpGet)

      try {
        val statusCode = response.getStatusLine.getStatusCode
        if (statusCode != HttpStatus.SC_OK)
          throw new StatusCodeException(statusCode)

        val entity = response.getEntity()

        val bis = new BufferedReader(new InputStreamReader(entity.getContent, "UTF-8"))
        val result = new StringBuilder
        var line: String = null
        var finished: Boolean = false

        while (!finished) {
          line = bis.readLine()

          if (line != null)
            result.append(line)
          else
            finished = true
        }

        EntityUtils.consume(entity)

        result.toString()
      } finally {
        response.close()
      }
    } match {
      case Success(page) => Some(page)
      case Failure(e) => {logger.log(Level.ALL, s"Error while loading page: $url", e); None}
    }
  }
}

class Downloader {}
