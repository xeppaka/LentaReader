package com.xeppaka.lentaruserver

import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.{Files, FileSystems}
import com.xeppaka.lentaruserver.items.RssSnapshot
import com.xeppaka.lentaruserver.Rubrics.Rubrics

object Main extends App {
  if (args.length >= 1) {
    println("Will use root folder: " + args(0))

    val rootPath = FileSystems.getDefault.getPath(args(0))
    FileSystem.createfs(rootPath)

    var snapshots = Map[Rubrics, LentaSnapshot]()

    while (true) {
      Rubrics.values.foreach(rubric => {
        val curdir = FileSystem.createFullPath(rootPath.toString, NewsType.NEWS, rubric)
        val cursnapshot = snapshots.get(rubric)

        print("Downloading rss snapshot for " + rubric.toString + "... ")
        val rss = RssSnapshot.downloadRss(NewsType.NEWS, rubric)
        println("Done")

        val newSnapshot = cursnapshot match {
          case Some(memsnapshot) => {
            val newRssItems = rss.olderThan(memsnapshot.oldestWithoutPicture(5))

            if (newRssItems.isEmpty) {
              println("No new items found. Skipping...")
              None
            }
            else {
              print("Downloading new " + newRssItems.items.length + " items... " )
              val newSnapshot = LentaSnapshot.download(newRssItems).merge(memsnapshot)
              println("Done")
              Some(newSnapshot)
            }
          }
          case None => {
            print("Downloading and parsing " + rss.items.length + " items (complete rss)... " )
            val newSnapshot = LentaSnapshot.download(rss)
            println("Done")
            Some(newSnapshot)
          }
        }

        newSnapshot match {
          case Some(snap) => {
            snapshots = snapshots.updated(rubric, snap)

            print("Deleting xmls in " + curdir.toString + "... ")
            FileSystem.clean(curdir)
            println("Done")

            print("Writing xmls in " + curdir.toString + "... ")
            snap.writeXmlSet(curdir)
            println("Done")
          }
          case None =>
        }
      })

      println("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}
