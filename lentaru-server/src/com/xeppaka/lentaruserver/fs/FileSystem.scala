package com.xeppaka.lentaruserver.fs

import java.io.{IOException, File}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import com.xeppaka.lentaruserver.{NewsType, Rubrics}
import com.xeppaka.lentaruserver.NewsType.NewsType
import com.xeppaka.lentaruserver.NewsType
import com.xeppaka.lentaruserver.Rubrics.Rubrics
import com.xeppaka.lentaruserver.Rubrics

/**
 * Created with IntelliJ IDEA.
 * User: kacpa01
 * Date: 11/1/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
object FileSystem {

  val FILENAME_ROOT_SNAPSHOT = "root.xml"

  private def createRubricFolders(dir: Path): List[Path] = {
    Rubrics.values.foldLeft(List[Path]())((z, item) => {
      val path = dir.resolve(item.toString)
      try {
        Files.createDirectory(path)
        path :: z
      } catch {
        case e: FileAlreadyExistsException => z
      }
    })
  }

  private def createNewsTypeFolders(dir: Path): List[Path] = {
    NewsType.values.foldLeft(List[Path]())((z, item) => {
      val path = dir.resolve(item.toString)
      try {
        Files.createDirectory(path)
        path :: z
      } catch {
        case e: FileAlreadyExistsException => z
      }
    })
  }

  def createfs(dir: Path) = {
    createNewsTypeFolders(dir)
    NewsType.values.foreach(item => createRubricFolders(dir.resolve(item.toString)))
  }

  def clean(dir: Path): Unit = {
    if (Files.exists(dir)) {
      val deleteVisitor = new SimpleFileVisitor[Path]() {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          if (file.getFileName.toString != FILENAME_ROOT_SNAPSHOT)
            Files.delete(file)
          FileVisitResult.CONTINUE
        }

        override def postVisitDirectory(curdir: Path, exc: IOException): FileVisitResult = {
          if (exc == null) {
            if (curdir != dir)
              Files.delete(curdir)
            FileVisitResult.CONTINUE
          } else {
            throw exc
          }
        }
      }

      Files.walkFileTree(dir, deleteVisitor)
    }
  }

  def createFullPath(root: String, newsType: NewsType, rubric: Rubrics): Path = {
    FileSystems.getDefault.getPath(root, newsType.toString, rubric.toString)
  }
}
