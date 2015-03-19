//#if defined TARGET_HAS_ThirdPartyITPP
#include "openvibe/include/openvibe/ovCIdentifier.h"

/*
#define OVP_Algorithm_FeatureExtractionLda_InputParameterId_EpochTable                 1
#define OVP_Algorithm_FeatureExtractionLda_InputParameterId_Class1SamplesNumber        1// OpenViBE::CIdentifier(0x7F99FBC6, 0x748B183B)
#define OVP_Algorithm_FeatureExtractionLda_OutputParameterId_MatrixFirstClass          2 //OpenViBE::CIdentifier(0x76F84603, 0x1F5D7A10)
#define OVP_Algorithm_FeatureExtractionLda_OutputParameterId_MatrixSecondClass         3// OpenViBE::CIdentifier(0x0AF9008A, 0xE7A7B2B7)
#define OVP_Algorithm_FeatureExtractionLda_InputTriggerId_Initialize                   1// OpenViBE::CIdentifier(0x06176684, 0x5FA2C7C7)
#define OVP_Algorithm_FeatureExtractionLda_InputTriggerId_ExtractFeature               1// OpenViBE::CIdentifier(0x3B505F2F, 0x024B2C52)
*/



//#include "contrib/plugins/processing/classification/src/ovp_defines.h"
#include "ovpCFeatureExtractionLda.h"
//#include "../ovp_defines.h"
#include "contrib/plugins/processing/classification/src/ovp_defines.h"
using namespace OpenViBE;
using namespace OpenViBE::Kernel;
using namespace OpenViBE::Plugins;

using namespace OpenViBEPlugins;
using namespace OpenViBEPlugins::SignalProcessingGpl;
//using namespace itpp;
using namespace OpenViBEToolkit;
using namespace std;
// ________________________________________________________________________________________________________________
//

boolean CFeatureExtractionLda::initialize(void)
{
	ip_pSignalEpochTable.initialize(getInputParameter(ex));
	//ip_pClass1SamplesNumber.initialize(getInputParameter(OVP_Algorithm_FeatureExtractionLda_InputParameterId_Class1SamplesNumber));

//	op_pMatrixFirstClass.initialize(getOutputParameter(OVP_Algorithm_FeatureExtractionLda_OutputParameterId_MatrixFirstClass));
	//op_pMatrixSecondClass.initialize(getOutputParameter(OVP_Algorithm_FeatureExtractionLda_OutputParameterId_MatrixSecondClass));

	m_uint64IndexTainingSet = 0;
	m_bFirstTime = true;

	return true;
}

boolean CFeatureExtractionLda::uninitialize(void)
{
	op_pMatrixSecondClass.uninitialize();
	op_pMatrixFirstClass.uninitialize();

	ip_pClass1SamplesNumber.uninitialize();
	ip_pSignalEpochTable.uninitialize();

	return true;
}

// ________________________________________________________________________________________________________________
//

boolean CFeatureExtractionLda::process(void)
{
	IMatrix* l_pInputClass1SamplesNumber = ip_pClass1SamplesNumber;
	IMatrix* l_pInputSignalEpochTable = ip_pSignalEpochTable;
	IMatrix* l_pMatrixFirstClass = op_pMatrixFirstClass;
	IMatrix* l_pMatrixSecondClass = op_pMatrixSecondClass;

	float64* l_pFirstClass = NULL;
	float64* l_pSecondClass = NULL;

	if(1)//isInputTriggerActive(OVP_Algorithm_FeatureExtractionLda_InputTriggerId_Initialize))
	{
		m_bFirstTime = true;
		m_uint64IndexTainingSet = 0;
	}

	if(1)//isInputTriggerActive(OVP_Algorithm_FeatureExtractionLda_InputTriggerId_ExtractFeature))
	{
		// input vars
		float64* l_pSignalEpochTable = (float64*)(l_pInputSignalEpochTable->getBuffer());
		float64* l_pClass1SamplesNumber = (float64*)(l_pInputClass1SamplesNumber->getBuffer());

		// dimension of input signal epochs
		uint32 l_ui32InputSignalEpochTableDimensionNbUtterances=ip_pSignalEpochTable->getDimensionSize(0);
		uint32 l_ui32InputSignalEpochTableDimensionSizeEpoch=ip_pSignalEpochTable->getDimensionSize(1);
		uint32 l_ui32InputClass1SamplesNumberDimensionColumn=ip_pClass1SamplesNumber->getDimensionSize(1);

		// output vars
		if (m_bFirstTime)
		{
			l_pMatrixFirstClass->setDimensionCount(2);
			l_pMatrixFirstClass->setDimensionSize(0, l_ui32InputClass1SamplesNumberDimensionColumn);
			l_pMatrixFirstClass->setDimensionSize(1, l_ui32InputSignalEpochTableDimensionSizeEpoch);

			l_pMatrixSecondClass->setDimensionCount(2);
			l_pMatrixSecondClass->setDimensionSize(0, (l_ui32InputSignalEpochTableDimensionNbUtterances-1)*l_ui32InputClass1SamplesNumberDimensionColumn);
			l_pMatrixSecondClass->setDimensionSize(1, l_ui32InputSignalEpochTableDimensionSizeEpoch);

			m_bFirstTime = false;
		}

		l_pFirstClass = l_pMatrixFirstClass->getBuffer();
		l_pSecondClass = l_pMatrixSecondClass->getBuffer();

		uint32 l_ui32TmpIndex = 0;
		for (uint64 i=0; i < l_ui32InputSignalEpochTableDimensionNbUtterances; i++)
		{
			if (i==l_pClass1SamplesNumber[m_uint64IndexTainingSet]-1)
			{
				for (uint64 k=0; k<l_ui32InputSignalEpochTableDimensionSizeEpoch; k++)
				{
					l_pFirstClass[k+m_uint64IndexTainingSet*l_ui32InputSignalEpochTableDimensionSizeEpoch] = l_pSignalEpochTable[k+i*l_ui32InputSignalEpochTableDimensionSizeEpoch];

				}
			}
			else
			{
				for (uint64 k=0; k<l_ui32InputSignalEpochTableDimensionSizeEpoch; k++)
				{
					l_pSecondClass[k+l_ui32TmpIndex*l_ui32InputSignalEpochTableDimensionSizeEpoch+m_uint64IndexTainingSet*(l_ui32InputSignalEpochTableDimensionNbUtterances-1)*l_ui32InputSignalEpochTableDimensionSizeEpoch] = l_pSignalEpochTable[k+i*l_ui32InputSignalEpochTableDimensionSizeEpoch];
				}
				l_ui32TmpIndex++;
			}
		}
		m_uint64IndexTainingSet++;
	}

	return true;
}

//#endif // TARGET_HAS_ThirdPartyITPP
