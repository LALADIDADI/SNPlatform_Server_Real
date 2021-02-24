#include <iostream>
#include <vector>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <time.h>
#include <string.h>
#include <ctype.h>
#include <ctime>
#include <cstdlib>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <algorithm>
#include <gsl/gsl_cdf.h>
#include <gsl/gsl_linalg.h>

#include <direct.h> // 建立标识文件夹使用
#include <io.h> // 建立标识文件夹使用

using namespace std;

FILE* pairwise_sig_result; // File to output significant pairwise SNPs
FILE* high_order_result;

#define FMT_INT64   "%lld"
#define FMT_UINT64   "%llu"
#define FMT_HEX64   "%llx"

typedef long long   int64;
typedef unsigned long long uint64;

#define LengthLongType 64
#define MarginalDistrSNP_Y_DimensionX 2
#define MarginalDistrSNP_Y_DimensionY 4

/* -------------------------------------------- GLOBAL VARIABLES -----------------------------------------------------------*/

vector<vector< double> > interactions;
vector<vector< int > > selectedCombinations;
double** interactionInfos;

static unsigned char wordbits[65536];

static int popcount(uint64 i)
{
	return(wordbits[i & 0xFFFF] + wordbits[(i >> 16) & 0xFFFF] + wordbits[(i >> 32) & 0xFFFF] + wordbits[i >> 48]);
}

// The number of sample,SNP and case,control
int nsample, nsnp;
int ncase, nctrl;

int nlongintcase, nlongintctrl;
int* pMarginalDistrSNP, * pMarginalDistrSNP_Y;

uint64** genocase_c0 = NULL, ** genocase_c1 = NULL, ** genocase_c2 = NULL, ** genocase_c3 = NULL;
uint64** genoctrl_c0 = NULL, ** genoctrl_c1 = NULL, ** genoctrl_c2 = NULL, ** genoctrl_c3 = NULL;

int countOfcandidate;
int topK;
int typeOfSearch;

double bonfe_pari_thres;
double single_sig_chisq, pairwise_sig_chisq;
double* single_chisqs;
double threshold;
double search_stage_chisq;
double sig_threshold;

// Mutual information and conditional mutual information
double mi, mi_c;

// cluster
#define MAX_ROUND_TIME  200     //the max times to cluster
int k;  // the number of cluster 
int* cluster_center;  // center
int* cluster_new_center; // new center
double* mi_center;
int clusterID;
int isContinue;
int* counter;
int** cluster;

//ACO 
int IterCount, nAntCount, kLociSet, nLociSet, kEpiModel, TopKModel;
double* pheromone, * cdf;
double rou, alpha, level;
int** nSelectedLociSet;
int** loci_TopModel;
double* eva_TopModel, * phe_TopLoci;

/* FUNCTION DECLARATIONS */
int bitCount(uint64 i);
//void read_parameter();
void GetDataSize(const char* filename, int* nsample, int* nsnp);
void GetCaseControlSize(const char* filename, int* ncase, int* nctrl);
void readData(const char* filename);
double chi_square(int* selectedSNPSet, int m);

void setpheromone(double level);
void get_toploci();

/*
* 函数声明到此为止，下面是一些我附加的工具类函数
*
*/
static void makeDir(char* path);

/* FUNCTION DEFINITIONS */

int bitCount(uint64 i)
{
	i = i - ((i >> 1) & 0x5555555555555555);
	i = (i & 0x3333333333333333) + ((i >> 2) & 0x3333333333333333);
	i = (i + (i >> 4)) & 0x0f0f0f0f0f0f0f0f;
	i = i + (i >> 8);
	i = i + (i >> 16);
	i = i + (i >> 32);
	return (int)i & 0x7f;
}

int rnd(int uper)
{
	return (rand() % uper);
}
double rnd(int low, double uper)
{
	double p = (rand() / (double)RAND_MAX) * ((uper)-(low)) + (low);
	return (p);
}
int cdf2loci(double x, int start, int end)
{
	if (start + 1 == end)
	{
		return start;
	}
	else
	{
		int temp = (start + end) / 2;
		if (cdf[temp] <= x)
		{
			return cdf2loci(x, temp, end);
		}
		else
		{
			return cdf2loci(x, start, temp);
		}
	}
}
bool cmp(vector<double> a, vector<double> b) {
	return a[3] > b[3];
}
void combine_increase(vector<vector <int> >& comb, int start, int* result, int count, const int NUM, const int arr_len)
{
	int i = 0;
	vector <int> record;
	record.resize(NUM);
	for (i = start; i < arr_len + 1 - count; i++)
	{
		result[count - 1] = i;
		if (count - 1 == 0)
		{
			int j;
			for (j = NUM - 1; j >= 0; j--) {
				record[j] = result[j];
			}
			comb.push_back(record);
		}
		else
			combine_increase(comb, i + 1, result, count - 1, NUM, arr_len);
	}
}

void GetDataSize(const char* filename, int* nsample, int* nsnp)
{
	FILE* fp;
	int c;
	time_t st, ed;
	int n, p, i, flag;

	fp = fopen(filename, "r");

	if (fp == NULL)
	{
		printf("can't open input file %s\n", filename);
		exit(1);
	}
	printf("start getting data size of file : %s\n", filename);
	time(&st);
	//initialization

	n = 0;//samples number

	// find the number of samples: n
	while (1)
	{
		int c = fgetc(fp);//read a character from the data file
		switch (c)
		{
		case '\n'://the end of line
			n++;
			break;
			// fall through,
			// count the '3' element
		case EOF://file end
			goto out;
		default:
			;
		}
	}
out:
	rewind(fp);//Repositions the file pointer to the beginning of a file

	// find number of variables: p
	p = 0;
	i = 0;
	flag = 1;
	while (1)
	{
		c = getc(fp);
		if (c == '\n') goto out2;//end of line
		if (isspace(c))
		{
			flag = 1;
		}

		if (!isspace(c) && (flag == 1))
		{
			p++;//indicate the dimension of the vector
			flag = 0;
		}

	}
out2:
	fclose(fp);

	time(&ed);
	*nsample = n;
	*nsnp = p - 1;


	printf("Data contains %d rows and %d column. \n", n, p);

}

void GetCaseControlSize(const char* filename, int* ncase, int* nctrl)
{
	FILE* fp;
	int i, j, tmp;
	int icase, ictrl;

	fp = fopen(filename, "r");
	if (fp == NULL)
	{
		fprintf(stderr, "can't open input file %s\n", filename);
		exit(3);
	}

	printf("\n");
	std::cout << "start get count of case and control" << std::endl;

	i = 0;
	j = 0;
	icase = 0;
	ictrl = 0;
	while (!feof(fp)) {

		//if (n*j + i == 400)
		//{
		//	printf("%d,%d\n",i,j);
		//}

		/* loop through and store the numbers into the array */
		if (j == 0)
		{
			//j = 0 means read ind class label y
			fscanf(fp, "%d", &tmp);

			if (tmp)
			{
				// tmp=1 means case
				icase++;

			}
			else
			{
				ictrl++;

			}
			j++;
		}
		else
		{
			fscanf(fp, "%d", &tmp);
			j++; //column index
			if (j == (nsnp + 1)) // DataSize[ndataset] is the nsnp in the first dataset
			{
				j = 0;
				i++; // row index
			}

		}

		if (i >= nsample)
		{
			break;
		}
	}
	fclose(fp);
	*nctrl = ictrl;
	*ncase = icase;
	printf("total sample: %d (ncase = %d; nctrl = %d).", nsample, (int)icase, (int)ictrl);

}

void readData(const char* filename)
{
	FILE* fp;
	int i, j, tmp, flag;
	int icase, ictrl;
	uint64 mask1 = 0x0000000000000001;

	i = 0; //row index
	j = 0; // column index


	fp = fopen(filename, "r");
	if (fp == NULL)
	{
		fprintf(stderr, "can't open input file %s\n", filename);
		exit(1);
	}
	icase = 3;
	ictrl = 3;

	while (!feof(fp)) {
		/* loop through and store the numbers into the array */

		if (j == 0)
		{
			//j = 0 means read class label y
			fscanf(fp, "%d", &tmp);

			if (tmp)
			{
				// tmp=1 means case
				icase++;
				flag = 1;
			}
			else
			{
				ictrl++;
				flag = 0;
			}
			j++;
		}
		else
		{
			fscanf(fp, "%d", &tmp);

			if (flag)
			{
				switch (tmp)
				{
				case 0: genocase_c0[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
				case 1: genocase_c1[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
				case 2: genocase_c2[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
				case 3: genocase_c3[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
				default: break;
				}

			}
			else
			{
				switch (tmp)
				{
				case 0: genoctrl_c0[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				case 1: genoctrl_c1[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				case 2: genoctrl_c2[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				case 3: genoctrl_c3[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				default: break;
				}
			}

			j++; //column index
			if (j == (nsnp + 1))
			{
				j = 0;
				i++; // row index
			}

		}

		if (i >= nsample)
		{
			break;
		}
	}

	fclose(fp);

}

/* CLASS DEFINITIONS */
class ant
{
private:
	//double* prob;
	int m_iLociCount;
	//int* AllowedLoci;
public:
	int ant_number;
	int* tabu;

	void initiate();
	void start();
	int ChooseNextLociSet();
	void move();
	void addLociSet(int lociSet);
	void destroy();
};

class seeker
{
public:
	seeker();
	~seeker();
	ant* ants;
	void GetAnt();
	void UpdatePheromone();
	void StartSearch();
};


// chi_square test
double chi_square(int* selectedSNPSet, int m)
{
	int comb = (int)pow(3.0, m);
	double** observedValues;
	double* colSumTable;
	double** expectedValues;
	int i, j, k, value, * index;
	int count;
	uint64* mask1, * mask2;
	double x2 = 0;

	index = new int[m];
	mask1 = new uint64[nlongintcase];
	mask2 = new uint64[nlongintctrl];

	observedValues = new double* [2];
	expectedValues = new double* [2];
	for (i = 0; i < 2; i++)
	{
		observedValues[i] = new double[comb];
		expectedValues[i] = new double[comb];
	}
	colSumTable = new double[comb];
	for (i = 0; i < comb; i++)
	{
		observedValues[0][i] = 0;
		observedValues[1][i] = 0;
		colSumTable[i] = 0;
	}

	for (i = 0; i < comb; i++) {
		value = i;
		for (j = m - 1; j >= 0; j--) {
			index[j] = value / (int)pow(3, j);
			value = value % (int)pow(3, j);
		}

		for (j = 0; j < nlongintcase; j++) {
			mask1[j] = 0xffffffffffffffff;
		}

		count = 0;
		for (j = 0; j < m; j++) {
			for (k = 0; k < nlongintcase; k++) {
				switch (index[j])
				{
				case 0: mask1[k] = mask1[k] & genocase_c0[selectedSNPSet[j]][k]; break;
				case 1: mask1[k] = mask1[k] & genocase_c1[selectedSNPSet[j]][k]; break;
				case 2: mask1[k] = mask1[k] & genocase_c2[selectedSNPSet[j]][k]; break;
				}
			}
		}
		count = 0;
		for (k = 0; k < nlongintcase; k++) {
			count += bitCount(mask1[k]);
		}
		observedValues[1][i] = count;


		for (j = 0; j < nlongintctrl; j++) {
			mask2[j] = 0xffffffffffffffff;
		}
		for (j = 0; j < m; j++) {
			for (k = 0; k < nlongintctrl; k++) {
				switch (index[j])
				{
				case 0: mask2[k] = mask2[k] & genoctrl_c0[selectedSNPSet[j]][k]; break;
				case 1: mask2[k] = mask2[k] & genoctrl_c1[selectedSNPSet[j]][k]; break;
				case 2: mask2[k] = mask2[k] & genoctrl_c2[selectedSNPSet[j]][k]; break;
				}
			}
		}
		count = 0;
		for (k = 0; k < nlongintctrl; k++) {
			count += bitCount(mask2[k]);
		}
		observedValues[0][i] = count;

		colSumTable[i] = observedValues[0][i] + observedValues[1][i];
		expectedValues[0][i] = colSumTable[i] * nctrl / (double)nsample;
		expectedValues[1][i] = colSumTable[i] * ncase / (double)nsample;
		if (expectedValues[0][i] != 0) {
			x2 = x2 + (expectedValues[0][i] - observedValues[0][i]) * (expectedValues[0][i] - observedValues[0][i]) / expectedValues[0][i];
		}
		if (expectedValues[1][i] != 0) {
			x2 = x2 + (expectedValues[1][i] - observedValues[1][i]) * (expectedValues[1][i] - observedValues[1][i]) / expectedValues[1][i];
		}
	}

	for (i = 0; i < 2; i++)
	{
		delete[] expectedValues[i];
		delete[] observedValues[i];
	}
	delete[] expectedValues;
	delete[] observedValues;
	delete[] colSumTable;
	delete[] index;
	delete[] mask1;
	delete[] mask2;
	return x2;
}

void CalculateMarginalDistr(int* pMarginalDistrSNP, int* pMarginalDistrSNP_Y)
{
	int i1, i2, i3;
	int count0, count1, count2, count3;

	int GenoMarginalDistr[4][2];

	for (i1 = 0; i1 < nsnp; i1++)
	{

		count0 = 0;
		count1 = 0;
		count2 = 0;
		count3 = 0;
		for (i3 = 0; i3 < nlongintcase; i3++)
		{
			count0 += bitCount(genocase_c0[i1][i3]);
			count1 += bitCount(genocase_c1[i1][i3]);
			count2 += bitCount(genocase_c2[i1][i3]);
			count3 += bitCount(genocase_c3[i1][i3]);
		}
		GenoMarginalDistr[0][0] = count0;
		GenoMarginalDistr[1][0] = count1;
		GenoMarginalDistr[2][0] = count2;
		GenoMarginalDistr[3][0] = count3;

		pMarginalDistrSNP_Y[(0 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count0;
		pMarginalDistrSNP_Y[(1 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count1;
		pMarginalDistrSNP_Y[(2 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count2;
		pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count3;

		count0 = 0;
		count1 = 0;
		count2 = 0;
		count3 = 0;
		for (i3 = 0; i3 < nlongintctrl; i3++)
		{
			count0 += bitCount(genoctrl_c0[i1][i3]);
			count1 += bitCount(genoctrl_c1[i1][i3]);
			count2 += bitCount(genoctrl_c2[i1][i3]);
			count3 += bitCount(genoctrl_c3[i1][i3]);
		}
		GenoMarginalDistr[0][1] = count0;
		GenoMarginalDistr[1][1] = count1;
		GenoMarginalDistr[2][1] = count2;
		GenoMarginalDistr[3][1] = count3;

		pMarginalDistrSNP_Y[(0 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count0;
		pMarginalDistrSNP_Y[(1 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count1;
		pMarginalDistrSNP_Y[(2 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count2;
		pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count3;


		for (i2 = 0; i2 < 4; i2++)
		{
			pMarginalDistrSNP[i2 * nsnp + i1] =
				pMarginalDistrSNP_Y[(i2 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] +
				pMarginalDistrSNP_Y[(i2 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1];

			//fprintf(snp_number_result, "%d\t%d\t%d\n", i1, i2, pMarginalDistrSNP_Y[(i2*MarginalDistrSNP_Y_DimensionX + 1)*nsnp + i1]);
		}
	}
}

double Calculate_MI(int j1, int j2)
{
	if (j1 != j2) {
		uint64 x = 0, * x_c00 = NULL, * x_c01 = NULL, * x_c02 = NULL, * x_c03 = NULL, * x_c10 = NULL, * x_c11 = NULL, * x_c12 = NULL, * x_c13 = NULL;
		uint64* y_c00 = NULL, * y_c01 = NULL, * y_c02 = NULL, * y_c03 = NULL, * y_c10 = NULL, * y_c11 = NULL, * y_c12 = NULL, * y_c13 = NULL;

		int m, n, h;
		double p, p1, p2;
		int casefreq[4][4], ctrlfreq[4][4];
		int freq[4][4];

		mi = 0;

		x_c00 = genocase_c0[j1]; x_c01 = genocase_c1[j1];
		x_c02 = genocase_c2[j1]; x_c03 = genocase_c3[j1];

		x_c10 = genoctrl_c0[j1]; x_c11 = genoctrl_c1[j1];
		x_c12 = genoctrl_c2[j1]; x_c13 = genoctrl_c3[j1];

		y_c00 = genocase_c0[j2]; y_c01 = genocase_c1[j2];
		y_c02 = genocase_c2[j2]; y_c03 = genocase_c3[j2];

		y_c10 = genoctrl_c0[j2]; y_c11 = genoctrl_c1[j2];
		y_c12 = genoctrl_c2[j2]; y_c13 = genoctrl_c3[j2];

		// reset casefreq[4][4] ctrlfreq[4][4] all to 0
		for (m = 0; m < 4; m++)
			for (n = 0; n < 4; n++)
			{
				casefreq[m][n] = 0;
				ctrlfreq[m][n] = 0;
			}

		for (h = 0; h < nlongintcase; h++) {

			x = x_c00[h] & y_c00[h]; casefreq[0][0] += popcount(x);
			x = x_c00[h] & y_c01[h]; casefreq[0][1] += popcount(x);
			x = x_c00[h] & y_c02[h]; casefreq[0][2] += popcount(x);
			x = x_c01[h] & y_c00[h]; casefreq[1][0] += popcount(x);
			x = x_c01[h] & y_c01[h]; casefreq[1][1] += popcount(x);
			x = x_c01[h] & y_c02[h]; casefreq[1][2] += popcount(x);
			x = x_c02[h] & y_c00[h]; casefreq[2][0] += popcount(x);
			x = x_c02[h] & y_c01[h]; casefreq[2][1] += popcount(x);
			x = x_c02[h] & y_c02[h]; casefreq[2][2] += popcount(x);
		}

		for (h = 0; h < nlongintctrl; h++) {

			x = x_c10[h] & y_c10[h]; ctrlfreq[0][0] += popcount(x);
			x = x_c10[h] & y_c11[h]; ctrlfreq[0][1] += popcount(x);
			x = x_c10[h] & y_c12[h]; ctrlfreq[0][2] += popcount(x);
			x = x_c11[h] & y_c10[h]; ctrlfreq[1][0] += popcount(x);
			x = x_c11[h] & y_c11[h]; ctrlfreq[1][1] += popcount(x);
			x = x_c11[h] & y_c12[h]; ctrlfreq[1][2] += popcount(x);
			x = x_c12[h] & y_c10[h]; ctrlfreq[2][0] += popcount(x);
			x = x_c12[h] & y_c11[h]; ctrlfreq[2][1] += popcount(x);
			x = x_c12[h] & y_c12[h]; ctrlfreq[2][2] += popcount(x);

		}
		for (m = 0; m < 3; m++) {
			for (n = 0; n < 3; n++) {
				freq[m][n] = casefreq[m][n] + ctrlfreq[m][n];
			}
		}

		for (m = 0; m < 3; m++)
		{
			casefreq[m][3] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - casefreq[m][0] - casefreq[m][1] - casefreq[m][2];
			ctrlfreq[m][3] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j1] - ctrlfreq[m][0] - ctrlfreq[m][1] - ctrlfreq[m][2];
			freq[m][3] = casefreq[m][3] + ctrlfreq[m][3];
			casefreq[3][m] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j2] - casefreq[0][m] - casefreq[1][m] - casefreq[2][m];
			ctrlfreq[3][m] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j2] - ctrlfreq[0][m] - ctrlfreq[1][m] - ctrlfreq[2][m];
			freq[3][m] = casefreq[3][m] + ctrlfreq[3][m];
		}
		casefreq[3][3] = pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - casefreq[3][0] - casefreq[3][1] - casefreq[3][2];
		ctrlfreq[3][3] = pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - ctrlfreq[3][0] - ctrlfreq[3][1] - ctrlfreq[3][2];
		freq[3][3] = casefreq[3][3] + ctrlfreq[3][3];

		for (m = 0; m < 4; m++) {
			for (n = 0; n < 4; n++) {

				p = freq[m][n] / double(nsample);

				p1 = pMarginalDistrSNP[m * nsnp + j1] / double(nsample);
				p2 = pMarginalDistrSNP[n * nsnp + j2] / double(nsample);

				if (freq[m][n] == 0) {
					mi += 0;
				}
				else
					mi += p * log2(p / (p1 * p2));
			}
		}
		return mi;
	}

	else {
		mi = 1;
		return mi;
	}
}

double Calculate_cMI(int j1, int j2)
{
	if (j1 != j2) {
		uint64 x = 0, * x_c00 = NULL, * x_c01 = NULL, * x_c02 = NULL, * x_c03 = NULL, * x_c10 = NULL, * x_c11 = NULL, * x_c12 = NULL, * x_c13 = NULL;
		uint64* y_c00 = NULL, * y_c01 = NULL, * y_c02 = NULL, * y_c03 = NULL, * y_c10 = NULL, * y_c11 = NULL, * y_c12 = NULL, * y_c13 = NULL;

		int m, n, h;
		double p, p1, p2;
		double s_c;
		int casefreq[4][4], ctrlfreq[4][4];

		mi_c = 0;

		x_c00 = genocase_c0[j1]; x_c01 = genocase_c1[j1];
		x_c02 = genocase_c2[j1]; x_c03 = genocase_c3[j1];

		x_c10 = genoctrl_c0[j1]; x_c11 = genoctrl_c1[j1];
		x_c12 = genoctrl_c2[j1]; x_c13 = genoctrl_c3[j1];

		y_c00 = genocase_c0[j2]; y_c01 = genocase_c1[j2];
		y_c02 = genocase_c2[j2]; y_c03 = genocase_c3[j2];

		y_c10 = genoctrl_c0[j2]; y_c11 = genoctrl_c1[j2];
		y_c12 = genoctrl_c2[j2]; y_c13 = genoctrl_c3[j2];

		/* reset casefreq[4][4] ctrlfreq[4][4] all to 0 */
		for (m = 0; m < 4; m++)
			for (n = 0; n < 4; n++)
			{
				casefreq[m][n] = 0;
				ctrlfreq[m][n] = 0;
			}

		for (h = 0; h < nlongintcase; h++) {

			x = x_c00[h] & y_c00[h]; casefreq[0][0] += popcount(x);
			x = x_c00[h] & y_c01[h]; casefreq[0][1] += popcount(x);
			x = x_c00[h] & y_c02[h]; casefreq[0][2] += popcount(x);
			x = x_c01[h] & y_c00[h]; casefreq[1][0] += popcount(x);
			x = x_c01[h] & y_c01[h]; casefreq[1][1] += popcount(x);
			x = x_c01[h] & y_c02[h]; casefreq[1][2] += popcount(x);
			x = x_c02[h] & y_c00[h]; casefreq[2][0] += popcount(x);
			x = x_c02[h] & y_c01[h]; casefreq[2][1] += popcount(x);
			x = x_c02[h] & y_c02[h]; casefreq[2][2] += popcount(x);
		}

		for (h = 0; h < nlongintctrl; h++) {

			x = x_c10[h] & y_c10[h]; ctrlfreq[0][0] += popcount(x);
			x = x_c10[h] & y_c11[h]; ctrlfreq[0][1] += popcount(x);
			x = x_c10[h] & y_c12[h]; ctrlfreq[0][2] += popcount(x);
			x = x_c11[h] & y_c10[h]; ctrlfreq[1][0] += popcount(x);
			x = x_c11[h] & y_c11[h]; ctrlfreq[1][1] += popcount(x);
			x = x_c11[h] & y_c12[h]; ctrlfreq[1][2] += popcount(x);
			x = x_c12[h] & y_c10[h]; ctrlfreq[2][0] += popcount(x);
			x = x_c12[h] & y_c11[h]; ctrlfreq[2][1] += popcount(x);
			x = x_c12[h] & y_c12[h]; ctrlfreq[2][2] += popcount(x);

		}

		for (m = 0; m < 3; m++)
		{
			casefreq[m][3] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - casefreq[m][0] - casefreq[m][1] - casefreq[m][2];
			ctrlfreq[m][3] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j1] - ctrlfreq[m][0] - ctrlfreq[m][1] - ctrlfreq[m][2];
			casefreq[3][m] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j2] - casefreq[0][m] - casefreq[1][m] - casefreq[2][m];
			ctrlfreq[3][m] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j2] - ctrlfreq[0][m] - ctrlfreq[1][m] - ctrlfreq[2][m];
		}
		casefreq[3][3] = pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - casefreq[3][0] - casefreq[3][1] - casefreq[3][2];
		ctrlfreq[3][3] = pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - ctrlfreq[3][0] - ctrlfreq[3][1] - ctrlfreq[3][2];

		for (m = 0; m < 4; m++) {
			for (n = 0; n < 4; n++) {

				s_c = 0;
				p = casefreq[m][n] / double(ncase);

				p1 = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j1] / double(ncase);
				p2 = pMarginalDistrSNP_Y[(n * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j2] / double(ncase);

				if (p == 0 || p1 == 0 || p2 == 0) {
					mi_c += 0;
				}

				else {
					s_c = p * log2(p / (p1 * p2));
					if (s_c < 0) mi_c += (-s_c);
					else mi_c += s_c;
				}

			}
		}
		return mi_c;
	}

	else {
		mi_c = 1;
		return mi_c;
	}
}

long int comb_num(int m, int n)
{
	long int i, p, q;
	p = 1;
	q = 1;
	for (i = 1; i <= n; i++)
	{
		p = p * i;
		q = q * (m - i + 1);
	}
	return q / p;
}

/*  CLUSTERING FUNCTION  */

void cluster_ini() {
	cluster_center = (int*)malloc(sizeof(int) * (k * (MAX_ROUND_TIME + 1)));
	if (!cluster_center) {
		printf("malloc error:cluster_center!\n");
		exit(0);
	}

	mi_center = (double*)malloc(sizeof(double) * (k * (MAX_ROUND_TIME + 1)));
	if (!mi_center) {
		printf("malloc error: mi_center!\n");
		exit(0);
	}

	cluster_new_center = (int*)malloc(sizeof(int) * (k * (MAX_ROUND_TIME + 1)));
	if (!cluster_new_center) {
		printf("malloc error:cluster_new_center!\n");
		exit(0);
	}

	counter = (int*)malloc(sizeof(int) * (k * (MAX_ROUND_TIME + 1)));
	if (!counter) {
		printf("malloc error: counter!\n");
		exit(0);
	}

	cluster = (int**)malloc(sizeof(int*) * (k + 1));
	for (int i = 0; i < k; i++) {
		cluster[i] = (int*)malloc(sizeof(int) * (nsnp + 1));
	}
	if (!cluster) {
		printf("malloc error: cluster!\n");
		exit(0);
	}

}

void init_cluster() {
	int i;

	int random_int;
	for (i = 0; i < k; i++) {
		random_int = rand() % (nsnp - 1);
		cluster_center[i] = random_int;
	}
	printf("\n");

	for (i = 0; i < k; i++) {
		cluster_new_center[i] = cluster_center[i];
		//clusterID = i;
	}
}

void partion_allsnp(int round) {
	char filename[58866];
	double max;
	int max_k;
	FILE** file;
	file = (FILE**)malloc(sizeof(FILE*) * (k + 1));
	if (!file) {
		printf("malloc file error!\n");
		exit(0);
	}

	// initialize counter to 0
	for (int i = 0; i < k; i++) {
		counter[i] = 0;
	}

	/*
	for (int i = 0; i < k; i++){
		//sprintf(filename, "result/result_round%d_cluster%d.txt", round, i);
		if (NULL == (file[i] = fopen(filename, "w"))){
			printf("file open(%s) error!", filename);
			exit(0);
		}
	}
	*/

	for (int i = 0; i < nsnp; i++) {
		for (int j = 0; j < k; j++) {
			mi_center[j] = Calculate_MI(i, cluster_new_center[j]);
		}

		if ((mi_center[0] > mi_center[1]) & (mi_center[0] > mi_center[2])) {
			cluster[0][counter[0]] = i;
			counter[0]++;

		}
		if ((mi_center[1] > mi_center[0]) & (mi_center[1] > mi_center[2])) {
			cluster[1][counter[1]] = i;
			counter[1]++;

		}
		if ((mi_center[2] > mi_center[0]) & (mi_center[2] > mi_center[1])) {
			cluster[2][counter[2]] = i;
			counter[2]++;

		}
	}

	//for (int i = 0; i < k; i++){
		//fclose(file[i]);
	//}

}

void compareNew_Oldcenter(int* new_center) {
	isContinue = 0;

	for (int i = 0; i < k; i++) {
		if (new_center[i] != cluster_new_center[i]) {
			isContinue = 1;
			break;
		}
	}
}

void cluster_center_file(int round) {
	FILE* file;
	int i;
	char filename[58866];
	sprintf(filename, "round%d_cluster_center.txt", round);
	if (NULL == (file = fopen(filename, "w"))) {
		printf("open file(%s) error!\n", filename);
		exit(0);
	}

	for (i = 0; i < k; i++) {
		fclose(file);
	}
}

void calculate_new_center(int round) {
	int i, j, j1, j2;
	double sum1, sum2;
	int m = 0;

	//int *snps = new int[2];
	int random_data;
	int* random;
	random = (int*)malloc(sizeof(int) * (k + 1));
	if (!random) {
		printf("malloc error: random\n");
		exit(0);
	}

	int* new_center;  // calculate and save new cluster center
	new_center = (int*)malloc(sizeof(int) * (k + 1));
	if (!new_center) {
		printf("malloc error: new_center!\n");
		exit(0);
	}

	//selecting sequentially a snp as new center in each cluster, compare the sum of mutual information with old center
	for (i = 0; i < k; i++) {
		j1 = 0;
		do {
			sum1 = sum2 = 0.0;
			random[i] = cluster[i][j1];
			for (j2 = 0; j2 < counter[i]; j2++) {
				sum1 += Calculate_MI(cluster[i][j2], cluster_new_center[i]);   // old
				sum2 += Calculate_MI(cluster[i][j2], random[i]);              // new
			}
			j1++;
		} while (sum1 > sum2);

		new_center[i] = random[i];

	}

	compareNew_Oldcenter(new_center);

	for (i = 0; i < k; i++) {
		cluster_new_center[i] = new_center[i];
		//clusterID = i;
	}

	free(random);
	free(new_center);
}

// k-medodis 
void k_medodis() {
	srand((unsigned)time(NULL));
	cluster_ini();
	init_cluster();
	vector < int > record;
	record.resize(2);

	int i, j, j1, j2;

	int* snps = new int[2];
	double p, pvalue;

	for (int cluster_times = 0; cluster_times < MAX_ROUND_TIME; cluster_times++) {
		partion_allsnp(cluster_times);
		calculate_new_center(cluster_times);
		if (0 == isContinue) {
			break;
		}
	}

	// screening significant SNP pairwises in each cluster
	for (i = 0; i < k; i++) {
		for (j1 = 0; j1 < counter[i] - 1; j1++) {
			for (j2 = j1 + 1; j2 < counter[i]; j2++) {
				snps[0] = cluster[i][j1];
				snps[1] = cluster[i][j2];

				if (Calculate_cMI(cluster[i][j1], cluster[i][j2]) > 0.2) {
					record[0] = cluster[i][j1];
					record[1] = cluster[i][j2];
					countOfcandidate++;
					selectedCombinations.push_back(record);
				}
			}
		}
	}

	// free 
	for (int i = 0; i < k; i++) {
		free((void*)cluster[i]);
	}
	free((void*)cluster);

	free(cluster_center);
	free(mi_center);
	free(cluster_new_center);
	free(counter);

}


/* ACO FUNCTION */

void ant::initiate()
{
	tabu = new int[kLociSet + 1];

}
void ant::start()
{
	m_iLociCount = 0;

}
int ant::ChooseNextLociSet()
{
	int i, j, k;
	j = 3;
	double mRate;

	while (j == 3)
	{
		j = 0;
		srand((unsigned)time(NULL) + rand());
		mRate = rnd(0, 1);
		k = cdf2loci(mRate, 0, nLociSet);
		for (i = 0; i < m_iLociCount; i++)
		{
			if (k == tabu[i])
			{
				j = 3;
				break;
			}
		}
	}
	return k;
}
void ant::addLociSet(int lociSet)
{
	tabu[m_iLociCount] = lociSet;
	m_iLociCount++;
}
void ant::move()
{
	int j;
	j = ChooseNextLociSet();
	addLociSet(j);
}
void ant::destroy()
{
	delete[] tabu;
}
seeker::seeker()
{
	ants = new ant[nAntCount];
	int i;
	for (i = 0; i < nAntCount; i++)
	{
		ants[i].initiate();
	}
}
seeker::~seeker()
{
	delete[] ants;
}
void seeker::UpdatePheromone()
{
	int i, j, k, tag, flag;
	for (i = 0; i < nLociSet; i++)
		pheromone[i] = pheromone[i] * (1 - rou);
	double eva;
	int* tmp;
	int* locidata;

	vector<int> loci;

	locidata = new int[kLociSet * 2];
	for (i = 0; i < kLociSet * 2; i++)
		locidata[i] = 3;

	for (i = 0; i < nAntCount; i++)
	{
		loci.clear();
		flag = 0;
		loci.push_back(nSelectedLociSet[ants[i].tabu[1]][0]);
		if (nSelectedLociSet[ants[i].tabu[1]][1] != 3) {
			flag = 1;
			loci.push_back(nSelectedLociSet[ants[i].tabu[1]][1]);
		}
		for (j = 0; j < 2; j++) {
			switch (flag) {
			case 1:
				if (nSelectedLociSet[ants[i].tabu[2]][j] != loci[0] && nSelectedLociSet[ants[i].tabu[2]][j] != loci[1] && nSelectedLociSet[ants[i].tabu[2]][j] != 3) {
					loci.push_back(nSelectedLociSet[ants[i].tabu[2]][j]);
				}
				break;
			case 0:
				if (nSelectedLociSet[ants[i].tabu[2]][j] != loci[0] && nSelectedLociSet[ants[i].tabu[2]][j] != 3) {
					loci.push_back(nSelectedLociSet[ants[i].tabu[2]][j]);
				}
				break;
			}
		}
		tmp = new int[loci.size()];
		for (k = 0; k < loci.size(); k++) {
			tmp[k] = loci[k];
			locidata[k] = loci[k];
		}

		eva = chi_square(tmp, loci.size()) / 100;

		if (loci.size() == 3) {
			locidata[3] = 3;
			eva = eva * 2;
		}
		else if (loci.size() == 2) {
			locidata[3] = locidata[2] = 3;
			eva = eva * 2;
		}


		for (j = 0; j < kLociSet; j++)
		{
			pheromone[ants[i].tabu[j + 1]] += eva;
		}
		if (eva > eva_TopModel[0])
		{
			tag = 1;
			for (j = 0; j < TopKModel; j++)
			{
				if (fabs(eva - eva_TopModel[j]) < 0.000001)
				{
					tag = 0;
					break;
				}
			}
			if (tag)
			{
				eva_TopModel[0] = eva_TopModel[1];
				for (j = 0; j < 2 * kLociSet; j++)
					loci_TopModel[0][j] = loci_TopModel[1][j];
				k = 1;
				while (k<TopKModel && eva>eva_TopModel[k])
				{
					eva_TopModel[k - 1] = eva_TopModel[k];
					for (j = 0; j < 2 * kLociSet; j++)
						loci_TopModel[k - 1][j] = loci_TopModel[k][j];
					k++;
				}
				eva_TopModel[k - 1] = eva;
				for (j = 0; j < 2 * kLociSet; j++)
					loci_TopModel[k - 1][j] = locidata[j];
			}
		}
	}
	//#################update cdf##################
	double* prob;
	double temp = 0;
	prob = new double[nLociSet];
	for (i = 0; i < nLociSet; i++)
	{
		temp += pow(pheromone[i], alpha);
		prob[i] = temp;
	}
	for (i = 0; i < nLociSet - 1; i++)
	{
		cdf[i + 1] = prob[i] / temp;
	}
	delete[] prob;
	delete[] locidata;
}
void seeker::GetAnt()
{
	int i = 0;
	int loci;
	srand((unsigned)time(NULL) + rand());
	for (i = 0; i < nAntCount; i++)
	{
		ants[i].start();
		loci = rnd(nLociSet);
		//locus=ChooseNextLocus(nLoci,double *cdf);
		ants[i].ant_number = i;
		ants[i].addLociSet(loci);
	}
}
void seeker::StartSearch() {
	int max, i, j;
	max = 0;
	//double temp;
	while (max < IterCount)
		//while(max<1)
	{
		cout << "Iteration: " << max << endl;
		GetAnt();
		for (i = 0; i < nAntCount; i++)
		{
			for (j = 1; j <= kLociSet; j++)
			{
				ants[i].move();
			}
		}
		UpdatePheromone();
		max++;
	}
}
void setpheromone(double level)
{
	int i;
	cdf[0] = 0;
	//FILE *fp;
	for (i = 0; i < nLociSet; i++) {
		pheromone[i] = level;
		cdf[i + 1] = double(i + 1) / double(nLociSet);
	}
}


/*  HIGH-ORDER SEARCH   */

void postprocessing()
{
	long int i, num;
	int j, k, s, isNew;
	unsigned int l;
	double eva, p_value;
	int* loci;
	int* index = new int[kEpiModel];
	vector <vector <int> > combination;
	loci = new int[kEpiModel];
	vector<double> record;
	record.resize(kEpiModel + 2);
	cout << "Post-processing" << endl;
	cout << "Dealing with top ranking SNP sets" << endl;
	num = comb_num(2 * kLociSet, kEpiModel);
	cout << "Number of interatcions evaluated: " << num * TopKModel << endl;

	combine_increase(combination, 0, index, kEpiModel, kEpiModel, 2 * kLociSet);

	for (i = TopKModel - 1; i >= 0; i--)
	{
		if (loci_TopModel[i][3] == 3) {
			if (loci_TopModel[i][2] == 3) {
				for (k = 0; k < 2; k++)
					loci[k] = loci_TopModel[i][k];
			}
			else {
				for (k = 0; k < 3; k++)
					loci[k] = loci_TopModel[i][k];
			}

			eva = chi_square(loci, k);
			p_value = 1 - gsl_cdf_chisq_P(eva, pow(3, k) - 1);
			if (p_value < sig_threshold * comb_num(nsnp, 3))
			{
				isNew = 1;
				for (l = 0; l < interactions.size(); l++)
				{
					if (fabs(eva - interactions[l][kEpiModel]) < 0.00000001)
					{
						isNew = 0;
						break;
					}
				}
				if (isNew)
				{
					for (s = 0; s < kEpiModel; s++)
					{
						record[s] = (double)loci[s];
					}
					record[kEpiModel] = eva;
					record[kEpiModel + 1] = p_value;
					interactions.push_back(record);
				}
			}

		}
		else
		{
			for (j = 0; j < num; j++)
			{
				for (k = 0; k < kEpiModel; k++)
				{
					loci[k] = loci_TopModel[i][combination[j][k]];

				}
				eva = chi_square(loci, kEpiModel);
				p_value = 1 - gsl_cdf_chisq_P(eva, pow(3, kEpiModel) - 1);
				if (p_value < sig_threshold * 6 / (nsnp * (nsnp - 1) * (nsnp - 2)))
				{
					isNew = 1;
					for (l = 0; l < interactions.size(); l++)
					{
						if (fabs(eva - interactions[l][kEpiModel]) < 0.00000001)
						{
							isNew = 0;
							break;
						}
					}
					if (isNew)
					{
						for (s = 0; s < kEpiModel; s++)
						{
							record[s] = (double)loci[s];
						}
						record[kEpiModel] = eva;
						record[kEpiModel + 1] = p_value;
						interactions.push_back(record);
					}
				}
			}
		}
	}

	vector<vector<int> >().swap(combination);

}

void runExhuastiveSearch() {

	int snp3[3];
	int i, j, k, l, tag = 0;
	double chisq;

	// exhaustively search
	for (i = 0; i < selectedCombinations.size(); i++) {
		for (j = 0; j < nsnp; j++) {
			snp3[0] = (int)selectedCombinations[i][0];
			snp3[1] = (int)selectedCombinations[i][1];
			if (j != snp3[0] && j != (int)snp3[1]) {
				snp3[2] = j;
				sort(snp3, snp3 + 3);
				chisq = chi_square(snp3, 3);
				if (chisq > interactionInfos[0][3]) {
					tag = 1;
					for (k = 0; k < topK; k++)
					{
						if (fabs(chisq - interactionInfos[k][3]) < 0.000001)
						{
							tag = 0;
							break;
						}
					}
					if (tag)
					{
						interactionInfos[0][3] = interactionInfos[1][3];
						for (k = 0; k < 5; k++)
							interactionInfos[0][k] = interactionInfos[1][k];
						k = 1;
						while (k<topK && chisq> interactionInfos[k][3])
						{
							for (l = 0; l < 5; l++)
								interactionInfos[k - 1][l] = interactionInfos[k][l];
							k++;
						}
						interactionInfos[k - 1][3] = chisq;
						interactionInfos[k - 1][4] = 1 - gsl_cdf_chisq_P(chisq, 8);
						for (l = 0; l < 3; l++)
							interactionInfos[k - 1][l] = snp3[l];
					}
				}
			}
		}
	}
};

void runACObasedSearch() {
	int i, j;
	nLociSet = selectedCombinations.size();
	nSelectedLociSet = (int**)malloc(nLociSet * sizeof(int*));
	for (i = 0; i < nLociSet; i++) {
		nSelectedLociSet[i] = (int*)malloc(2 * sizeof(int));
		nSelectedLociSet[i][0] = selectedCombinations[i][0];
		nSelectedLociSet[i][1] = selectedCombinations[i][1];
	}
	loci_TopModel = (int**)calloc(TopKModel, sizeof(int*));
	for (i = 0; i < TopKModel; i++)
		loci_TopModel[i] = (int*)calloc(2 * kLociSet, sizeof(int));
	eva_TopModel = (double*)calloc(TopKModel, sizeof(double));
	//loci_TopLoci=(int *)calloc(TopKLoci,sizeof(int));
	//phe_TopLoci=(double *)calloc(TopKLoci,sizeof(double));
	pheromone = (double*)calloc(nLociSet, sizeof(double));
	cdf = (double*)calloc(nLociSet + 1, sizeof(double));

	for (i = 0; i < TopKModel; i++) {
		for (j = 0; j < 2 * kLociSet; j++)
		{
			loci_TopModel[i][j] = 3;
		}
	}

	setpheromone(level);

	seeker episeeker;
	episeeker.StartSearch();
	postprocessing();

}

/*
* 我新加的属性和函数就在这吧
*
*/

// 建立标识文件夹的方法
static void makeDir(string folderPath) {
	if (0 != _access(folderPath.c_str(), 0))
	{
		// if this folder not exist, create a new one.
		_mkdir(folderPath.c_str());   // 返回 0 表示创建成功，-1 表示失败
	}
}

// 请求号
const char* queryId = "00000000000000";
string stringQueryId;

// 常量
const char* inputDataPath = "G:/SNPalgorithm/ClusterMI/inputData/data.txt";
const char* resultDataPath = "G:/SNPalgorithm/ClusterMI/resultData/high_order_result.txt";
const string prefixPath = "D:/SNPPlatfromData/";
const string highOrder = "high-order.txt";

// 完整返回文件路径
string stringHighOrder;
string stringFinished;

// 将文件路径由string转为char*;
const char* highOrderSig;


int main(int argc, char* argv[]) {
	int i;
	int single_sig_count;

	// parameter defining
	// search approach: 0: Exhaustive search  1: ACO search
	typeOfSearch = 0;
	alpha = 1;
	sig_threshold = 0.05;
	topK = 100;

	// ACO
	rou = 0.01;
	level = 500;
	nAntCount = 1000;
	IterCount = 100;
	kLociSet = 2;
	kEpiModel = 3;
	TopKModel = 1000;

	// clustering 
	k = 3;
	/*
	* 2021.02.23 新建的属性和方法就在这下面吧
	* 
	* 传入12个计算相关的参数，2个返回文件相关的参数，分别是完整inputDataPath路径和请求号
	* 返回的结果文件统一命名，不需传参
	*/

	// 等待测试
	if (argc > 1) {
		typeOfSearch = atoi(argv[1]);
		alpha = strtod(argv[2], NULL);
		sig_threshold = strtod(argv[3], NULL);
		topK = atoi(argv[4]);
		rou = strtod(argv[5], NULL);
		level = strtod(argv[6], NULL);
		nAntCount = atoi(argv[7]);
		IterCount = atoi(argv[8]);
		kLociSet = atoi(argv[9]);
		kEpiModel = atoi(argv[10]);
		TopKModel = atoi(argv[11]);
		k = atoi(argv[12]); // clustering

		inputDataPath = argv[13];
		queryId = argv[14];

		// char*和string转来转去
		stringQueryId = queryId;

		// 完整返回文件路径
		stringHighOrder = prefixPath + stringQueryId + "/resultData/" + highOrder;
		stringFinished = prefixPath + stringQueryId + "/haveFinished";

		// 将完整文件路径转回char*,以匹配函数参数
		highOrderSig = stringHighOrder.data();
		cout << "三阶：" << highOrderSig << endl;

	}



	/*分割线*/


	high_order_result = fopen(highOrderSig, "w");

	fprintf(high_order_result, "SNP1\tSNP2\tSNP3\tchi2\tP-value\n");

	for (i = 0; i < 65536; i++)
	{
		wordbits[i] = bitCount(i);
	}

	GetDataSize(inputDataPath, &nsample, &nsnp);
	GetCaseControlSize(inputDataPath, &ncase, &nctrl);

	nlongintcase = (int)ceil(((double)ncase) / LengthLongType);
	nlongintctrl = (int)ceil(((double)nctrl) / LengthLongType);

	pMarginalDistrSNP = (int*)malloc(MarginalDistrSNP_Y_DimensionY * nsnp * sizeof(int));
	pMarginalDistrSNP_Y = (int*)malloc(MarginalDistrSNP_Y_DimensionY * MarginalDistrSNP_Y_DimensionX * nsnp * sizeof(int));

	//calloc memory for bit representation
	genocase_c0 = (uint64**)calloc(nsnp, sizeof(uint64*));
	genocase_c1 = (uint64**)calloc(nsnp, sizeof(uint64*));
	genocase_c2 = (uint64**)calloc(nsnp, sizeof(uint64*));
	genocase_c3 = (uint64**)calloc(nsnp, sizeof(uint64*));

	genoctrl_c0 = (uint64**)calloc(nsnp, sizeof(uint64*));
	genoctrl_c1 = (uint64**)calloc(nsnp, sizeof(uint64*));
	genoctrl_c2 = (uint64**)calloc(nsnp, sizeof(uint64*));
	genoctrl_c3 = (uint64**)calloc(nsnp, sizeof(uint64*));
	for (i = 0; i < nsnp; i++) {
		genocase_c0[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
		genocase_c1[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
		genocase_c2[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
		genocase_c3[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));

		genoctrl_c0[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
		genoctrl_c1[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
		genoctrl_c2[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
		genoctrl_c3[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
	}

	readData(inputDataPath);
	CalculateMarginalDistr(pMarginalDistrSNP, pMarginalDistrSNP_Y);

	bonfe_pari_thres = sig_threshold / comb_num(nsnp, 2);

	single_sig_chisq = gsl_cdf_chisq_Qinv(sig_threshold / nsnp, 2);
	pairwise_sig_chisq = gsl_cdf_chisq_Qinv(bonfe_pari_thres, 8);
	search_stage_chisq = gsl_cdf_chisq_Qinv(sig_threshold / comb_num(nsnp, kEpiModel), pow(3, kEpiModel) - 1);

	// clustering 
	k_medodis();

	printf("\n");
	printf("Search high-order SNP interactions......\n");
	//search high-order interactions via ACO-based search or exhuastive search
	if (typeOfSearch == 0) {
		interactionInfos = (double**)calloc(topK, sizeof(double));
		for (i = 0; i < topK; i++)
			interactionInfos[i] = (double*)calloc(kEpiModel + 2, sizeof(double));
		runExhuastiveSearch();
		for (i = topK - 1; i >= 0; i--)
			fprintf(high_order_result, "%d\t%d\t%d\t%f\t%g\n", (int)interactionInfos[i][0], (int)interactionInfos[i][1], (int)interactionInfos[i][2], interactionInfos[i][3], interactionInfos[i][4]);
		for (i = 0; i < topK; i++)
			free(interactionInfos[i]);
		free(interactionInfos);
	}
	else if (typeOfSearch == 1) {
		runACObasedSearch();
		sort(interactions.begin(), interactions.end(), cmp);
		if (interactions.size() < topK) {
			for (i = 0; i < interactions.size(); i++)
				fprintf(high_order_result, "%d\t%d\t%d\t%f\t%g\n", (int)interactions[i][0], (int)interactions[i][1], (int)interactions[i][2], interactions[i][3], interactions[i][4]);
			vector<vector<double> >().swap(interactions);
		}
		else {
			for (i = 0; i < topK; i++)
				fprintf(high_order_result, "%d\t%d\t%d\t%f\t%g\n", (int)interactions[i][0], (int)interactions[i][1], (int)interactions[i][2], interactions[i][3], interactions[i][4]);
			vector<vector<double> >().swap(interactions);
		}
	}
	else {
		printf("chose a wrong search strategy!");
	}

	fclose(high_order_result);

	free(pMarginalDistrSNP);
	free(pMarginalDistrSNP_Y);
	free(single_chisqs);

	// 生成标识文件夹
	makeDir(stringFinished);

	vector<vector<int> >().swap(selectedCombinations);

	for (i = 0; i < nsnp; i++) {
		free(genocase_c0[i]);
		free(genocase_c1[i]);
		free(genocase_c2[i]);
		free(genocase_c3[i]);
		free(genoctrl_c0[i]);
		free(genoctrl_c1[i]);
		free(genoctrl_c2[i]);
		free(genoctrl_c3[i]);
	}

	free(genocase_c0);
	free(genocase_c1);
	free(genocase_c2);
	free(genocase_c3);
	free(genoctrl_c0);
	free(genoctrl_c1);
	free(genoctrl_c2);
	free(genoctrl_c3);

}

