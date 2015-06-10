package org.opencv.samples.colorblobdetect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class ColorBlobDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private Scalar POINT_COLOR;
    protected double angle;


    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mNew = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    Mat mHierarchy1 = new Mat();
    Mat mHierarchy2 = new Mat();
    Mat mHierarchy3 = new Mat();


    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process_tech(Mat rgbaImage)
    {
        int x1,x2,x3,y1,y2,y3;
        Point c1 = new Point(0,0);
        Point c2 = new Point(0,0);
        Point c3 = new Point(0,0);
        rgbaImage.copyTo(mNew);
        Rect roi1 = new Rect(0, 0, 287, 480);
        Rect roi2 = new Rect(288, 0, 287, 480);
        Rect roi3 = new Rect(576, 0, 287, 480);
        Mat roiImg1 = mNew.submat(roi1);
        Mat roiImg2 = mNew.submat(roi2);
        Mat roiImg3 = mNew.submat(roi3);
        Imgproc.threshold(roiImg1, roiImg1, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(roiImg1, roiImg1);
        Imgproc.threshold(roiImg2, roiImg2, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(roiImg2, roiImg2);
        Imgproc.threshold(roiImg3, roiImg3, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(roiImg3, roiImg3);
        List<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
        List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
        List<MatOfPoint> contours3 = new ArrayList<MatOfPoint>();
        Imgproc.findContours(roiImg1, contours1, mHierarchy1, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(roiImg2, contours2, mHierarchy2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(roiImg3, contours3, mHierarchy3, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        List<Moments> mu1 = new ArrayList<Moments>(contours1.size());
        List<Moments> mu2 = new ArrayList<Moments>(contours2.size());
        List<Moments> mu3 = new ArrayList<Moments>(contours3.size());
        for (int i=0;i<contours1.size();i++)
        {
            if (Imgproc.contourArea(contours1.get(i))>10000)
            {
                Moments p1= Imgproc.moments(contours1.get(i), true);
                //Moments p = mu1.get(i);
                if (p1.get_m00()!=0)
                {
                    x1 = (int) (p1.get_m10()/p1.get_m00());
                    y1 = (int) (p1.get_m01()/p1.get_m00());
                    c1= new Point(x1,y1);
                    }


            }
        }
        for (int i=0;i<contours2.size();i++)
        {
            if (Imgproc.contourArea(contours2.get(i))>10000)
            {
                Moments p2= Imgproc.moments(contours2.get(i), true);
                //Moments p = mu1.get(i);
                if (p2.get_m00()!=0)
                {
                    x2 = (int) (p2.get_m10()/p2.get_m00());
                    y2 = (int) (p2.get_m01()/p2.get_m00());
                    c2= new Point(x2+287,y2);
                }


            }
        }
        for (int i=0;i<contours3.size();i++)
        {
            if (Imgproc.contourArea(contours3.get(i))>10000)
            {
                Moments p3= Imgproc.moments(contours3.get(i), true);
                //Moments p = mu1.get(i);
                if (p3.get_m00()!=0)
                {
                    x3 = (int) (p3.get_m10()/p3.get_m00());
                    y3 = (int) (p3.get_m01()/p3.get_m00());
                    c3= new Point(x3+287+287,y3);
                }


            }
        }
        Core.circle(rgbaImage, c1, 4, new Scalar(255,49,0,255));
        Core.circle(rgbaImage, c2, 4, new Scalar(255,49,0,255));
        Core.circle(rgbaImage, c3, 4, new Scalar(255, 49, 0, 255));
        Core.line(rgbaImage, c1, c2, new Scalar(255, 49, 0, 255));
        Core.line(rgbaImage,c2,c3,new Scalar(255,49,0,255));
        double len1 = Math.sqrt((c1.x-c2.x) * (c1.x-c2.x) + (c1.y-c2.y) * (c1.y-c2.y));
        double len2 = Math.sqrt((c2.x-c3.x) * (c2.x-c3.x) + (c2.y-c3.y) * (c2.y-c3.y));
        double dot = (c1.x-c2.x)  * (c3.x-c2.x) + (c1.y-c2.y) * (c3.y-c2.y);
        double a = dot/(len1*len2);
        angle = Math.acos(a) * 57.3;
        /*if (a>=1.0)
            angle = 180.0;
        else if (a<=-1.0)
            angle = 0.0;
        else
            angle = 180-Math.acos(a)*57.3;*/

        /*Imgproc.threshold(rgbaImage, rgbaImage, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(rgbaImage,rgbaImage);*/

    }

    public void process(Mat rgbaImage) {
        int x1,x2,x3,y1,y2,y3;
        Point c1 = new Point(0,0);
        Point c2 = new Point(0,0);
        Point c3 = new Point(0,0);
        rgbaImage.copyTo(mNew);
        Imgproc.cvtColor(mNew, mNew, Imgproc.COLOR_RGB2GRAY, 1);
        Rect roi1 = new Rect(0, 0, 287, 480);
        Rect roi2 = new Rect(288, 0, 287, 480);
        Rect roi3 = new Rect(576, 0, 287, 480);
        Mat roiImg1 = mNew.submat(roi1);
        Mat roiImg2 = mNew.submat(roi2);
        Mat roiImg3 = mNew.submat(roi3);
        Imgproc.threshold(roiImg1, roiImg1, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(roiImg1, roiImg1);
        Imgproc.threshold(roiImg2, roiImg2, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(roiImg2, roiImg2);
        Imgproc.threshold(roiImg3, roiImg3, 75, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_not(roiImg3, roiImg3);
        List<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
        List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
        List<MatOfPoint> contours3 = new ArrayList<MatOfPoint>();
        Imgproc.findContours(roiImg1, contours1, mHierarchy1, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(roiImg2, contours2, mHierarchy2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(roiImg3, contours3, mHierarchy3, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        List<Moments> mu1 = new ArrayList<Moments>(contours1.size());
        List<Moments> mu2 = new ArrayList<Moments>(contours2.size());
        List<Moments> mu3 = new ArrayList<Moments>(contours3.size());
        for (int i=0;i<contours1.size();i++)
        {
            if (Imgproc.contourArea(contours1.get(i))>10000)
            {
                Moments p1= Imgproc.moments(contours1.get(i), true);
                //Moments p = mu1.get(i);
                if (p1.get_m00()!=0)
                {
                    x1 = (int) (p1.get_m10()/p1.get_m00());
                    y1 = (int) (p1.get_m01()/p1.get_m00());
                    c1= new Point(x1,y1);
                }


            }
        }
        for (int i=0;i<contours2.size();i++)
        {
            if (Imgproc.contourArea(contours2.get(i))>10000)
            {
                Moments p2= Imgproc.moments(contours2.get(i), true);
                //Moments p = mu1.get(i);
                if (p2.get_m00()!=0)
                {
                    x2 = (int) (p2.get_m10()/p2.get_m00());
                    y2 = (int) (p2.get_m01()/p2.get_m00());
                    c2= new Point(x2+287,y2);
                }


            }
        }
        for (int i=0;i<contours3.size();i++)
        {
            if (Imgproc.contourArea(contours3.get(i))>10000)
            {
                Moments p3= Imgproc.moments(contours3.get(i), true);
                //Moments p = mu1.get(i);
                if (p3.get_m00()!=0)
                {
                    x3 = (int) (p3.get_m10()/p3.get_m00());
                    y3 = (int) (p3.get_m01()/p3.get_m00());
                    c3= new Point(x3+287+287,y3);
                }


            }
        }
        Core.circle(rgbaImage, c1, 4, new Scalar(255,49,0,255));
        Core.circle(rgbaImage, c2, 4, new Scalar(255,49,0,255));
        Core.circle(rgbaImage, c3, 4, new Scalar(255,49,0,255));
        Core.line(rgbaImage, c1, c2, new Scalar(255, 49, 0, 255));
        Core.line(rgbaImage,c2,c3,new Scalar(255,49,0,255));
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
}
