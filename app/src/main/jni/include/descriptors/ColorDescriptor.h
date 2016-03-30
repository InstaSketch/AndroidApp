#ifndef COLORDESCRIPTOR_H
#define COLORDESCRIPTOR_H

#include "opencv2/core/core.hpp"

class ColorDescriptor
{
public:
//    ColorDescriptor(int h_bins, int s_bins, int v_bins);
    static std::vector<float> describe(cv::Mat image, int h_bins, int s_bins, int v_bins);

    static float compare_chi_squared(std::vector<float> hist1, std::vector<float> hist2, float eps=1e-10);
    static float compare_chi_squared(float hist1[], int hist1_size, float hist2[], int hist2_size, float eps=1e-10);
    static float compare_bhattacharyya(float hist1[], int hist1_size, float hist2[], int hist2_size);

protected:
private:
    /*int h_bins;
    int s_bins;
    int v_bins;*/
    static cv::Mat histogram(cv::Mat image, cv::Mat mask, int h_bins, int s_bins, int v_bins);
    static std::vector<float> flatten_vec(cv::Mat hist, int h_bins, int s_bins, int v_bins);
};

#endif // COLORDESCRIPTOR_H
