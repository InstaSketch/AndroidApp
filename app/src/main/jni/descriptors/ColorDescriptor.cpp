#include "../include/descriptors/ColorDescriptor.h"
#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/opencv.hpp"
#include "/home/transfusion/eigen-eigen-07105f7124f9/Eigen/Core"

#include <tuple>
#include <vector>

typedef std::tuple<int, int,int,int> i4tuple;

const int SUB_HISTOGRAMS = 5;

/*ColorDescriptor::ColorDescriptor(int h_bins, int s_bins, int v_bins){
    this->h_bins = h_bins;
    this->s_bins = s_bins;
    this->v_bins = v_bins;
}*/

std::vector<float> ColorDescriptor::flatten_vec(cv::Mat hist, int h_bins, int s_bins, int v_bins){

//http://stackoverflow.com/questions/26681713/convert-mat-to-array-vector-in-opencv
    std::vector<float> hist_vec;
    if (hist.isContinuous()){
        hist_vec.assign((float*) hist.datastart, (float*)hist.dataend);
    }
    else {
//    WARNING: No idea how to test with noncontiguous arrays.
//        std::cout << "NONCONTIGUOUS" << std::endl;
        for (int i = 0; i < h_bins; i++){
            for (int j = 0; j < s_bins; j++){
                hist_vec.insert(hist_vec.end(), (float*)hist.ptr<float>(j), (float*)hist.ptr<float>(j)+v_bins);
            }
        }
    }

//    std::cout << hist_vec.size() << std::endl;
//    std::cout << hist_vec[392] << std::endl;
    return hist_vec;
}

float ColorDescriptor::compare_chi_squared(float hist1[], int hist1_size, float hist2[], int hist2_size, float eps){
    Eigen::Map<Eigen::ArrayXf> hist1map(hist1, hist1_size);
    Eigen::Map<Eigen::ArrayXf> hist2map(hist2, hist2_size);
    Eigen::ArrayXf sub = (hist1map-hist2map).square() / (hist1map+hist2map+eps);
    return sub.sum()*0.5;
}

float ColorDescriptor::compare_chi_squared(std::vector<float> hist1, std::vector<float> hist2, float eps){
    Eigen::Map<Eigen::ArrayXf> hist1map(hist1.data(), hist1.size());
    Eigen::Map<Eigen::ArrayXf> hist2map(hist2.data(), hist2.size());
    Eigen::ArrayXf sub = (hist1map-hist2map).square() / (hist1map+hist2map+eps);
    return sub.sum()*0.5;
}

float ColorDescriptor::compare_bhattacharyya(float hist1[], int hist1_size, float hist2[], int hist2_size) {
    cv::Mat mat1 = cv::Mat(1,hist1_size,CV_32FC1,hist1);
    cv::Mat mat2 = cv::Mat(1,hist2_size,CV_32FC1, hist2);
    return cv::compareHist(mat1, mat2, CV_COMP_BHATTACHARYYA );
}

float ColorDescriptor::compare_intersect(float hist1[], int hist1_size, float hist2[], int hist2_size) {
    cv::Mat mat1 = cv::Mat(1,hist1_size,CV_32FC1,hist1);
    cv::Mat mat2 = cv::Mat(1,hist2_size,CV_32FC1, hist2);
    return cv::compareHist(mat1, mat2, CV_COMP_INTERSECT );
}

cv::Mat ColorDescriptor::histogram(cv::Mat image, cv::Mat mask, int h_bins, int s_bins, int v_bins){
    cv::Mat hist;
    int channels[] = {0,1,2};

    float hranges[] = { 0, 180 };
    float sranges[] = { 0, 256 };
    float vranges[] = { 0, 256 };

    int histSize[] = {h_bins, s_bins, v_bins};
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

std::vector<float> ColorDescriptor::describe(cv::Mat image, int h_bins, int s_bins, int v_bins){
    if (image.empty()){
        return std::vector<float>();
    }
    cv::cvtColor(image, image, CV_BGR2HSV);
//    cv::resize(image, image, cv::Size(300, 300), 0, 0, CV_INTER_LINEAR);

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
        localHist = flatten_vec(histogram(image, cornerMask, h_bins, s_bins, v_bins), h_bins, s_bins, v_bins);
        finalHist.insert(finalHist.end(), localHist.begin(), localHist.end() );

    }

    localHist = flatten_vec(histogram(image, ellipMask, h_bins, s_bins, v_bins), h_bins, s_bins, v_bins);

    finalHist.insert(finalHist.end(), localHist.begin(), localHist.end() );
//    return image;
    return finalHist;
}

std::vector<float> ColorDescriptor::describe_sketch(cv::Mat image, int h_bins, int s_bins, int v_bins, int threshold){
//    image must be in BGRA format!!!!
    if (image.empty()){
        return std::vector<float>();
    }

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

    cv::Mat transparencyMask;
    cv::Scalar lowerb = cv::Scalar(0,0,0,0);
    cv::Scalar upperb = cv::Scalar(180,255,255,threshold);

    cv::inRange(image, lowerb, upperb, transparencyMask);
    transparencyMask = 255-transparencyMask;

    cv::cvtColor(image, image, CV_BGR2HSV);

    std::vector<float> finalHist, localHist;
    for(std::vector<i4tuple>::iterator it = v.begin(); it != v.end(); ++it) {
        cv::Mat cornerMask = cv::Mat(rows, cols, CV_8UC1, cv::Scalar(0));
        cv::rectangle(cornerMask, cv::Point(std::get<0>(*it), std::get<2>(*it)), cv::Point(std::get<1>(*it), std::get<3>(*it)), cv::Scalar(255, 255, 255), -1);
        cornerMask = 255-cornerMask;

        cv::subtract(transparencyMask, cornerMask, cornerMask);
        cv::subtract(cornerMask, ellipMask, cornerMask);

        localHist = flatten_vec(histogram(image, cornerMask, h_bins, s_bins, v_bins), h_bins, s_bins, v_bins);
        finalHist.insert(finalHist.end(), localHist.begin(), localHist.end() );

    }

    cv::subtract(transparencyMask, 255-ellipMask, ellipMask);
    localHist = flatten_vec(histogram(image, ellipMask, h_bins, s_bins, v_bins), h_bins, s_bins, v_bins);
    finalHist.insert(finalHist.end(), localHist.begin(), localHist.end() );
    return finalHist;

}