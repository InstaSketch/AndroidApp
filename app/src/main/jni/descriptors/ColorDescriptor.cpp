#include "../include/descriptors/ColorDescriptor.h"
#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/opencv.hpp"

#include <tuple>
#include <vector>

typedef std::tuple<int, int,int,int> i4tuple;

const int SUB_HISTOGRAMS = 5;

ColorDescriptor::ColorDescriptor(int h_bins, int s_bins, int v_bins){
    this->h_bins = h_bins;
    this->s_bins = s_bins;
    this->v_bins = v_bins;
}

std::vector<float> ColorDescriptor::flatten_vec(cv::Mat hist){

//http://stackoverflow.com/questions/26681713/convert-mat-to-array-vector-in-opencv
    std::vector<float> hist_vec;
    if (hist.isContinuous()){
        hist_vec.assign((float*) hist.datastart, (float*)hist.dataend);
    }
    else {
//    WARNING: No idea how to test with noncontiguous arrays.
//        std::cout << "NONCONTIGUOUS" << std::endl;
        for (int i = 0; i < this->h_bins; i++){
            for (int j = 0; j < this->s_bins; j++){
                hist_vec.insert(hist_vec.end(), (float*)hist.ptr<float>(j), (float*)hist.ptr<float>(j)+this->v_bins);
            }
        }
    }

//    std::cout << hist_vec.size() << std::endl;
//    std::cout << hist_vec[392] << std::endl;
    return hist_vec;
}

cv::Mat ColorDescriptor::histogram(cv::Mat image, cv::Mat mask){
    cv::Mat hist;
    int channels[] = {0,1,2};

    float hranges[] = { 0, 180 };
    float sranges[] = { 0, 256 };
    float vranges[] = { 0, 256 };

    int histSize[] = {this->h_bins, this->s_bins, this->v_bins};
    const float* ranges[] = { hranges, sranges, vranges };

    cv::calcHist(&image, 1, channels, mask, hist, 3, histSize, ranges);
    cv::normalize(hist, hist);
//    hist.reshape(1,1);

//    std::cout << hist << std::endl;
//    std::cout << hist.dims << std::endl;
//    std::cout << "Value: " << hist.at<float>(0,0,4) << std::endl;
//    std::cout << "Value: " << hist.at<float>(0,0,479) << std::endl;

    return hist;
}

std::vector<float> ColorDescriptor::describe(cv::Mat image){
    cv::cvtColor(image, image, CV_BGR2HSV);

    int rows = image.rows;
    int cols = image.cols;

    int cX = cols*0.5;
    int cY = rows*0.5;

    std::vector <i4tuple> v;
    v.push_back(i4tuple(0, cX, 0, cY));
    v.push_back(i4tuple(cX, cols, 0, cY));
    v.push_back(i4tuple(cX, cols, cY, rows));
    v.push_back(i4tuple(0, cX, cY, rows));

    int axesX = (cols*0.75)/2;
    int axesY = (rows*0.75)/2;

    cv::Mat ellipMask = cv::Mat(rows, cols, CV_8UC1, cv::Scalar(0));
    cv::ellipse(ellipMask, cv::Point(cX, cY), cv::Point(axesX, axesY), 0,0,360, cv::Scalar(255,255,255), -1);

    std::vector<float> finalHist, localHist;
    for(std::vector<i4tuple>::iterator it = v.begin(); it != v.end(); ++it) {
//        std::cout << std::get<0>(*it) << std::endl;
        cv::Mat cornerMask = cv::Mat(rows, cols, CV_8UC1, cv::Scalar(0));
        cv::rectangle(cornerMask, cv::Point(std::get<0>(*it), std::get<2>(*it)), cv::Point(std::get<1>(*it), std::get<3>(*it)), cv::Scalar(255, 255, 255), -1);
        cv::subtract(cornerMask, ellipMask, cornerMask);
//        std::cout << "cornermask_dims " << cornerMask.dims << std::endl;
        localHist = flatten_vec(histogram(image, cornerMask));
        finalHist.insert(finalHist.end(), localHist.begin(), localHist.end() );

    }

    localHist = flatten_vec(histogram(image, ellipMask));

    finalHist.insert(finalHist.end(), localHist.begin(), localHist.end() );
//    return image;
    return finalHist;
}
