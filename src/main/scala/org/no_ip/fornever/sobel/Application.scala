package org.no_ip.fornever.sobel

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.awt.Color

object Application extends App {
  val filename = args(0)
  val resultFilename = args(1)

  val image = ImageIO.read(new File(filename))
  val grayscaled = getGrayScale(image)
  val width = grayscaled.getWidth
  val height = grayscaled.getHeight

  val Gy = Array(Array(-1, -2, -1), Array(0, 0, 0), Array(1, 2, 1))
  val Gx = Array(Array(-1, 0, 1), Array(-2, 0, 2), Array(-1, 0, 1))

  val resultImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
  for {
    y <- 0 to (height - 1)
    x <- 0 to (width - 1)
  } {
    val xValue = mapImagePoint(grayscaled, x, y, Gx)
    val yValue = mapImagePoint(grayscaled, x, y, Gy)
    val value = Math.min(1.0, Math.sqrt(Math.pow(xValue, 2) + Math.pow(yValue, 2))).toFloat
    val color = new Color(value, value, value)
    resultImage.setRGB(x, y, color.getRGB)
  }

  ImageIO.write(resultImage, "png", new File(resultFilename))

  def getGrayScale(inputImage: BufferedImage): BufferedImage = {
    val img = new BufferedImage(inputImage.getWidth, inputImage.getHeight, BufferedImage.TYPE_BYTE_GRAY)
    val graphics = img.getGraphics
    graphics.drawImage(inputImage, 0, 0, null)
    graphics.dispose()
    img
  }



  private def mapImagePoint(image: BufferedImage, x: Int, y: Int, matrix: Array[Array[Int]]): Double = {
    var result = 0.0
    for {
      dY <- -1 to 1
      dX <- -1 to 1
      cY = y + dY
      cX = x + dX
    } {
      if (cY >= 0 && cY < image.getHeight && cX >= 0 && cX < image.getWidth) {
        val coefficient = matrix(dY + 1)(dX + 1)
        val color = new Color(image.getRGB(cX, cY))
        result += color.getRed.toDouble / 255.0 * coefficient
      }
    }

    result
  }
}