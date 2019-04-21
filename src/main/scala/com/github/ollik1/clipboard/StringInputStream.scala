package com.github.ollik1.clipboard

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import org.apache.hadoop.fs.{FSDataInputStream, PositionedReadable, Seekable}

/**
  * Creates an in-memory [[java.io.InputStream]] based on given string.
  * Implements [[PositionedReadable]] and [[Seekable]] so that the stream
  * can be passed to [[FSDataInputStream]]
  */
class StringInputStream(in: String)
    extends ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8))
    with PositionedReadable
    with Seekable {

  override def read(
      position: Long,
      buffer: Array[Byte],
      offset: Int,
      length: Int
  ): Int = {
    val len =
      length.min(buf.length - position.toInt).min(buffer.length - offset)
    System.arraycopy(buf, position.toInt, buffer, offset, len)
    len
  }
  override def readFully(
      position: Long,
      buffer: Array[Byte],
      offset: Int,
      length: Int
  ): Unit = System.arraycopy(buf, position.toInt, buffer, offset, length)
  override def readFully(position: Long, buffer: Array[Byte]): Unit =
    buf.copyToArray(buffer, position.toInt, buffer.size)
  override def seek(pos: Long): Unit = mark(pos.toInt)
  override def getPos: Long = pos
  override def seekToNewSource(targetPos: Long): Boolean = false
}
