package fr.lewon.dofus.bot.util

import net.sourceforge.tess4j.ITessAPI
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.highgui.HighGui
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage


object OCRUtil {

    @Synchronized
    private fun initTesseract(charWhitelist: String? = null): ITesseract {
        val tess = Tesseract()
        tess.setDatapath("tessdata")
        if (charWhitelist != null) {
            tess.setTessVariable("tessedit_char_whitelist", charWhitelist)
        }
        return tess
    }

    @Synchronized
    fun getAllLines(img: BufferedImage, charWhitelist: String? = null): List<String> {
        val imgText = initTesseract(charWhitelist).doOCR(img)
        return imgText.split("\n").filter { it.isNotBlank() }
    }

    @Synchronized
    fun splitByLine(img: BufferedImage, ratio: Int): List<BufferedImage> {
        return split(img, ratio, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE)
    }

    @Synchronized
    fun split(img: BufferedImage, ratio: Int, mode: Int): List<BufferedImage> {
        val regions = initTesseract().getSegmentedRegions(img, mode)
        return regions.map {
            ImageUtil.resizeImage(
                img.getSubimage(it.x, it.y, it.width, it.height),
                ratio
            )
        }
    }

    @Synchronized
    fun segmentImage(img: Mat): Mat {
        // init
        val grayImage = Mat()
        val detectedEdges = Mat()

        // convert to grayscale
        Imgproc.cvtColor(img, grayImage, Imgproc.COLOR_BGR2GRAY)

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, Size(3.0, 3.0))

        // canny detector, with ratio of lower:upper threshold of 3:1
        Imgproc.Canny(detectedEdges, detectedEdges, 1.0, 3.0)

        // using Canny's output as a mask, display the result
        val dest = Mat()
        img.copyTo(dest, detectedEdges)

        return dest
    }

    @Synchronized
    fun keepDarkOnImage(imageMat: Mat, smoothen: Boolean = true, thresh: Double = 95.0): BufferedImage {
        val srcGray = Mat()
        Imgproc.cvtColor(imageMat, srcGray, Imgproc.COLOR_BGR2GRAY)

        val dst = Mat()
        Imgproc.threshold(srcGray, dst, thresh, 255.0, 0)

        if (!smoothen) {
            return HighGui.toBufferedImage(dst) as BufferedImage
        }

        val smoothDst = Mat()
        Imgproc.medianBlur(dst, smoothDst, 7)

        return HighGui.toBufferedImage(smoothDst) as BufferedImage
    }

    @Synchronized
    fun keepWhiteOnImage(imageMat: Mat, smoothen: Boolean = true, thresh: Double = 115.0): BufferedImage {
        val srcGray = Mat()
        Imgproc.cvtColor(imageMat, srcGray, Imgproc.COLOR_BGR2GRAY)

        val dst = Mat()
        Imgproc.threshold(srcGray, dst, thresh, 255.0, 1)

        if (!smoothen) {
            return HighGui.toBufferedImage(dst) as BufferedImage
        }

        val smoothDst = Mat()
        Imgproc.medianBlur(dst, smoothDst, 7)

        return HighGui.toBufferedImage(smoothDst) as BufferedImage
    }

}