package com.xeppaka.lentaruserver

import java.io.{PrintWriter, File}

object Main extends App {
  if (args.length >= 1) {
    println("Will use file name: " + args(0))

    while (true) {
      println("Downloading news")
      val nn = LentaSnapshot.download(NewsType.NEWS)
      println("Writing to file: " + args(0))
      val f = new File(args(0))
      val out = new PrintWriter(f)
      try {
        out.println(nn.toXml())
      } finally {
        out.close()
      }

      println("Sleep for 60 seconds...")
      Thread.sleep(1000 * 60)
    }
  } else {
    println("No arguments provided!")
  }

  println("Exit...")
}