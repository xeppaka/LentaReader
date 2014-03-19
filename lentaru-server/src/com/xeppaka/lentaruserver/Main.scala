package com.xeppaka.lentaruserver

import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.{Files, FileSystems}
import com.xeppaka.lentaruserver.items.RssSnapshot
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.None
import java.util.logging.{SimpleFormatter, StreamHandler, Logger}
import java.io.{InputStreamReader, BufferedReader, BufferedInputStream}
import com.xeppaka.lentaruserver.NewsType.NewsType

object Main extends App {
  private val logger = Logger.getLogger(Main.getClass.getName)

  logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()))

  if (args.length >= 1) {
    println("Will use root folder: " + args(0))

    val rootPath = FileSystems.getDefault.getPath(args(0))
    FileSystem.createfs(rootPath)

    var snapshots = Map[NewsType, Map[Rubrics, LentaSnapshot]]()
    NewsType.values.foreach(newstype => snapshots = snapshots.updated(newstype, Map[Rubrics, LentaSnapshot]()))

    while (true) {
      NewsType.values.foreach(newstype => {
      Rubrics.values.foreach(rubric => {
        val curdir = FileSystem.createFullPath(rootPath.toString, newstype, rubric)
        val cursnapshot = snapshots.get(newstype).get.get(rubric)

        logger.info("Downloading rss snapshot for " + rubric.toString + "... ")
        val newrss = RssSnapshot.downloadRss(newstype, rubric)
        logger.info("Done")

        newrss match {
          case Some(rss) => {
            val newSnapshot = cursnapshot match {
              case Some(memsnapshot) => {
                val oldestDate = memsnapshot.oldestWithoutPicture(10)

                // -1 means we don't have without images. Otherwise we clear seconds in oldestDate
                val oldestWOImage = if (oldestDate == -1) memsnapshot.latestDate() + 1 else (oldestDate / 60000) * 60000

                logger.info("Downloaded rss lentgh: " + rss.items.length + " items")
                logger.info("Oldest without image: " + oldestWOImage)
                val newRssItems = rss.newerOrEqualThan(oldestWOImage)

                if (newRssItems.isEmpty) {
                  logger.info("No new items found. Skipping...")
                  None
                } else {
                  logger.info("First 10 items dates: \n")
                  rss.items.take(10).foreach(item => println(item.pubDate))
                  println("")

                  logger.info("Downloading new " + newRssItems.items.length + " items... " )
                  val newSnapshot = LentaSnapshot.download(newRssItems).merge(memsnapshot)
                  logger.info("Done")
                  Some(newSnapshot)
                }
              }
              case None => {
                logger.info("Downloading and parsing " + rss.items.length + " items (complete rss)... " )
                val newSnapshot = LentaSnapshot.download(rss)
                logger.info("Done")
                Some(newSnapshot)
              }
            }

            newSnapshot match {
              case Some(snap) => {
                snapshots = snapshots.updated(newstype, snapshots.get(newstype).get.updated(rubric, snap))

                logger.info("Deleting xmls in " + curdir.toString + "... ")
                FileSystem.clean(curdir)
                logger.info("Done")

                logger.info("Writing xmls in " + curdir.toString + "... ")
                snap.writeXmlSet(curdir)
                logger.info("Done")

                logger.info("Creating gzipped xmls... ")
                FileSystem.gzip(curdir)
                logger.info("Done")

              }
              case None =>
            }
          }

          case None =>
        }
      })})

      logger.info("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}
