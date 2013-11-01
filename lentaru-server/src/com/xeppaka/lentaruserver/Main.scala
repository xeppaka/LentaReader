package com.xeppaka.lentaruserver

import java.io.{PrintWriter, File}
import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import com.xeppaka.lentaruserver.items.LentaItem
import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.FileSystems

object Main extends App {
  if (args.length >= 1) {
    println("Will use root folder: " + args(0))

    val rootPath = FileSystems.getDefault.getPath(args(0))
    FileSystem.createfs(rootPath)

    while (true) {
      println("Downloading news")
      val nn = LentaSnapshot.download(NewsType.NEWS, Rubrics.ECONOMICS)

      nn.writeXmlSet(rootPath)

      println("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}
