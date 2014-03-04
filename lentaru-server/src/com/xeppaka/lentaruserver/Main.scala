package com.xeppaka.lentaruserver

import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.{Files, FileSystems}
import com.xeppaka.lentaruserver.items.RssSnapshot
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import scala.None
import java.util.logging.{SimpleFormatter, StreamHandler, Logger}
import java.io.{InputStreamReader, BufferedReader, BufferedInputStream}

object Main extends App {
  private val logger = Logger.getLogger(Main.getClass.getName)

  logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()))

  if (args.length >= 1) {
    println("Will use root folder: " + args(0))

    val rootPath = FileSystems.getDefault.getPath(args(0))
    FileSystem.createfs(rootPath)

    var snapshots = Map[Rubrics, LentaSnapshot]()

    while (true) {
      Rubrics.values.foreach(rubric => {
        val curdir = FileSystem.createFullPath(rootPath.toString, NewsType.NEWS, rubric)
        val curdir_tmp = FileSystem.createFullTmpPath(rootPath.toString, NewsType.NEWS, rubric)
        val curdir_tmp2 = FileSystem.createFullTmp2Path(rootPath.toString, NewsType.NEWS, rubric)
        val cursnapshot = snapshots.get(rubric)

        logger.info("Downloading rss snapshot for " + rubric.toString + "... ")
        val newrss = RssSnapshot.downloadRss(NewsType.NEWS, rubric)
        logger.info("Done")

        newrss match {
          case Some(rss) => {
            val newSnapshot = cursnapshot match {
              case Some(memsnapshot) => {
                // with zeroed seconds
                val oldestWOImage = (memsnapshot.oldestWithoutPicture(10) / 60000) * 60000

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
                snapshots = snapshots.updated(rubric, snap)

                logger.info("Writing xmls in " + curdir_tmp.toString + "... ")
                snap.writeXmlSet(curdir_tmp)
                logger.info("Done")

                logger.info("Creating gzipped xmls... ")
                FileSystem.gzip(curdir_tmp)
                logger.info("Done")

                logger.info("Swapping directories...")
                Files.move(curdir, curdir_tmp2)
                Files.move(curdir_tmp, curdir)
                Files.move(curdir_tmp2, curdir_tmp)
                logger.info("Done")

                logger.info("Deleting xmls in " + curdir_tmp.toString + "... ")
                FileSystem.clean(curdir_tmp)
                logger.info("Done")
              }
              case None =>
            }
          }

          case None =>
        }
      })

      logger.info("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}
