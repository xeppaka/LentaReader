package com.xeppaka.lentaruserver.fs

import java.io.IOException
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
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
  val TMP_SUFFIX = "_tmp"
  val TMP2_SUFFIX = "_tmp2"

  private def createRubricFolders(dir: Path): List[Path] = {
    Rubrics.values.foldLeft(List[Path]())((z, item) => {
      val path = dir.resolve(item.toString)
      val path_tmp = dir.resolve(item.toString + TMP_SUFFIX)
      try {
        Files.createDirectory(path)
        Files.createDirectory(path_tmp)
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
          // if (file.getFileName.toString != FILENAME_ROOT_SNAPSHOT)
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

  def gzip(dir: Path): Unit = {
    if (Files.exists(dir)) {
      val deleteVisitor = new SimpleFileVisitor[Path]() {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          val command = "gzip -k " + file.toString
          val p = Runtime.getRuntime.exec(command)
          p.waitFor()
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

  def createFullTmpPath(root: String, newsType: NewsType, rubric: Rubrics): Path = {
    FileSystems.getDefault.getPath(root, newsType.toString, rubric.toString + TMP_SUFFIX)
  }

  def createFullTmp2Path(root: String, newsType: NewsType, rubric: Rubrics): Path = {
    FileSystems.getDefault.getPath(root, newsType.toString, rubric.toString + TMP2_SUFFIX)
  }
}
