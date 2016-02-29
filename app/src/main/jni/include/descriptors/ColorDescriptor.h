#ifndef COLORDESCRIPTOR_H
#define COLORDESCRIPTOR_H

#include "opencv2/core/core.hpp"

class ColorDescriptor
{
public:
    ColorDescriptor(int h_bins, int s_bins, int v_bins);
    std::vector<float> describe(cv::Mat image);
    cv::Mat histogram(cv::Mat image, cv::Mat mask);
    std::vector<float> flatten_vec(cv::Mat hist);

protected:
private:
    int h_bins;
    int s_bins;
    int v_bins;
};

#endif // COLORDESCRIPTOR_H
