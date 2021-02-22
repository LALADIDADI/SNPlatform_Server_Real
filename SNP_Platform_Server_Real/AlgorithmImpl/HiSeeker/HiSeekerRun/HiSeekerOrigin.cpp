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

using namespace std;
typedef long long   int64;
typedef unsigned long long uint64;
#define FMT_INT64   "%lld"
#define FMT_UINT64   "%llu"
#define FMT_HEX64   "%llx"

#define MarginalDistrSNP_Y_DimensionX 2
#define MarginalDistrSNP_Y_DimensionY 4
#define LengthLongType 64


//global variables
int nsample, nsnp, ncase, nctrl;
uint64** genocase_c0 = NULL, ** genocase_c1 = NULL, ** genocase_c2 = NULL, ** genocase_c3 = NULL;
uint64** genoctrl_c0 = NULL, ** genoctrl_c1 = NULL, ** genoctrl_c2 = NULL, ** genoctrl_c3 = NULL;

FILE* single_sig_result, * pairwise_sig_result, * high_order_result, * pair_chi_result;

int nlongintcase, nlongintctrl;
int* pMarginalDistrSNP, * pMarginalDistrSNP_Y;

//char input_file[256];
double sig_threshold;
int scalefactor;
int topK;
int typeOfSearch;
int numberOfthread;

int countOfcandidate;

double bonfe_pari_thres;
double single_sig_chisq, pairwise_sig_chisq;
double* single_chisqs;
double threshold; // =scalefactor*bonfe_pari_thres
double search_stage_chisq;

//ACO 
int IterCount, nAntCount, kLociSet, nLociSet, kEpiModel, TopKModel;
double* pheromone, * cdf;
double rou, alpha, level;
int** nSelectedLociSet;
int** loci_TopModel;
double* eva_TopModel, * phe_TopLoci;

vector<vector< double> > interactions;
vector<vector< int > > selectedCombinations;
double** interactionInfos;

// static variable for the precomput bin count of 64 bit string
static unsigned char wordbits[65536];// { bitcounts of ints between 0 and 65535 };

// compute number of 1s in 64 bit string
static int popcount(uint64 i)
{
	return(wordbits[i & 0xFFFF] + wordbits[(i >> 16) & 0xFFFF] + wordbits[(i >> 32) & 0xFFFF] + wordbits[i >> 48]);
}

int bitCount(uint64 i);
void read_parameter();
void GetDataSize(const char* filename, int* nsample, int* nsnp);
void GetCaseControlSize(const char* filename, int* ncase, int* nctrl);
void readData(const char* filename);
double chi_square(int* selectedSNPSet, int m);

int calcdev(double* deviance, int casef[4][4], int ctrlf[4][4]);
int calcsingledev_exclmiss(double* deviance, int casef[4][4], int ctrlf[4][4], int snpindex);
double det(gsl_matrix* x);
int calcLR(gsl_matrix* x, gsl_vector* b, gsl_vector* y, int maxIter);
double calcG(gsl_matrix* x, gsl_vector* b, gsl_vector* y, int casen, int ctrln);
int logreg(double* g, int casef[4][4], int ctrlf[4][4]);
void* thread_CalInterInfo(void* threadid);

void setpheromone(double level);
void postprocessing();
void get_toploci();

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

bool cmp(vector<double> a, vector<double> b) {
	return a[3] > b[3];
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
			// count the '-1' element
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
		/*do {
		c = getc(fp);
		if(c=='\n') goto out2;//end of line
		} while(isspace(c));//space
		*/
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

	printf("cputime for getting data size: %d seconds.\n", (int)ed - st);

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
		exit(-1);
	}


	std::cout << "start get count of case and control \n" << std::endl;

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
	printf("total sample: %d (ncase = %d; nctrl = %d).\n", nsample, (int)icase, (int)ictrl);


}
/*
void read_parameter()
{
FILE *fh_parameter;
char temp1[256], temp2[256];
printf("\nread parameters:\n");
fh_parameter = fopen("parameters.txt", "r");

fscanf(fh_parameter, "%s\t%s\t%s\n", temp1, &input_file, temp2);
fscanf(fh_parameter, "%s\t%lf\t%s\n", temp1, &sig_threshold, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &scalefactor, temp2);

fscanf(fh_parameter, "%s\t%lf\t%s\n", temp1, &rou, temp2);
fscanf(fh_parameter, "%s\t%lf\t%s\n", temp1, &level, temp2);
fscanf(fh_parameter, "%s\t%lf\t%s\n", temp1, &alpha, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &nAntCount, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &IterCount, temp2);


fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &kLociSet, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &kEpiModel, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &TopKModel, temp2);

fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &topK, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &typeOfSearch, temp2);
fscanf(fh_parameter, "%s\t%d\t%s\n", temp1, &numberOfthread, temp2);

fclose(fh_parameter);

// printf("input_file: %s\n", input_file);
// printf("sig_hreshold: %f\n", sig_threshold);
// printf("scale favtor: %d\n", scalefactor);

// printf("evaporation_rate: %f\n", rou);
// printf("initial_pheromone_level: %f\n", level);
// printf("weight_for_pheromone: %f\n", alpha);
// printf("ant_count: %d\n", nAntCount);
// printf("iteration_count: %d\n", IterCount);

// printf("k_loci_set: %d\n", kLociSet);
// printf("k_locus_epis: %d\n", kEpiModel);
// printf("TopKModel: %d\n", TopKModel);

// printf("topK interaction: %d\n", topK);
// printf("type of search: %d\n", typeOfSearch);
// printf("number of threads: %d\n", numberOfthread);

}
*/
void readData(const char* filename)
{
	FILE* fp;
	int i, j, tmp, flag;
	int icase, ictrl;
	time_t st, ed;
	uint64 mask1 = 0x0000000000000001;

	time(&st);
	i = 0; //row index
	j = 0; // column index


	fp = fopen(filename, "r");
	if (fp == NULL)
	{
		fprintf(stderr, "can't open input file %s\n", filename);
		exit(1);
	}
	icase = -1;
	ictrl = -1;

	printf("Loading data in file : %s\n", filename);


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

	//printf("Number of numbers read: %d\n\n", n*p);
	time(&ed);
	printf("cputime for loading data: %d seconds\n", (int)ed - st);
}

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
		}
	}
}

double calsingledev(int locus)
{
	int j, k;
	int rowsum[2], colsum[3], samplen;
	double gtcountexp[2][3];

	int expcountinfo = 0;
	double dev;

	/* marginal total */
	rowsum[0] = 0;
	rowsum[1] = 0;
	for (j = 0; j < 3; j++)
	{
		rowsum[0] += pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + locus];
		rowsum[1] += pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + locus];
		colsum[j] = pMarginalDistrSNP[j * nsnp + locus];
	}
	samplen = rowsum[0] + rowsum[1];


	/* expected cell count */
	expcountinfo = 0;
	for (j = 0; j < 2; j++)
		for (k = 0; k < 3; k++)
		{
			gtcountexp[j][k] = (double)rowsum[j] * colsum[k] / samplen;
			/* check if expected cell count < 5 or <1 */
			if (gtcountexp[j][k] < 5 && gtcountexp[j][k] >= 1)
			{
				expcountinfo++;
			}
			else if (gtcountexp[j][k] < 1 && gtcountexp[j][k] != 0)
			{
				expcountinfo += 100;
			}
		}
	/* deviance */
	dev = 0;
	for (j = 0; j < 3; j++)
		if (pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + locus] != 0)
			dev += pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + locus] * log(pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + locus] / gtcountexp[0][j]);
	for (j = 0; j < 3; j++)
		if (pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + locus] != 0)
			dev += pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + locus] * log(pMarginalDistrSNP_Y[(j * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + locus] / gtcountexp[1][j]);
	dev *= 2;
	return dev;

}
int calcdev(double* deviance, int casef[4][4], int ctrlf[4][4])
{
	int i, j, k;
	int casen = 0, ctrln = 0, samplen, SNP12gtmarg[9];
	double gtcountexp[2][9];
	int expcountinfo = 0;
	double dev = 0;

	/* calculate full deviance */
	/* get expected cell count: gtcountexp[2][9] */
	for (i = 0; i < 3; i++)
	{
		for (j = 0; j < 3; j++)
		{
			casen += casef[i][j];
			ctrln += ctrlf[i][j];
			SNP12gtmarg[3 * i + j] = casef[i][j] + ctrlf[i][j];
		}
	}
	samplen = casen + ctrln;
	for (i = 0; i < 9; i++)
	{
		gtcountexp[0][i] = (double)casen * SNP12gtmarg[i] / samplen;
		gtcountexp[1][i] = (double)ctrln * SNP12gtmarg[i] / samplen;
		if (gtcountexp[0][i] < 5 && gtcountexp[0][i] >= 1)
		{
			expcountinfo++;
		}
		else if (gtcountexp[0][i] < 1 && gtcountexp[0][i] != 0)
		{
			expcountinfo += 100;
		}
		if (gtcountexp[1][i] < 5 && gtcountexp[1][i] >= 1)
		{
			expcountinfo++;
		}
		else if (gtcountexp[1][i] < 1 && gtcountexp[1][i] != 0)
		{
			expcountinfo += 100;
		}
	}
	/* calculate deviance */
	for (i = 0; i < 9; i++)
	{
		j = i / 3;
		k = i % 3;
		if (casef[j][k] != 0)
			dev += casef[j][k] * log(casef[j][k] / gtcountexp[0][i]);
		if (ctrlf[j][k] != 0)
			dev += ctrlf[j][k] * log(ctrlf[j][k] / gtcountexp[1][i]);
	}
	dev = 2 * dev;
	*deviance = dev;
	return expcountinfo;
}
int calcsingledev_exclmiss(double* deviance, int casef[4][4], int ctrlf[4][4], int snpindex)
{
	int i, j;
	int gtcount[2][3] = { 0, 0, 0, 0, 0, 0 };
	int rowsum[2] = { 0, 0 }, colsum[3] = { 0, 0, 0 };
	int samplen;
	double gtcountexp[2][3];
	int expcountinfo = 0;
	double dev = 0;

	/* calculate single SNP deviance within SNPpair, excluding samples with any missing genotype */
	/* get genotype counts and marginal total */
	for (i = 0; i < 3; i++)
	{
		for (j = 0; j < 3; j++)
		{
			if (snpindex == 1)
			{
				gtcount[0][i] += casef[i][j];
				gtcount[1][i] += ctrlf[i][j];
			}
			else if (snpindex == 2)
			{
				gtcount[0][i] += casef[j][i];
				gtcount[1][i] += ctrlf[j][i];
			}
			colsum[i] = gtcount[0][i] + gtcount[1][i];
		}
		rowsum[0] += gtcount[0][i];
		rowsum[1] += gtcount[1][i];
	}
	samplen = rowsum[0] + rowsum[1];
	/* get expected cell count */
	for (i = 0; i < 2; i++)
	{
		for (j = 0; j < 3; j++)
		{
			gtcountexp[i][j] = (double)rowsum[i] * colsum[j] / samplen;
			if (gtcountexp[i][j] < 5 && gtcountexp[i][j] >= 1)
			{
				expcountinfo++;
			}
			else if (gtcountexp[i][j] < 1 && gtcountexp[i][j] != 0)
			{
				expcountinfo += 100;
			}
		}
	}
	/* calculate deviance */
	for (i = 0; i < 2; i++)
	{
		for (j = 0; j < 3; j++)
		{
			if (gtcount[i][j] != 0)
				dev += gtcount[i][j] * log(gtcount[i][j] / gtcountexp[i][j]);
		}
	}
	dev *= 2;
	*deviance = dev;
	return expcountinfo;
}

/* functions for logreg: det, calcLR, calcG, logreg*/
double det(gsl_matrix* x)
{
	int i, j, k, m, n;
	double t, result;
	gsl_matrix* tempX;

	m = (int)(*x).size1;
	n = (int)(*x).size2;
	result = 1;
	tempX = gsl_matrix_alloc(m, n);

	gsl_matrix_memcpy(tempX, x);
	for (i = 0; i < m - 1; i++)
	{
		for (j = i + 1; j < m; j++)
		{
			t = gsl_matrix_get(tempX, j, i) / gsl_matrix_get(tempX, i, i);
			gsl_matrix_set(tempX, j, i, 0);
			for (k = i + 1; k < n; k++)
				gsl_matrix_set(tempX, j, k, gsl_matrix_get(tempX, j, k) - t * gsl_matrix_get(tempX, i, k));
		}
	}
	for (i = 0; i < m; i++)
		for (j = 0; j < n; j++)
			if (i == j) result *= gsl_matrix_get(tempX, i, j);

	gsl_matrix_free(tempX);
	return result;
}
int calcLR(gsl_matrix* x, gsl_vector* b, gsl_vector* y, int maxIter)
{
	int	i, j, k, iter, nr, nc, iTemp;
	double eTemp, s;
	gsl_matrix* xTran, * multOut1, * multOut2, * mInv, * lu;
	gsl_vector* weights, * adjY, * expY, * deriv, * wadjy, * oldExpY;
	gsl_permutation* perm;

	nr = (int)(*x).size1;
	nc = (int)(*x).size2;
	/* weights = gsl_matrix_alloc(nr, nr); */
	xTran = gsl_matrix_alloc(nc, nr);
	multOut1 = gsl_matrix_alloc(nc, nr);
	multOut2 = gsl_matrix_alloc(nc, nc);
	mInv = gsl_matrix_alloc(nc, nc);
	lu = gsl_matrix_alloc(nc, nc);
	weights = gsl_vector_alloc(nr); /* new */
	adjY = gsl_vector_alloc(nr);
	expY = gsl_vector_alloc(nr);
	deriv = gsl_vector_alloc(nr);
	wadjy = gsl_vector_alloc(nr);
	oldExpY = gsl_vector_alloc(nr);
	perm = gsl_permutation_alloc(nc);


	gsl_vector_set_all(oldExpY, -1);
	for (iter = 0; iter < maxIter; iter++)
	{
		/* calculate adjy */
		for (i = 0; i < nr; i++)
		{
			eTemp = 0;
			for (j = 0; j < nc; j++)
			{
				eTemp += gsl_matrix_get(x, i, j) * gsl_vector_get(b, j);
			}
			gsl_vector_set(adjY, i, eTemp);
		}
		/* calculate expy */
		for (i = 0; i < nr; i++)
			gsl_vector_set(expY, i, 1 / (1 + exp((double)(-gsl_vector_get(adjY, i)))));
		/* calculate deriv */
		for (i = 0; i < nr; i++)
			gsl_vector_set(deriv, i, gsl_vector_get(expY, i) * (1 - gsl_vector_get(expY, i)));
		/* calculate wadjy */
		for (i = 0; i < nr; i++)
			gsl_vector_set(wadjy, i, gsl_vector_get(deriv, i) * gsl_vector_get(adjY, i) + gsl_vector_get(y, i) - gsl_vector_get(expY, i));
		/* calculate the x */
		/* diagonal weights */
		for (i = 0; i < nr; i++)
			gsl_vector_set(weights, i, gsl_vector_get(deriv, i));
		/* gsl_matrix_set(weights, i, i, gsl_vector_get(deriv, i)); */
		/* a transpose */
		gsl_matrix_transpose_memcpy(xTran, x);
		/* a' * weights * a+ridgemat */
		for (i = 0; i < nc; i++)
			for (j = 0; j < nr; j++)
				gsl_matrix_set(multOut1, i, j, gsl_matrix_get(xTran, i, j) * gsl_vector_get(weights, j));
		for (i = 0; i < nc; i++)
			for (j = 0; j < nc; j++)
			{
				eTemp = 0;
				for (k = 0; k < nr; k++)
					eTemp += gsl_matrix_get(multOut1, i, k) * gsl_matrix_get(x, k, j);
				gsl_matrix_set(multOut2, i, j, eTemp);
			}
		for (i = 0; i < nc; i++)
			gsl_matrix_set(multOut2, i, i, gsl_matrix_get(multOut2, i, i) + 1e-5);
		/* inverse of (a' * weights * a+ridgemat) */
		if (det(multOut2) == 0)
		{
			gsl_vector_free(weights);
			gsl_matrix_free(xTran);
			gsl_matrix_free(multOut1);
			gsl_matrix_free(multOut2);
			gsl_matrix_free(mInv);
			gsl_matrix_free(lu);
			gsl_vector_free(adjY);
			gsl_vector_free(expY);
			gsl_vector_free(deriv);
			gsl_vector_free(wadjy);
			gsl_vector_free(oldExpY);
			gsl_permutation_free(perm);
			return 0;
		}
		gsl_matrix_memcpy(lu, multOut2);
		gsl_linalg_LU_decomp(lu, perm, &iTemp);
		gsl_linalg_LU_invert(lu, perm, mInv);
		/* inv(a'*weights*a+ridgemat)*a'*wadjy */
		gsl_matrix_set_zero(multOut1);
		for (i = 0; i < nc; i++)
			for (j = 0; j < nr; j++)
			{
				eTemp = 0;
				for (k = 0; k < nc; k++)
					eTemp += gsl_matrix_get(mInv, i, k) * gsl_matrix_get(xTran, k, j);
				gsl_matrix_set(multOut1, i, j, eTemp);
			}
		for (i = 0; i < nc; i++)
		{
			eTemp = 0;
			for (j = 0; j < nr; j++)
			{
				eTemp += gsl_matrix_get(multOut1, i, j) * gsl_vector_get(wadjy, j);
			}
			gsl_vector_set(b, i, eTemp);
		}
		/* Output
		printf("%d: ", iter+1);
		for (i=0;i<nc;i++)
		printf("%.5f  ", gsl_vector_get(b, i));
		printf("\n"); */
		for (i = 0, s = 0; i < nr; i++)
			s += fabs(gsl_vector_get(expY, i) - gsl_vector_get(oldExpY, i));
		if (s < nr * (1e-10))
		{
			/* printf("Converged.\n"); */
			gsl_vector_free(weights);
			gsl_matrix_free(xTran);
			gsl_matrix_free(multOut1);
			gsl_matrix_free(multOut2);
			gsl_matrix_free(mInv);
			gsl_matrix_free(lu);
			gsl_vector_free(adjY);
			gsl_vector_free(expY);
			gsl_vector_free(deriv);
			gsl_vector_free(wadjy);
			gsl_vector_free(oldExpY);
			gsl_permutation_free(perm);
			return 1;
		}
		gsl_vector_memcpy(oldExpY, expY);
	}
	gsl_vector_free(weights);
	gsl_matrix_free(xTran);
	gsl_matrix_free(multOut1);
	gsl_matrix_free(multOut2);
	gsl_matrix_free(mInv);
	gsl_matrix_free(lu);
	gsl_vector_free(adjY);
	gsl_vector_free(expY);
	gsl_vector_free(deriv);
	gsl_vector_free(wadjy);
	gsl_vector_free(oldExpY);
	gsl_permutation_free(perm);
	return 0;
}
double calcG(gsl_matrix* x, gsl_vector* b, gsl_vector* y, int casen, int ctrln)
{
	int	i, j, nr, nc;
	int	caseNum[9], ctrlNum[9];
	int	tempX[4];
	double	tempPai[9], paiFull[9], paiNull, like;

	nr = (int)(*x).size1;
	nc = (int)(*x).size2;
	like = 0;

	for (i = 0; i < 9; i++)
		caseNum[i] = ctrlNum[i] = 0;
	/* Statistic for case & control number */
	for (i = 0; i < nr; i++)
	{
		switch ((int)gsl_vector_get(y, i))
		{
		case 0:
			ctrlNum[(int)(gsl_matrix_get(x, i, 1) + 3 * gsl_matrix_get(x, i, 3))] ++;
			break;
		case 1:
			caseNum[(int)(gsl_matrix_get(x, i, 1) + 3 * gsl_matrix_get(x, i, 3))] ++;
			break;
		default:
			break;
		}
	}
	/* pai */
	for (i = 0; i < 9; i++)
	{
		tempX[0] = i % 3;
		tempX[1] = (tempX[0] == 1) ? 1 : 0;
		tempX[2] = i / 3;
		tempX[3] = (tempX[2] == 1) ? 1 : 0;
		tempPai[i] = gsl_vector_get(b, 0);
		for (j = 0; j < 4; j++)
			tempPai[i] += gsl_vector_get(b, j + 1) * tempX[j];
		paiFull[i] = 1 / (1 + exp(-tempPai[i]));
	}
	paiNull = (double)casen / (double)(casen + ctrln);
	/* likelihood */
	for (i = 0; i < 9; i++)
		like += caseNum[i] * log(paiFull[i] / paiNull) + ctrlNum[i] * log((1 - paiFull[i]) / (1 - paiNull));
	return 2 * like;
}
int logreg(double* g, int casef[4][4], int ctrlf[4][4])
{
	/* Process Data -> Matrix */
	int m, n, i, nr, nc, casenn, ctrlnn, count;
	int ret;
	int* x11, * x22;
	gsl_matrix* x;
	gsl_vector* y;
	gsl_vector* b;

	casenn = casef[0][0] + casef[0][1] + casef[0][2] + casef[1][0] + casef[1][1] + casef[1][2] + casef[2][0] + casef[2][1] + casef[2][2];
	ctrlnn = ctrlf[0][0] + ctrlf[0][1] + ctrlf[0][2] + ctrlf[1][0] + ctrlf[1][1] + ctrlf[1][2] + ctrlf[2][0] + ctrlf[2][1] + ctrlf[2][2];
	nr = casenn + ctrlnn;

	x11 = (int*)malloc(sizeof(int) * nr);
	x22 = (int*)malloc(sizeof(int) * nr);

	count = 0;
	for (m = 0; m < 3; m++)
	{
		for (n = 0; n < 3; n++)
		{
			for (i = 0; i < casef[m][n]; i++)
			{
				x11[count] = m;
				x22[count] = n;
				count++;
			}
		}
	}
	for (m = 0; m < 3; m++)
	{
		for (n = 0; n < 3; n++)
		{
			for (i = 0; i < ctrlf[m][n]; i++)
			{
				x11[count] = m;
				x22[count] = n;
				count++;
			}
		}
	}

	/* make Matrix */
	nc = 5;
	x = gsl_matrix_alloc(nr, nc);
	b = gsl_vector_alloc(nc);
	y = gsl_vector_alloc(nr);


	for (i = 0; i < nr; i++)
	{
		gsl_matrix_set(x, i, 0, 1);
		gsl_matrix_set(x, i, 1, x11[i]);
		gsl_matrix_set(x, i, 2, (x11[i] == 1) ? 1 : 0);
		gsl_matrix_set(x, i, 3, x22[i]);
		gsl_matrix_set(x, i, 4, (x22[i] == 1) ? 1 : 0);

	}
	for (i = 0; i < casenn; i++)
		gsl_vector_set(y, i, 1);
	for (i = casenn; i < nr; i++)
		gsl_vector_set(y, i, 0);
	for (i = 0; i < nc; i++)
		gsl_vector_set(b, i, 0);

	ret = calcLR(x, b, y, 25);
	if (ret == 1)
		*g = calcG(x, b, y, casenn, ctrlnn);
	free(x11);
	free(x22);
	gsl_matrix_free(x);
	gsl_vector_free(b);
	gsl_vector_free(y);
	return ret;
}

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
	j = -1;
	double mRate;

	while (j == -1)
	{
		j = 0;
		srand((unsigned)time(NULL) + rand());
		mRate = rnd(0, 1);
		k = cdf2loci(mRate, 0, nLociSet);
		for (i = 0; i < m_iLociCount; i++)
		{
			if (k == tabu[i])
			{
				j = -1;
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
		locidata[i] = -1;

	for (i = 0; i < nAntCount; i++)
	{
		loci.clear();
		flag = 0;
		loci.push_back(nSelectedLociSet[ants[i].tabu[1]][0]);
		if (nSelectedLociSet[ants[i].tabu[1]][1] != -1) {
			flag = 1;
			loci.push_back(nSelectedLociSet[ants[i].tabu[1]][1]);
		}
		for (j = 0; j < 2; j++) {
			switch (flag) {
			case 1:
				if (nSelectedLociSet[ants[i].tabu[2]][j] != loci[0] && nSelectedLociSet[ants[i].tabu[2]][j] != loci[1] && nSelectedLociSet[ants[i].tabu[2]][j] != -1) {
					loci.push_back(nSelectedLociSet[ants[i].tabu[2]][j]);
				}
				break;
			case 0:
				if (nSelectedLociSet[ants[i].tabu[2]][j] != loci[0] && nSelectedLociSet[ants[i].tabu[2]][j] != -1) {
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
			locidata[3] = -1;
			eva = eva * 2;
		}
		else if (loci.size() == 2) {
			locidata[3] = locidata[2] = -1;
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

//search three-order interaction through top ranking haplotypes
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
		if (loci_TopModel[i][3] == -1) {
			if (loci_TopModel[i][2] == -1) {
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

void calcuLRstatic()
{
	uint64 x, * x_c00, * x_c01, * x_c02, * x_c03, * x_c10, * x_c11, * x_c12, * x_c13;
	uint64* y_c00, * y_c01, * y_c02, * y_c03, * y_c10, * y_c11, * y_c12, * y_c13;

	int j1, j2, m, n, k;
	int casefreq[4][4], ctrlfreq[4][4];

	double chiSq_value, pvalue;
	int* snps = new int[2];
	int expcountinfo;
	int situation;
	int lrcheck;
	double deviance, deviance_full, deviance_single;

	vector < int > record;
	record.resize(2);

	for (j1 = 0; j1 < nsnp - 1; j1++) {
		for (j2 = j1 + 1; j2 < nsnp; j2++) {
			//for (j1 = 20; j1<21; j1++){
			//for (j2 = 30; j2<31; j2++){
			snps[0] = j1;
			snps[1] = j2;

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

			for (k = 0; k < nlongintcase; k++) {

				x = x_c00[k] & y_c00[k]; casefreq[0][0] += popcount(x);
				x = x_c00[k] & y_c01[k]; casefreq[0][1] += popcount(x);
				x = x_c00[k] & y_c02[k]; casefreq[0][2] += popcount(x);
				x = x_c01[k] & y_c00[k]; casefreq[1][0] += popcount(x);
				x = x_c01[k] & y_c01[k]; casefreq[1][1] += popcount(x);
				x = x_c01[k] & y_c02[k]; casefreq[1][2] += popcount(x);
				x = x_c02[k] & y_c00[k]; casefreq[2][0] += popcount(x);
				x = x_c02[k] & y_c01[k]; casefreq[2][1] += popcount(x);
				x = x_c02[k] & y_c02[k]; casefreq[2][2] += popcount(x);
			}

			for (k = 0; k < nlongintctrl; k++) {

				x = x_c10[k] & y_c10[k]; ctrlfreq[0][0] += popcount(x);
				x = x_c10[k] & y_c11[k]; ctrlfreq[0][1] += popcount(x);
				x = x_c10[k] & y_c12[k]; ctrlfreq[0][2] += popcount(x);
				x = x_c11[k] & y_c10[k]; ctrlfreq[1][0] += popcount(x);
				x = x_c11[k] & y_c11[k]; ctrlfreq[1][1] += popcount(x);
				x = x_c11[k] & y_c12[k]; ctrlfreq[1][2] += popcount(x);
				x = x_c12[k] & y_c10[k]; ctrlfreq[2][0] += popcount(x);
				x = x_c12[k] & y_c11[k]; ctrlfreq[2][1] += popcount(x);
				x = x_c12[k] & y_c12[k]; ctrlfreq[2][2] += popcount(x);

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

			chiSq_value = chi_square(snps, 2);
			if (chiSq_value > threshold) {
				expcountinfo = calcdev(&deviance_full, casefreq, ctrlfreq);
				deviance = deviance_full;
				if (single_chisqs[j1] > single_sig_chisq && single_chisqs[j2] > single_sig_chisq)
				{
					situation = 2;
					lrcheck = logreg(&deviance_single, casefreq, ctrlfreq);
					lrcheck ? (deviance -= deviance_single) : (deviance = 0);
				}
				else if (single_chisqs[j1] > single_sig_chisq)
				{
					situation = 1;
					calcsingledev_exclmiss(&deviance_single, casefreq, ctrlfreq, 1);
					deviance -= deviance_single;
				}
				else if (single_chisqs[j2] > single_sig_chisq)
				{
					situation = 1;
					calcsingledev_exclmiss(&deviance_single, casefreq, ctrlfreq, 2);
					deviance -= deviance_single;
				}
				deviance = (deviance > 0) ? deviance : 0;
				//if (deviance>threshold){
				record[0] = j1;
				record[1] = j2;
				countOfcandidate++;
				selectedCombinations.push_back(record);
				pvalue = gsl_cdf_chisq_Q(deviance, 8 - 2 * situation);
				//if (pvalue < bonfe_pari_thres)
				fprintf(pairwise_sig_result, "%d\t%d\t%f\t%g\t%d\n", j1, j2, deviance, pvalue, situation);
			}

		}
		//}
	}
}

//search topK three-order interactions via exhuastive search
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

//search high-order interactions via ACO-based search
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
			loci_TopModel[i][j] = -1;
		}
	}

	setpheromone(level);

	seeker episeeker;
	episeeker.StartSearch();
	postprocessing();

}


int main()
{
	int i;
	int j1, j2, j3, j4;
	int single_sig_count;

	// search approach: 0: Exhaustive search  1: ACO search
	typeOfSearch = 1;
	// parameter setting
	alpha = 1;
	//countOfcandidate = 0;
	sig_threshold = 0.05;
	scalefactor = 10;
	topK = 100;
	// ACO
	rou = 0.05;
	level = 100;
	nAntCount = 500;
	IterCount = 200;
	kLociSet = 2;
	kEpiModel = 3;
	TopKModel = 800;
	numberOfthread = 4;
	time_t st, ed;



	//single_sig_result = fopen("G:/hlzhang_data/Result/hiseeker_BC_single_significant_snps.txt", "w");
	//pairwise_sig_result = fopen("G:/hlzhang_data/Result/hiseeker_BC_pairwise_sig_snps.txt", "w");
	//high_order_result = fopen("G:/hlzhang_data/Result/hiseeker_BC_high_order_result.txt", "w");

	//fprintf(single_sig_result, "SNP\tx2\tp-value\n");
	//fprintf(pairwise_sig_result, "SNP1\tSNP2\tdeviance\tp-value\tsituation\n");
	//fprintf(high_order_result, "SNP1\tSNP2\tSNP3\tx2\tp-value\n");

	//time(&st);

	////read_parameter();

	//// precompute the wordbits (a global variable)
	//for (i = 0; i<65536; i++)
	//{
	//	wordbits[i] = bitCount(i);
	//	//printf("%d\n",wordbits[i]);
	//}

	//GetDataSize("F:/xcao/Real Datasets/BC/EGA013/ega013_298_genotype_data.txt", &nsample, &nsnp);
	//GetCaseControlSize("F:/xcao/Real Datasets/BC/EGA013/ega013_298_genotype_data.txt", &ncase, &nctrl);

	//// calculate marginal distribution

	//nlongintcase = (int)ceil(((double)ncase) / LengthLongType);
	//nlongintctrl = (int)ceil(((double)nctrl) / LengthLongType);
	//printf("nLongIntcase = %d; nLongIntctrl = %d.\n", nlongintcase, nlongintctrl);

	//pMarginalDistrSNP = (int *)malloc(MarginalDistrSNP_Y_DimensionY*nsnp*sizeof(int));
	//pMarginalDistrSNP_Y = (int *)malloc(MarginalDistrSNP_Y_DimensionY*MarginalDistrSNP_Y_DimensionX*nsnp*sizeof(int));

	////calloc memory for bit representation
	//genocase_c0 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//genocase_c1 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//genocase_c2 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//genocase_c3 = (uint64 **)calloc(nsnp, sizeof(uint64 *));


	//genoctrl_c0 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//genoctrl_c1 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//genoctrl_c2 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//genoctrl_c3 = (uint64 **)calloc(nsnp, sizeof(uint64 *));
	//for (i = 0; i <nsnp; i++){
	//	genocase_c0[i] = (uint64 *)calloc(nlongintcase, sizeof(uint64));
	//	genocase_c1[i] = (uint64 *)calloc(nlongintcase, sizeof(uint64));
	//	genocase_c2[i] = (uint64 *)calloc(nlongintcase, sizeof(uint64));
	//	genocase_c3[i] = (uint64 *)calloc(nlongintcase, sizeof(uint64));

	//	genoctrl_c0[i] = (uint64 *)calloc(nlongintctrl, sizeof(uint64));
	//	genoctrl_c1[i] = (uint64 *)calloc(nlongintctrl, sizeof(uint64));
	//	genoctrl_c2[i] = (uint64 *)calloc(nlongintctrl, sizeof(uint64));
	//	genoctrl_c3[i] = (uint64 *)calloc(nlongintctrl, sizeof(uint64));
	//}

	//readData("F:/xcao/Real Datasets/BC/EGA013/ega013_298_genotype_data.txt");
	//CalculateMarginalDistr(pMarginalDistrSNP, pMarginalDistrSNP_Y);

	//bonfe_pari_thres = sig_threshold / comb_num(nsnp, 2);

	//single_sig_chisq = gsl_cdf_chisq_Qinv(sig_threshold / nsnp, 2);
	//pairwise_sig_chisq = gsl_cdf_chisq_Qinv(bonfe_pari_thres, 8);
	//threshold = gsl_cdf_chisq_Qinv(bonfe_pari_thres*scalefactor, 8);
	//search_stage_chisq = gsl_cdf_chisq_Qinv(sig_threshold / comb_num(nsnp, kEpiModel), pow(3, kEpiModel) - 1);

	//// search single significant SNP
	//single_chisqs = (double *)calloc(nsnp, sizeof(double));
	//int snp[1];
	//single_sig_count = 0;
	//for (i = 0; i<nsnp; i++)
	//{
	//	snp[0] = i;
	//	single_chisqs[i] = chi_square(snp, 1);
	//	if (single_chisqs[i] >= single_sig_chisq){
	//		fprintf(single_sig_result, "%d\t%f\t%g\n", i, single_chisqs[i], gsl_cdf_chisq_Q(single_chisqs[i], 2));
	//		single_sig_count++;
	//	}
	//}
	//printf("The count of significant single SNPs with P-Values < %g : %d\n", sig_threshold / nsnp, single_sig_count);

	////search pairwise combinations having intermediate or significant associations with the phenotype
	//countOfcandidate = 0;
	//calcuLRstatic();
	//printf("The count of candidate pairwise combinations: %d\n", countOfcandidate);
	////search high-order interactions via ACO-based search or exhuastive search

	//if (typeOfSearch == 0){
	//	interactionInfos = (double **)calloc(topK, sizeof(double));
	//	for (i = 0; i<topK; i++)
	//		interactionInfos[i] = (double *)calloc(kEpiModel + 2, sizeof(double));
	//	runExhuastiveSearch();
	//	for (i = topK - 1; i >= 0; i--)
	//		fprintf(high_order_result, "%d\t%d\t%d\t%f\t%g\n", (int)interactionInfos[i][0], (int)interactionInfos[i][1], (int)interactionInfos[i][2], interactionInfos[i][3], interactionInfos[i][4]);
	//	for (i = 0; i<topK; i++)
	//		free(interactionInfos[i]);
	//	free(interactionInfos);

	//}
	//else if (typeOfSearch == 1){
	//	runACObasedSearch();
	//	sort(interactions.begin(), interactions.end(), cmp);
	//	if (interactions.size()<topK){
	//		for (i = 0; i<interactions.size(); i++)
	//			fprintf(high_order_result, "%d\t%d\t%d\t%f\t%g\n", (int)interactions[i][0], (int)interactions[i][1], (int)interactions[i][2], interactions[i][3], interactions[i][4]);
	//		vector<vector<double> >().swap(interactions);
	//	}
	//	else{
	//		for (i = 0; i<topK; i++)
	//			fprintf(high_order_result, "%d\t%d\t%d\t%f\t%g\n", (int)interactions[i][0], (int)interactions[i][1], (int)interactions[i][2], interactions[i][3], interactions[i][4]);
	//		vector<vector<double> >().swap(interactions);
	//	}
	//}
	//else {
	//	printf("chose a wrong search strategy!");
	//}

	//time(&ed);

	//printf("\nThe total time: %d seconds\n", (int)ed - st);

	//fclose(single_sig_result);
	//fclose(pairwise_sig_result);
	//fclose(high_order_result);


	//free(pMarginalDistrSNP);
	//free(pMarginalDistrSNP_Y);
	//free(single_chisqs);

	//vector<vector<int> >().swap(selectedCombinations);

	//for (i = 0; i <nsnp; i++){
	//	free(genocase_c0[i]);
	//	free(genocase_c1[i]);
	//	free(genocase_c2[i]);
	//	free(genocase_c3[i]);
	//	free(genoctrl_c0[i]);
	//	free(genoctrl_c1[i]);
	//	free(genoctrl_c2[i]);
	//	free(genoctrl_c3[i]);
	//}

	//free(genocase_c0);
	//free(genocase_c1);
	//free(genocase_c2);
	//free(genocase_c3);
	//free(genoctrl_c0);
	//free(genoctrl_c1);
	//free(genoctrl_c2);
	//free(genoctrl_c3);


	char result1name[100000] = "";
	char result2name[100000] = "";
	char result3name[100000] = "";

	char* filename = (char*)malloc(58388 * sizeof(char));
	//char *data;
	int MAFs[3] = { 1, 2, 5 };
	//int MAFs[1] = { 5 };
	//int H[4] = { 0.01, 0.05, 0.20, 0.40 };
	int Samples[2] = { 1000, 2000 };
	int LDs[2] = { 7, 10 };
	int maf, samp, ld, h;

	//for (int sa = 1; sa < 2; sa++){
	//	samp = Samples[sa];
	for (int ma = 0; ma < 1; ma++) {
		maf = MAFs[ma];
		//for (int l = 0; l < 1; l++){
		//	ld = LDs[l];
			//for (int ha = 0; ha < 4;ha++){
			//h = H[ha];
		for (int fileno = 0; fileno < 1; fileno++) {

			sprintf(result1name, "G:/SNPalgorithm/HiSeeker/ResultData/test_single-sig0903.txt");
			sprintf(result2name, "G:/SNPalgorithm/HiSeeker/ResultData/test_pairwise-sig0903.txt");
			sprintf(result3name, "G:/SNPalgorithm/HiSeeker/ResultData/test_BC_high-order0903.txt");


			single_sig_result = fopen(result1name, "w");
			pairwise_sig_result = fopen(result2name, "w");
			high_order_result = fopen(result3name, "w");

			fprintf(single_sig_result, "SNP\tx2\tp-value\n");
			fprintf(pairwise_sig_result, "SNP1\tSNP2\tdeviance\tp-value\tsituation\n");
			fprintf(high_order_result, "SNP1\tSNP2\tSNP3\tx2\tp-value\n");

			sprintf(filename, "G:/SNPalgorithm/HiSeeker/inputData/test_data.txt");
			// precompute the wordbits (a global variable)
			for (i = 0; i < 65536; i++)
			{
				wordbits[i] = bitCount(i);
				//printf("%d\n",wordbits[i]);
			}

			GetDataSize(filename, &nsample, &nsnp);
			GetCaseControlSize(filename, &ncase, &nctrl);

			// calculate marginal distribution
			nlongintcase = (int)ceil(((double)ncase) / LengthLongType);
			nlongintctrl = (int)ceil(((double)nctrl) / LengthLongType);
			printf("nLongIntcase = %d; nLongIntctrl = %d.\n", nlongintcase, nlongintctrl);

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

			readData(filename);
			CalculateMarginalDistr(pMarginalDistrSNP, pMarginalDistrSNP_Y);

			bonfe_pari_thres = sig_threshold / comb_num(nsnp, 2);

			single_sig_chisq = gsl_cdf_chisq_Qinv(sig_threshold / nsnp, 2);
			pairwise_sig_chisq = gsl_cdf_chisq_Qinv(bonfe_pari_thres, 8);
			threshold = gsl_cdf_chisq_Qinv(bonfe_pari_thres * scalefactor, 8);
			search_stage_chisq = gsl_cdf_chisq_Qinv(sig_threshold / comb_num(nsnp, kEpiModel), pow(3, kEpiModel) - 1);

			// search single significant SNP
			single_chisqs = (double*)calloc(nsnp, sizeof(double));
			int snp[1];
			single_sig_count = 0;
			for (i = 0; i < nsnp; i++)
			{
				snp[0] = i;
				single_chisqs[i] = chi_square(snp, 1);
				if (single_chisqs[i] >= single_sig_chisq) {
					fprintf(single_sig_result, "%d\t%f\t%g\n", i, single_chisqs[i], gsl_cdf_chisq_Q(single_chisqs[i], 2));
					single_sig_count++;
				}
			}
			printf("The count of significant single SNPs with P-Values < %g : %d\n", sig_threshold / nsnp, single_sig_count);

			//search pairwise combinations having intermediate or significant associations with the phenotype
			countOfcandidate = 0;
			calcuLRstatic();
			printf("The count of candidate pairwise combinations: %d\n", countOfcandidate);
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

			fclose(single_sig_result);
			fclose(pairwise_sig_result);
			fclose(high_order_result);


			free(pMarginalDistrSNP);
			free(pMarginalDistrSNP_Y);
			free(single_chisqs);

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
	}
}
//}
//exit(0);
//}