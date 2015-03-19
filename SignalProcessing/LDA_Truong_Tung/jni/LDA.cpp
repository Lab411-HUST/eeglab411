#include <string.h>
#include <jni.h>
#include <fstream>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>
//#include <ovpCApplyTemporalFilter.h>
#include <plugins/processing/classification/src/algorithms/ovpCAlgorithmClassifierLDA.h>
#include <contrib/plugins/processing/classification/src/algorithms/ovpCFeatureExtractionLda.cpp>
#include <contrib/plugins/processing/classification/src/algorithms/ovpCFeatureExtractionLda.h>
#include <contrib/plugins/processing/signal-processing/src/algorithms/ovpCApplyTemporalFilter.h>
//#include <contrib/plugins/processing/classification/src/algorithms/ovpC>
//#include <Eigen/src/Core/DenseBase.h>
#include <Eigen/Dense>
#include <opencv2/core/eigen.hpp>
using namespace cv;
using namespace Eigen;
using namespace OpenViBE;
using namespace std;
using namespace OpenViBEPlugins;
//using namespace OpenViBEPlugins;
using namespace cv;
//using namespace type

