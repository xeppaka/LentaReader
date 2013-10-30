package com.xeppaka.lentaruserver

import java.io.{PrintWriter, File}
import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import com.xeppaka.lentaruserver.items.LentaItem

object Main extends App {
  def createFolder(path: String): Boolean = {
    val f: File = new File(path)
    if (!f.exists())
      f.mkdir()
    else
      false
  }

  def createRubrics(dir: String): List[String] = {
    Rubrics.values.foldLeft(List[String]())((z, item) => {
      val path = dir + File.separator + item.toString
      if (createFolder(path)) path :: z else z
    })
  }

  def createNews(dir: String): List[String] = {
    NewsType.values.foldLeft(List[String]())((z, item) => {
      val path = dir + File.separator + item.toString
      if (createFolder(path)) path :: z else z
    })
  }

  def createfs(dir: String) = {
    val listNewsFolders = createNews(dir)
    listNewsFolders.foreach(item => createRubrics(item))
  }

  def cleanDir(dir: String): Unit = {
    val f: File = new File(dir)

    if (f.exists())
      f.listFiles().foreach(item => {
        if (item.isDirectory)
          cleanDir(item.getAbsolutePath)

        item.delete()
      })
  }

  def createPath(root: String, newsType: NewsType, rubric: Rubrics): String = {
    new StringBuilder(root).append(File.separator).append(newsType.toString)
      .append(File.separator).append(rubric.toString).toString
  }

  def writeSnapshots(dir: String, s: Option[(LentaSnapshot, Option[LentaItem])]): Unit = {
    s match {
      case None => Unit
      case Some((snapshot, lastItem)) => {
        writeSnapshots(dir, snapshot.dropLast())

        if (!snapshot.isEmpty) {
          lastItem match {
            case None => snapshot.writeXml(createPath(dir, snapshot.newsType, snapshot.rubric) + File.separator + "root.xml")
            case Some(item) => snapshot.writeXml(createPath(dir, snapshot.newsType, snapshot.rubric) + File.separator + item.pubDate + ".xml")
          }
        }
      }
    }
  }

  if (args.length >= 1) {
    println("Will use root folder: " + args(0))

    createfs(args(0))

    while (true) {
      println("Downloading news")
      val nn = LentaSnapshot.download(NewsType.NEWS)

      writeSnapshots(args(0), Some(nn, None))

      println("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}
