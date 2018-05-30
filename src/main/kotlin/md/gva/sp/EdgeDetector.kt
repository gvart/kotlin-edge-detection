package md.gva.sp

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import javafx.scene.image.Image
import java.io.InputStream
import javax.imageio.ImageIO

class EdgeDetector {
    var ready: Boolean = false
    var stream: InputStream? = null
        set(value) {
            ready = true
            field = value
        }

    fun render(): Image? {
        val image = ImageIO.read(stream)
        val x = image.width
        val y = image.height

        val edgeColors = Array(x) { IntArray(y) }
        var maxGradient = -1

        for (i in 1 until x - 1) {
            for (j in 1 until y - 1) {

                val val00 = getGrayScale(image.getRGB(i - 1, j - 1))
                val val01 = getGrayScale(image.getRGB(i - 1, j))
                val val02 = getGrayScale(image.getRGB(i - 1, j + 1))

                val val10 = getGrayScale(image.getRGB(i, j - 1))
                val val11 = getGrayScale(image.getRGB(i, j))
                val val12 = getGrayScale(image.getRGB(i, j + 1))

                val val20 = getGrayScale(image.getRGB(i + 1, j - 1))
                val val21 = getGrayScale(image.getRGB(i + 1, j))
                val val22 = getGrayScale(image.getRGB(i + 1, j + 1))

                val gx = ((-1 * val00 + 0 * val01 + 1 * val02)
                        + (-2 * val10 + 0 * val11 + 2 * val12)
                        + (-1 * val20 + 0 * val21 + 1 * val22))

                val gy = ((-1 * val00 + -2 * val01 + -1 * val02)
                        + (0 * val10 + 0 * val11 + 0 * val12)
                        + (1 * val20 + 2 * val21 + 1 * val22))

                val gFinal = Math.sqrt((gx * gx + gy * gy).toDouble()).toInt()

                if (maxGradient < gFinal) {
                    maxGradient = gFinal
                }

                edgeColors[i][j] = gFinal
            }
        }

        val scale = 255.0 / maxGradient

        for (i in 1 until x - 1) {
            for (j in 1 until y - 1) {
                var edgeColor = edgeColors[i][j]
                edgeColor = (edgeColor * scale).toInt()
                edgeColor = -0x1000000 or (edgeColor shl 16) or (edgeColor shl 8) or edgeColor

                image.setRGB(i, j, edgeColor)
            }
        }
        val os = ByteOutputStream()
        ImageIO.write(image, "png", os)

        ready = false
        val newInputStream = os.newInputStream()
        stream = newInputStream
        return Image(stream)
    }

    fun ready(): Boolean {
        return ready
    }

    private fun getGrayScale(rgb: Int): Int {
        val r = rgb shr 16 and 0xff
        val g = rgb shr 8 and 0xff
        val b = rgb and 0xff

        //https://en.wikipedia.org/wiki/Grayscale, calculating luminance
        return (0.2126 * r + 0.7152 * g + 0.0722 * b).toInt()
    }
}