package com.xeppaka.lentaruserver

import com.xeppaka.lentaruserver.fs.FileSystem
import java.nio.file.FileSystems

object Main extends App {
  if (args.length >= 1) {
    println("Will use root folder: " + args(0))

    val rootPath = FileSystems.getDefault.getPath(args(0))
    FileSystem.createfs(rootPath)

    while (true) {
      Rubrics.values.foreach(rubric => {
        val curdir = FileSystem.createFullPath(rootPath.toString, NewsType.NEWS, rubric)

        println("Downloading " + rubric.toString)
        val nn = LentaSnapshot.download(NewsType.NEWS, rubric)
        println("Deleting xmls in " + curdir.toString)
        FileSystem.clean(curdir)
        println("Writing xmls in " + curdir.toString)
        nn.writeXmlSet(curdir)
      })

      println("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}
