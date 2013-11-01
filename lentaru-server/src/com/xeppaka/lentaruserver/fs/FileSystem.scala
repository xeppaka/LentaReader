package com.xeppaka.lentaruserver.fs

import java.io.{IOException, File}
import com.xeppaka.lentaruserver.{NewsType, Rubrics}
import com.xeppaka.lentaruserver.NewsType._
import com.xeppaka.lentaruserver.Rubrics._
import com.xeppaka.lentaruserver.NewsType
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

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
      val path = FileSystems.getDefault.getPath(dir + FileSystems.getDefault.getSeparator + item.toString)
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
      val path = FileSystems.getDefault.getPath(dir + FileSystems.getDefault.getSeparator + item.toString)
      try {
        Files.createDirectory(path);
        path :: z
      } catch {
        case e: FileAlreadyExistsException => z
      }
    })
  }

  def createfs(dir: Path) = {
    val newsFolders = createNewsTypeFolders(dir)
    newsFolders.foreach(item => createRubricFolders(item))
  }

  def cleanfs(dir: Path): Unit = {
    val deleteVisitor = new SimpleFileVisitor[Path]() {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        Files.delete(file)
        FileVisitResult.CONTINUE;
      }

      override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
        if (exc == null) {
          Files.delete(dir)
          FileVisitResult.CONTINUE;
        } else {
          throw exc
        }
      }
    }

    Files.walkFileTree(dir, deleteVisitor)
  }

  def createFullPath(root: String, newsType: NewsType, rubric: Rubrics, filename: String = FILENAME_ROOT_SNAPSHOT): Path = {
    FileSystems.getDefault.getPath(root, newsType.toString, rubric.toString, filename)
  }
}
