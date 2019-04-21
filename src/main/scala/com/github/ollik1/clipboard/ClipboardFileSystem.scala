package com.github.ollik1.clipboard

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.io.IOException
import java.net.URI

import org.apache.hadoop.fs.permission.FsPermission
import org.apache.hadoop.fs._
import org.apache.hadoop.util.Progressable

/**
  * A [[FileSystem]] read-only implementation that exposes the string
  * contents of the system clipboard as a single file "clipboard:///contents"
  */
class ClipboardFileSystem extends FileSystem {
  import ClipboardFileSystem._

  override def getUri: URI = new URI(s"$Scheme:///")
  override def open(f: Path, bufferSize: Int): FSDataInputStream = {
    new FSDataInputStream(new StringInputStream(contents))
  }
  override def create(
      f: Path,
      permission: FsPermission,
      overwrite: Boolean,
      bufferSize: Int,
      replication: Short,
      blockSize: Long,
      progress: Progressable
  ): FSDataOutputStream = readOnly
  override def append(
      f: Path,
      bufferSize: Int,
      progress: Progressable
  ): FSDataOutputStream = readOnly
  override def rename(src: Path, dst: Path): Boolean = readOnly
  override def delete(f: Path, recursive: Boolean): Boolean = readOnly
  override def listStatus(f: Path): Array[FileStatus] = f.toUri.getPath match {
    case Root     => Array(getFileStatus(new Path(Contents)))
    case Contents => Array(getFileStatus(f))
  }
  override def setWorkingDirectory(new_dir: Path): Unit = readOnly
  override def getWorkingDirectory: Path = new Path(Root)
  override def mkdirs(f: Path, permission: FsPermission): Boolean = readOnly
  override def getFileStatus(f: Path): FileStatus = f.toUri.getPath match {
    case Root => new FileStatus(0L, true, 0, 0L, 0L, f)
    case Contents =>
      new FileStatus(contents.length.toLong, false, 0, 0L, 0L, f)
  }

  private def contents: String =
    Toolkit.getDefaultToolkit.getSystemClipboard
      .getData(DataFlavor.stringFlavor)
      .asInstanceOf[String]

  private def readOnly[T]: T =
    throw new IOException("This filesystem is read-only")
}

object ClipboardFileSystem {
  val Scheme = "clipboard"
  val Root = "/"
  val Contents = "/contents"
}
