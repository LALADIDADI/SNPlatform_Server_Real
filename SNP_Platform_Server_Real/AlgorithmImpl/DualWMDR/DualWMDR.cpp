#include <iostream>
#include <vector>
#include <set>
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
#include <iterator>
#include <malloc.h>
#include <corecrt_io.h>
#include <direct.h>

using namespace std;

FILE* pairwise_sig_result;

#define FMT_INT64   "%lld"
#define FMT_UINT64   "%llu"
#define FMT_HEX64   "%llx"

typedef long long   int64;
typedef unsigned long long uint64;

#define LengthLongType 64
#define MarginalDistrSNP_Y_DimensionX 2
#define MarginalDistrSNP_Y_DimensionY 4

/* -------------------------------------------- GLOBAL VARIABLES -----------------------------------------------------------*/
vector< int > candidate;
double** interactionInfos;

static unsigned char wordbits[65536];

static int popcount(uint64 i)
{
	return(wordbits[i & 0xFFFF] + wordbits[(i >> 16) & 0xFFFF] + wordbits[(i >> 32) & 0xFFFF] + wordbits[i >> 48]);
}

int nsample, nsnp;
int ncase, nctrl;

int nlongintcase, nlongintctrl;
int* pMarginalDistrSNP, * pMarginalDistrSNP_Y;

uint64** genocase_c0 = NULL, ** genocase_c1 = NULL, ** genocase_c2 = NULL, ** genocase_c3 = NULL;
uint64** genoctrl_c0 = NULL, ** genoctrl_c1 = NULL, ** genoctrl_c2 = NULL, ** genoctrl_c3 = NULL;
uint64** genocase_train_c0 = NULL, ** genocase_train_c1 = NULL, ** genocase_train_c2 = NULL, ** genocase_train_c3 = NULL;
uint64** genoctrl_train_c0 = NULL, ** genoctrl_train_c1 = NULL, ** genoctrl_train_c2 = NULL, ** genoctrl_train_c3 = NULL;
uint64** genocase_test_c0 = NULL, ** genocase_test_c1 = NULL, ** genocase_test_c2 = NULL, ** genocase_test_c3 = NULL;
uint64** genoctrl_test_c0 = NULL, ** genoctrl_test_c1 = NULL, ** genoctrl_test_c2 = NULL, ** genoctrl_test_c3 = NULL;
// store data
int** X, ** X_case, ** X_ctrl;
int* Y_label;

int topK;
double** pmi;
int nclust;
double dc;
double percent;
int** cluster_candidate;
int* cluster_count;
int K;
int nk;

// MDR 
double mdr_threshold;
int fold;
int** X_case1, ** X_case2, ** X_case3, ** X_case4, ** X_case5, ** X_ctrl1, ** X_ctrl2, ** X_ctrl3, ** X_ctrl4, ** X_ctrl5;
int** X_train, ** X_test;
double alpha, constant;
int topT;

time_t seed;

/* FUNCTION DECLARATIONS */
int bitCount(uint64 i);
void GetDataSize(const char* filename, int* nsample, int* nsnp);
void GetCaseControlSize(const char* filename, int* ncase, int* nctrl);
void readData(const char* filename);

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

	n = 0;

	while (1)
	{
		int c = fgetc(fp);
		switch (c)
		{
		case '\n':
			n++;
			break;
		case EOF://file 
			goto out;
		default:
			;
		}
	}
out:
	rewind(fp);

	p = 0;
	i = 0;
	flag = 1;
	while (1)
	{
		c = getc(fp);
		if (c == '\n') goto out2;
		if (isspace(c))
		{
			flag = 1;
		}

		if (!isspace(c) && (flag == 1))
		{
			p++;
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
		exit(-1);
	}

	std::cout << "\nstart get count of case and control:" << std::endl;

	i = 0;
	j = 0;
	icase = 0;
	ictrl = 0;
	while (!feof(fp)) {
		if (j == 0)
		{
			fscanf(fp, "%d", &tmp);

			if (tmp)
			{
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
			j++;
			if (j == (nsnp + 1))
			{
				j = 0;
				i++;
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

void readData(const char* filename)
{
	FILE* fp;
	int i, j, tmp, flag, k = 0;
	int icase, ictrl;
	time_t st, ed;
	uint64 mask1 = 0x0000000000000001;

	time(&st);
	i = 0;
	j = 0;


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
		if (j == 0)
		{
			fscanf(fp, "%d", &tmp);
			X[i][j] = tmp;
			Y_label[k] = tmp;

			if (tmp)
			{
				icase++;
				X_case[icase][j] = tmp;
				flag = 1;
			}
			else
			{
				ictrl++;
				X_ctrl[ictrl][j] = tmp;
				flag = 0;
			}
			j++;
			k++;
		}
		else
		{
			fscanf(fp, "%d", &tmp);
			X[i][j] = tmp;

			if (flag)
			{
				X_case[icase][j] = tmp;
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
				X_ctrl[ictrl][j] = tmp;
				switch (tmp)
				{
				case 0: genoctrl_c0[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				case 1: genoctrl_c1[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				case 2: genoctrl_c2[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				case 3: genoctrl_c3[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
				default: break;
				}
			}

			j++;
			if (j == (nsnp + 1))
			{
				j = 0;
				i++;
			}

		}

		if (i >= nsample)
		{
			break;
		}
	}

	fclose(fp);

	time(&ed);
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
		GenoMarginalDistr[0][1] = count0;
		GenoMarginalDistr[1][1] = count1;
		GenoMarginalDistr[2][1] = count2;
		GenoMarginalDistr[3][1] = count3;

		pMarginalDistrSNP_Y[(0 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count0;
		pMarginalDistrSNP_Y[(1 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count1;
		pMarginalDistrSNP_Y[(2 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count2;
		pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1] = count3;

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
		GenoMarginalDistr[0][0] = count0;
		GenoMarginalDistr[1][0] = count1;
		GenoMarginalDistr[2][0] = count2;
		GenoMarginalDistr[3][0] = count3;

		pMarginalDistrSNP_Y[(0 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count0;
		pMarginalDistrSNP_Y[(1 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count1;
		pMarginalDistrSNP_Y[(2 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count2;
		pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] = count3;


		for (i2 = 0; i2 < 4; i2++)
		{
			pMarginalDistrSNP[i2 * nsnp + i1] =
				pMarginalDistrSNP_Y[(i2 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + i1] +
				pMarginalDistrSNP_Y[(i2 * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + i1];
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

void Similarity() {
	uint64 x = 0, * x_c00 = NULL, * x_c01 = NULL, * x_c02 = NULL, * x_c03 = NULL, * x_c10 = NULL, * x_c11 = NULL, * x_c12 = NULL, * x_c13 = NULL;
	uint64 y = 0, * y_c00 = NULL, * y_c01 = NULL, * y_c02 = NULL, * y_c03 = NULL, * y_c10 = NULL, * y_c11 = NULL, * y_c12 = NULL, * y_c13 = NULL;
	uint64* z_c00 = NULL, * z_c01 = NULL, * z_c02 = NULL, * z_c03 = NULL, * z_c10 = NULL, * z_c11 = NULL, * z_c12 = NULL, * z_c13 = NULL;

	int j3;
	int  m, n, k, p, q, h, s, t;
	double p_xyz, p_xz, p_yz, p_Xz, p_Yz, p_xy_z, p_x_z, p_y_z, p_z, p_xYz, p_zY, p_Y, p_Xyz, p_zX, p_X, p_2, p_3, p1, p2, p3;
	int casefreq0[4][4], ctrlfreq0[4][4], casefreq1[4][4], ctrlfreq1[4][4], casefreq2[4][4], ctrlfreq2[4][4], casefreq3[4][4][4], ctrlfreq3[4][4][4];
	int freq0[4][4], freq1[4][4], freq2[4][4], freq3[4][4][4];
	int i, j, j1, j2;

	for (j1 = 0; j1 < nsnp; j1++) {
		for (j2 = 0; j2 < nsnp; j2++) {
			if (j1 != j2) {
				pmi[j1][j2] = 0.0;

				x_c00 = genocase_c0[j1]; x_c01 = genocase_c1[j1];
				x_c02 = genocase_c2[j1]; x_c03 = genocase_c3[j1];
				x_c10 = genoctrl_c0[j1]; x_c11 = genoctrl_c1[j1];
				x_c12 = genoctrl_c2[j1]; x_c13 = genoctrl_c3[j1];
				y_c00 = genocase_c0[j2]; y_c01 = genocase_c1[j2];
				y_c02 = genocase_c2[j2]; y_c03 = genocase_c3[j2];
				y_c10 = genoctrl_c0[j2]; y_c11 = genoctrl_c1[j2];
				y_c12 = genoctrl_c2[j2]; y_c13 = genoctrl_c3[j2];

				for (m = 0; m < 4; m++)
					for (n = 0; n < 4; n++)
					{
						casefreq0[m][n] = 0;
						ctrlfreq0[m][n] = 0;
					}

				for (h = 0; h < nlongintcase; h++) {
					x = x_c00[h] & y_c00[h]; casefreq0[0][0] += popcount(x);
					x = x_c00[h] & y_c01[h]; casefreq0[0][1] += popcount(x);
					x = x_c00[h] & y_c02[h]; casefreq0[0][2] += popcount(x);
					x = x_c01[h] & y_c00[h]; casefreq0[1][0] += popcount(x);
					x = x_c01[h] & y_c01[h]; casefreq0[1][1] += popcount(x);
					x = x_c01[h] & y_c02[h]; casefreq0[1][2] += popcount(x);
					x = x_c02[h] & y_c00[h]; casefreq0[2][0] += popcount(x);
					x = x_c02[h] & y_c01[h]; casefreq0[2][1] += popcount(x);
					x = x_c02[h] & y_c02[h]; casefreq0[2][2] += popcount(x);
				}

				for (h = 0; h < nlongintctrl; h++) {
					x = x_c10[h] & y_c10[h]; ctrlfreq0[0][0] += popcount(x);
					x = x_c10[h] & y_c11[h]; ctrlfreq0[0][1] += popcount(x);
					x = x_c10[h] & y_c12[h]; ctrlfreq0[0][2] += popcount(x);
					x = x_c11[h] & y_c10[h]; ctrlfreq0[1][0] += popcount(x);
					x = x_c11[h] & y_c11[h]; ctrlfreq0[1][1] += popcount(x);
					x = x_c11[h] & y_c12[h]; ctrlfreq0[1][2] += popcount(x);
					x = x_c12[h] & y_c10[h]; ctrlfreq0[2][0] += popcount(x);
					x = x_c12[h] & y_c11[h]; ctrlfreq0[2][1] += popcount(x);
					x = x_c12[h] & y_c12[h]; ctrlfreq0[2][2] += popcount(x);

				}
				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						freq0[m][n] = casefreq0[m][n] + ctrlfreq0[m][n];
					}
				}

				for (m = 0; m < 3; m++)
				{
					casefreq0[m][3] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - casefreq0[m][0] - casefreq0[m][1] - casefreq0[m][2];
					ctrlfreq0[m][3] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j1] - ctrlfreq0[m][0] - ctrlfreq0[m][1] - ctrlfreq0[m][2];
					freq0[m][3] = casefreq0[m][3] + ctrlfreq0[m][3];
					casefreq0[3][m] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j2] - casefreq0[0][m] - casefreq0[1][m] - casefreq0[2][m];
					ctrlfreq0[3][m] = pMarginalDistrSNP_Y[(m * MarginalDistrSNP_Y_DimensionX + 1) * nsnp + j2] - ctrlfreq0[0][m] - ctrlfreq0[1][m] - ctrlfreq0[2][m];
					freq0[3][m] = casefreq0[3][m] + ctrlfreq0[3][m];
				}
				casefreq0[3][3] = pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - casefreq0[3][0] - casefreq0[3][1] - casefreq0[3][2];
				ctrlfreq0[3][3] = pMarginalDistrSNP_Y[(3 * MarginalDistrSNP_Y_DimensionX + 0) * nsnp + j1] - ctrlfreq0[3][0] - ctrlfreq0[3][1] - ctrlfreq0[3][2];
				freq0[3][3] = casefreq0[3][3] + ctrlfreq0[3][3];

				do { j3 = rand() % (nsnp - 1) + 0; } while (j3 == j1 || j3 == j2);

				z_c00 = genocase_c0[j3]; z_c01 = genocase_c1[j3];
				z_c02 = genocase_c2[j3]; z_c03 = genocase_c3[j3];
				z_c10 = genoctrl_c0[j3]; z_c11 = genoctrl_c1[j3];
				z_c12 = genoctrl_c2[j3]; z_c13 = genoctrl_c3[j3];

				for (m = 0; m < 4; m++)
					for (n = 0; n < 4; n++)
					{
						casefreq1[m][n] = 0;
						ctrlfreq1[m][n] = 0;
					}

				for (h = 0; h < nlongintcase; h++) {
					x = x_c00[h] & z_c00[h]; casefreq1[0][0] += popcount(x);
					x = x_c00[h] & z_c01[h]; casefreq1[0][1] += popcount(x);
					x = x_c00[h] & z_c02[h]; casefreq1[0][2] += popcount(x);
					x = x_c01[h] & z_c00[h]; casefreq1[1][0] += popcount(x);
					x = x_c01[h] & z_c01[h]; casefreq1[1][1] += popcount(x);
					x = x_c01[h] & z_c02[h]; casefreq1[1][2] += popcount(x);
					x = x_c02[h] & z_c00[h]; casefreq1[2][0] += popcount(x);
					x = x_c02[h] & z_c01[h]; casefreq1[2][1] += popcount(x);
					x = x_c02[h] & z_c02[h]; casefreq1[2][2] += popcount(x);
				}

				for (h = 0; h < nlongintctrl; h++) {
					x = x_c10[h] & z_c10[h]; ctrlfreq1[0][0] += popcount(x);
					x = x_c10[h] & z_c11[h]; ctrlfreq1[0][1] += popcount(x);
					x = x_c10[h] & z_c12[h]; ctrlfreq1[0][2] += popcount(x);
					x = x_c11[h] & z_c10[h]; ctrlfreq1[1][0] += popcount(x);
					x = x_c11[h] & z_c11[h]; ctrlfreq1[1][1] += popcount(x);
					x = x_c11[h] & z_c12[h]; ctrlfreq1[1][2] += popcount(x);
					x = x_c12[h] & z_c10[h]; ctrlfreq1[2][0] += popcount(x);
					x = x_c12[h] & z_c11[h]; ctrlfreq1[2][1] += popcount(x);
					x = x_c12[h] & z_c12[h]; ctrlfreq1[2][2] += popcount(x);

				}
				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						freq1[m][n] = casefreq1[m][n] + ctrlfreq1[m][n];
					}
				}

				for (m = 0; m < 4; m++)
					for (n = 0; n < 4; n++)
					{
						casefreq2[m][n] = 0;
						ctrlfreq2[m][n] = 0;
					}

				for (h = 0; h < nlongintcase; h++) {
					x = y_c00[h] & z_c00[h]; casefreq2[0][0] += popcount(x);
					x = y_c00[h] & z_c01[h]; casefreq2[0][1] += popcount(x);
					x = y_c00[h] & z_c02[h]; casefreq2[0][2] += popcount(x);
					x = y_c01[h] & z_c00[h]; casefreq2[1][0] += popcount(x);
					x = y_c01[h] & z_c01[h]; casefreq2[1][1] += popcount(x);
					x = y_c01[h] & z_c02[h]; casefreq2[1][2] += popcount(x);
					x = y_c02[h] & z_c00[h]; casefreq2[2][0] += popcount(x);
					x = y_c02[h] & z_c01[h]; casefreq2[2][1] += popcount(x);
					x = y_c02[h] & z_c02[h]; casefreq2[2][2] += popcount(x);
				}

				for (h = 0; h < nlongintctrl; h++) {

					x = y_c10[h] & z_c10[h]; ctrlfreq2[0][0] += popcount(x);
					x = y_c10[h] & z_c11[h]; ctrlfreq2[0][1] += popcount(x);
					x = y_c10[h] & z_c12[h]; ctrlfreq2[0][2] += popcount(x);
					x = y_c11[h] & z_c10[h]; ctrlfreq2[1][0] += popcount(x);
					x = y_c11[h] & z_c11[h]; ctrlfreq2[1][1] += popcount(x);
					x = y_c11[h] & z_c12[h]; ctrlfreq2[1][2] += popcount(x);
					x = y_c12[h] & z_c10[h]; ctrlfreq2[2][0] += popcount(x);
					x = y_c12[h] & z_c11[h]; ctrlfreq2[2][1] += popcount(x);
					x = y_c12[h] & z_c12[h]; ctrlfreq2[2][2] += popcount(x);

				}
				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						freq2[m][n] = casefreq2[m][n] + ctrlfreq2[m][n];
					}
				}

				for (m = 0; m < 4; m++)
					for (n = 0; n < 4; n++)
						for (s = 0; s < 4; s++) {
							casefreq3[m][n][s] = 0;
							ctrlfreq3[m][n][s] = 0;
						}

				for (h = 0; h < nlongintcase; h++) {
					y = x_c00[h] & y_c00[h] & z_c00[h]; casefreq3[0][0][0] += popcount(y);
					y = x_c00[h] & y_c00[h] & z_c01[h]; casefreq3[0][0][1] += popcount(y);
					y = x_c00[h] & y_c00[h] & z_c02[h]; casefreq3[0][0][2] += popcount(y);
					y = x_c00[h] & y_c01[h] & z_c00[h]; casefreq3[0][1][0] += popcount(y);
					y = x_c00[h] & y_c01[h] & z_c01[h]; casefreq3[0][1][1] += popcount(y);
					y = x_c00[h] & y_c01[h] & z_c02[h]; casefreq3[0][1][2] += popcount(y);
					y = x_c00[h] & y_c02[h] & z_c00[h]; casefreq3[0][2][0] += popcount(y);
					y = x_c00[h] & y_c02[h] & z_c01[h]; casefreq3[0][2][1] += popcount(y);
					y = x_c00[h] & y_c02[h] & z_c02[h]; casefreq3[0][2][2] += popcount(y);

					y = x_c01[h] & y_c00[h] & z_c00[h]; casefreq3[1][0][0] += popcount(y);
					y = x_c01[h] & y_c00[h] & z_c01[h]; casefreq3[1][0][1] += popcount(y);
					y = x_c01[h] & y_c00[h] & z_c02[h]; casefreq3[1][0][2] += popcount(y);
					y = x_c01[h] & y_c01[h] & z_c00[h]; casefreq3[1][1][0] += popcount(y);
					y = x_c01[h] & y_c01[h] & z_c01[h]; casefreq3[1][1][1] += popcount(y);
					y = x_c01[h] & y_c01[h] & z_c02[h]; casefreq3[1][1][2] += popcount(y);
					y = x_c01[h] & y_c02[h] & z_c00[h]; casefreq3[1][2][0] += popcount(y);
					y = x_c01[h] & y_c02[h] & z_c01[h]; casefreq3[1][2][1] += popcount(y);
					y = x_c01[h] & y_c02[h] & z_c02[h]; casefreq3[1][2][2] += popcount(y);

					y = x_c02[h] & y_c00[h] & z_c00[h]; casefreq3[2][0][0] += popcount(y);
					y = x_c02[h] & y_c00[h] & z_c01[h]; casefreq3[2][0][1] += popcount(y);
					y = x_c02[h] & y_c00[h] & z_c02[h]; casefreq3[2][0][2] += popcount(y);
					y = x_c02[h] & y_c01[h] & z_c00[h]; casefreq3[2][1][0] += popcount(y);
					y = x_c02[h] & y_c01[h] & z_c01[h]; casefreq3[2][1][1] += popcount(y);
					y = x_c02[h] & y_c01[h] & z_c02[h]; casefreq3[2][1][2] += popcount(y);
					y = x_c02[h] & y_c02[h] & z_c00[h]; casefreq3[2][2][0] += popcount(y);
					y = x_c02[h] & y_c02[h] & z_c01[h]; casefreq3[2][2][1] += popcount(y);
					y = x_c02[h] & y_c02[h] & z_c02[h]; casefreq3[2][2][2] += popcount(y);
				}

				for (h = 0; h < nlongintctrl; h++) {
					y = x_c10[h] & y_c10[h] & z_c10[h]; ctrlfreq3[0][0][0] += popcount(y);
					y = x_c10[h] & y_c10[h] & z_c11[h]; ctrlfreq3[0][0][1] += popcount(y);
					y = x_c10[h] & y_c10[h] & z_c12[h]; ctrlfreq3[0][0][2] += popcount(y);
					y = x_c10[h] & y_c11[h] & z_c10[h]; ctrlfreq3[0][1][0] += popcount(y);
					y = x_c10[h] & y_c11[h] & z_c11[h]; ctrlfreq3[0][1][1] += popcount(y);
					y = x_c10[h] & y_c11[h] & z_c12[h]; ctrlfreq3[0][1][2] += popcount(y);
					y = x_c10[h] & y_c12[h] & z_c10[h]; ctrlfreq3[0][2][0] += popcount(y);
					y = x_c10[h] & y_c12[h] & z_c11[h]; ctrlfreq3[0][2][1] += popcount(y);
					y = x_c10[h] & y_c12[h] & z_c12[h]; ctrlfreq3[0][2][2] += popcount(y);

					y = x_c11[h] & y_c10[h] & z_c10[h]; ctrlfreq3[1][0][0] += popcount(y);
					y = x_c11[h] & y_c10[h] & z_c11[h]; ctrlfreq3[1][0][1] += popcount(y);
					y = x_c11[h] & y_c10[h] & z_c12[h]; ctrlfreq3[1][0][2] += popcount(y);
					y = x_c11[h] & y_c11[h] & z_c10[h]; ctrlfreq3[1][1][0] += popcount(y);
					y = x_c11[h] & y_c11[h] & z_c11[h]; ctrlfreq3[1][1][1] += popcount(y);
					y = x_c11[h] & y_c11[h] & z_c12[h]; ctrlfreq3[1][1][2] += popcount(y);
					y = x_c11[h] & y_c12[h] & z_c10[h]; ctrlfreq3[1][2][0] += popcount(y);
					y = x_c11[h] & y_c12[h] & z_c11[h]; ctrlfreq3[1][2][1] += popcount(y);
					y = x_c11[h] & y_c12[h] & z_c12[h]; ctrlfreq3[1][2][2] += popcount(y);

					y = x_c12[h] & y_c10[h] & z_c10[h]; ctrlfreq3[2][0][0] += popcount(y);
					y = x_c12[h] & y_c10[h] & z_c11[h]; ctrlfreq3[2][0][1] += popcount(y);
					y = x_c12[h] & y_c10[h] & z_c12[h]; ctrlfreq3[2][0][2] += popcount(y);
					y = x_c12[h] & y_c11[h] & z_c10[h]; ctrlfreq3[2][1][0] += popcount(y);
					y = x_c12[h] & y_c11[h] & z_c11[h]; ctrlfreq3[2][1][1] += popcount(y);
					y = x_c12[h] & y_c11[h] & z_c12[h]; ctrlfreq3[2][1][2] += popcount(y);
					y = x_c12[h] & y_c12[h] & z_c10[h]; ctrlfreq3[2][2][0] += popcount(y);
					y = x_c12[h] & y_c12[h] & z_c11[h]; ctrlfreq3[2][2][1] += popcount(y);
					y = x_c12[h] & y_c12[h] & z_c12[h]; ctrlfreq3[2][2][2] += popcount(y);
				}
				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						for (s = 0; s < 3; s++) {
							freq3[m][n][s] = casefreq3[m][n][s] + ctrlfreq3[m][n][s];
						}
					}
				}

				// PMI: z: the third SNPs (0,1,2)
				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						for (s = 0; s < 3; s++) {
							p_2 = 0.0;
							p_3 = 0.0;
							p2 = 0.0;
							p3 = 0.0;

							p_xyz = freq3[m][n][s] / double(nsample);

							if (freq3[m][n][s] == 0)
								pmi[j1][j2] += 0;
							else {
								p_z = pMarginalDistrSNP[s * nsnp + j3] / double(nsample);
								p_xy_z = p_xyz / p_z;
								p_xz = freq1[m][s] / double(nsample);
								p_yz = freq2[n][s] / double(nsample);
								p_x_z = p_xz / p_z;
								p_y_z = p_yz / p_z;
								p1 = log2f(p_xy_z / (p_x_z * p_y_z));

								for (t = 0; t < 3; t++) {
									p_xYz = freq3[m][t][s] / double(nsample);
									p_Yz = freq2[t][s] / double(nsample);
									p_Y = pMarginalDistrSNP[t * nsnp + j2] / double(nsample);

									if (pMarginalDistrSNP[t * nsnp + j2] == 0 || freq3[m][t][s] == 0 || freq2[t][s] == 0)
										p_2 += 0;
									else
										p_2 += p_xYz / p_Yz * p_Y;
								}
								if (p_2 == 0)
									p2 = 0;
								else
									p2 = log2f(p_x_z / p_2);

								for (t = 0; t < 3; t++) {
									p_Xyz = freq3[t][n][s] / double(nsample);
									p_Xz = freq1[t][s] / double(nsample);
									p_X = pMarginalDistrSNP[t * nsnp + j1] / double(nsample);

									if (pMarginalDistrSNP[t * nsnp + j1] == 0 || freq3[t][n][s] == 0 || freq1[t][s] == 0)
										p_3 += 0;
									else
										p_3 += p_Xyz / p_Xz * p_X;
								}
								if (p_3 == 0)
									p3 = 0;
								else
									p3 = log2f(p_y_z / p_3);

								pmi[j1][j2] += p_xyz * (p1 + p2 + p3);
							}
						}
					}
				}
			}
			else {
				pmi[j1][j2] = 1.0;
			}
		}
	}
}

/* ------------------------ Merge sorting ----------------------------- */
int Partition(double a[], int low, int high)
{
	double x = a[high];
	int i = low - 1;
	for (int j = low; j < high; j++)
	{
		if (a[j] < x)
		{
			double temp;
			i++;
			temp = a[i];
			a[i] = a[j];
			a[j] = temp;
		}
	}

	a[high] = a[i + 1];
	a[i + 1] = x;
	return i + 1;
}
void QuickSort(double a[], int low, int high)
{
	if (low < high)
	{
		int q = Partition(a, low, high);
		QuickSort(a, low, q - 1);
		QuickSort(a, q + 1, high);
	}
}

/* ------------------------ clustering and regression ----------------------------- */
void cluster() {
	int position;
	int i, j, i1, i2, m, k;
	double* pmi_sort;
	pmi_sort = (double*)malloc((nsnp * nsnp / 2) * sizeof(double));
	int count = -1;
	nclust = -1;
	double buf;
	int buf_num;
	double max, min;
	double rho_ave;
	int* nm, * nc, record;
	double** pmi_rec;
	pmi_rec = (double**)malloc((nsnp + 1) * sizeof(double*));
	for (i = 0; i < nsnp + 1; i++) {
		pmi_rec[i] = (double*)malloc((nsnp + 1) * sizeof(double));
	}

	for (i1 = 0; i1 < nsnp; i1++) {
		for (i2 = 0; i2 < nsnp; i2++) {
			pmi_rec[i1][i2] = 1.0 / pmi[i1][i2];
		}
	}
	for (i1 = 0; i1 < nsnp - 1; i1++) {
		for (i2 = i1 + 1; i2 < nsnp; i2++) {
			count++;
			pmi_sort[count] = pmi_rec[i1][i2];
		}
	}
	QuickSort(pmi_sort, 0, count);

	position = round(count * percent / 100);
	dc = pmi_sort[position];

	double* rho = (double*)malloc((nsnp + 1) * sizeof(double));
	for (i = 0; i < nsnp; i++)
		rho[i] = 0.0;
	int* rho_num = (int*)malloc((nsnp + 1) * sizeof(int));

	for (i = 0; i < nsnp - 1; i++) {
		for (j = i + 1; j < nsnp; j++) {
			rho[i] += exp(-(pmi_rec[i][j] / dc) * (pmi_rec[i][j] / dc));
			rho[j] += exp(-(pmi_rec[i][j] / dc) * (pmi_rec[i][j] / dc));
		}
	}
	for (i = 0; i < nsnp; i++) {
		rho_num[i] = i;
	}

	for (i = 0; i < nsnp - 1; i++) {
		for (j = 0; j < nsnp - 1 - i; j++) {
			if (rho[j] < rho[j + 1]) {
				buf = rho[j];
				rho[j] = rho[j + 1];
				rho[j + 1] = buf;

				buf_num = rho_num[j];
				rho_num[j] = rho_num[j + 1];
				rho_num[j + 1] = buf_num;
			}
		}
	}

	double* delta = (double*)malloc((nsnp + 1) * sizeof(double));
	int* nneight = (int*)malloc((nsnp + 1) * sizeof(int));
	for (i = 0; i < nsnp; i++)
		nneight[i] = -1;
	// calculate delta
	for (i = 1; i < nsnp; i++) {
		delta[rho_num[i]] = pmi_sort[count];
		for (j = 0; j < i; j++) {
			if (pmi_rec[rho_num[i]][rho_num[j]] < delta[rho_num[i]]) {
				delta[rho_num[i]] = pmi_rec[rho_num[i]][rho_num[j]];
				nneight[rho_num[i]] = rho_num[j];
			}
		}
	}
	max = delta[rho_num[1]];
	for (i = 2; i < nsnp; i++) {
		if (max < delta[rho_num[i]])
			max = delta[rho_num[i]];
	}
	delta[rho_num[0]] = max;

	double* gamma = (double*)malloc((nsnp + 1) * sizeof(double));
	int* gamma_num = (int*)malloc((nsnp + 1) * sizeof(int));
	for (i = 0; i < nsnp; i++) {
		gamma[rho_num[i]] = rho[i] * delta[rho_num[i]];
		gamma_num[i] = rho_num[i];
	}

	int* c = (int*)malloc((nsnp + 1) * sizeof(int));
	int* center = (int*)malloc(nsnp * sizeof(int));
	for (i = 0; i < nsnp; i++)
		c[i] = -1;
	for (i = 0; i < nsnp; i++) {
		if (gamma[rho_num[i]] > 5000) {
			// cout << "我运行了" << endl;
			nclust++;
			c[rho_num[i]] = nclust;
			center[nclust] = rho_num[i];
		}
	}
	for (i = 0; i < nsnp; i++) {
		// 这里正常
		// cout << rho_num[i] << endl;
		if (c[rho_num[i]] == -1) {
			// 问题在这一句话，c[rho_num[i]] = c[-1],越界
			c[rho_num[i]] = c[nneight[rho_num[i]]];
		}
	}

	int* halo = (int*)malloc((nsnp + 1) * sizeof(int));
	for (i = 0; i < nsnp; i++)
		halo[i] = c[i];

	double* bord_rho = (double*)malloc((nclust + 1) * sizeof(double));
	for (i = 0; i < nclust + 1; i++) {
		bord_rho[i] = 0.0;
	}
	// 测试
	// cout << "人工断点1" << endl;
	/*
	for (i = 0; i < nsnp; i++) {
		cout << "c[ " << i << " ]: " << c[i] << endl;
	}
	*/
	for (i = 0; i < nsnp - 1; i++) {
		for (j = i + 1; j < nsnp; j++) {
			if ((c[i] != c[j]) && (pmi_rec[i][j] <= dc)) {
				rho_ave = 0.5 * (rho[i] + rho[j]);
				if (rho_ave > bord_rho[c[i]])
					bord_rho[c[i]] = rho_ave;
				if (rho_ave > bord_rho[c[j]])
					bord_rho[c[j]] = rho_ave;
			}
		}
	}


	for (i = 0; i < nsnp; i++) {
		// cout << c[i] << endl;
		if (rho[i] < bord_rho[c[i]])
			halo[i] = -1;
	}

	nm = (int*)malloc((nclust + 2) * sizeof(int));
	nc = (int*)malloc((nclust + 2) * sizeof(int));
	for (i = 0; i < nclust + 1; i++) {
		nm[i] = 0;
		nc[i] = 0;
		for (j = 0; j < nsnp; j++) {
			if (c[j] == i) nm[i]++;
			if (halo[j] == i) nc[i]++;
		}
	}

	cluster_candidate = (int**)malloc((nclust + 1) * sizeof(int*));
	for (i = 0; i < nclust + 1; i++) {
		cluster_candidate[i] = (int*)malloc(nsnp * sizeof(int));
	}
	cluster_count = (int*)malloc((nclust + 1) * sizeof(int));

	for (i = 0; i < nclust + 1; i++) {
		m = -1;
		for (j = 0; j < nsnp; j++) {
			if (c[j] == i) {
				m++;
				record = j;
				cluster_candidate[K][m] = j;
			}
		}
		cluster_count[K] = m + 1;
		K++;
	}

	// free
	free(nm);
	free(nc);
	for (i == 0; i < (nsnp + 1); i++)
		free(pmi_rec[i]);
	free(pmi_rec);
	free(bord_rho);
	free(halo);
	free(center);
	free(c);
	free(gamma);
	free(gamma_num);
	free(nneight);
	free(delta);
	free(rho_num);
	free(rho);
	free(pmi_sort);
}

struct SNPData {
	vector<int> features;
	int cls;
	SNPData(vector<int> f, int c) :features(f), cls(c) {
	}
};
struct Param {
	vector<double> w;
	Param(vector<double> w1) :w(w1) {};
	Param() :w(vector<double>()) {}
};
class Logistic {
public:
	Logistic() {
		loadDataSet(dataSet);
		vector<double> pw(dataSet[0].features.size());
		//Param pt(pw,0.0);
		Param pt(pw);
		param = pt;
	};
	void loadDataSet(vector<SNPData>& ds) {
		int i, j, k;
		int t;

		i = 0;
		while (i < nsample)
		{
			vector<int> fea;
			int cl;
			cl = Y_label[i];

			j = 0;
			while (j < cluster_count[nk])
			{
				t = X[i][cluster_candidate[nk][j] + 1];
				fea.push_back(t);

				++j;
			}
			ds.push_back(SNPData(fea, cl));
			++i;
		}
	}

	double vecnorm(const Param& p1, const Param& p2) {
		double sum = 0.0;
		for (int i = 0; i < p1.w.size(); i++) {
			double minus = p1.w[i] - p2.w[i];
			double r = minus * minus;
			sum += r;
		}
		return sqrt(sum);
	}

	double l1norm(const Param& p) {

		double sum = 0.0;
		for (int i = 0; i < p.w.size(); i++) {
			sum += fabs(p.w[i]);
		}
		return sum;
	}

	double sigmoid(double x) {
		return 1.0 / (1.0 + exp(-x));
	}

	double classify(const Param& p, const SNPData& data) {

		double logit = 0.0;
		for (int i = 0; i < p.w.size(); i++) {
			logit += (p.w[i] * data.features[i]);
		}
		return sigmoid(logit);
	}

	void logisticRegression() {
		double alpha = 0.001;
		double l1 = 0.00001;
		unsigned int maxit = 2000;
		int shuf = 1;
		double eps = 0.005;
		double buf;
		int buf_num, * W_num, T, record;
		double* W, * W_pmi;
		W_num = (int*)malloc((cluster_count[nk] + 1) * sizeof(int));
		W = (double*)malloc((cluster_count[nk] + 1) * sizeof(double));
		W_pmi = (double*)malloc((cluster_count[nk] + 1) * sizeof(double));
		double sum, max_pmi;

		double mu = 0.0;
		double norm = 1.0;
		int n = 0;
		vector<int> index(dataSet.size());

		double* total_l1;
		total_l1 = (double*)malloc((param.w.size()) * sizeof(double));

		for (int i = 0; i < param.w.size(); i++) {
			param.w[i] = 0.0;
			total_l1[i] = 0.0;
		}

		while (norm > eps) {
			Param tpa(param.w);

			for (int i = 0; i < dataSet.size(); i++) {
				mu += (l1 * alpha);
				int label = dataSet[i].cls;
				double predicted = classify(param, dataSet[i]);

				for (int j = 0; j < dataSet[i].features.size(); j++) {
					param.w[j] += alpha * (label - predicted) * dataSet[i].features[j];
					if (l1) {
						double z = param.w[j];
						if (param.w[j] > 0.0) {
							param.w[j] = max(0.0, (double)(param.w[j] - (mu + total_l1[j])));
						}
						else if (param.w[j] < 0.0) {
							param.w[j] = min(0.0, (double)(param.w[j] + (mu - total_l1[j])));
						}
						total_l1[j] += (param.w[j] - z);
					}
				}
			}

			norm = vecnorm(param, tpa);
			tpa = Param(param.w);
			if (++n > maxit) {
				break;
			}
		}

		unsigned int sparsity = 0;
		for (int i = 0; i < param.w.size(); i++) {
			if (param.w[i] != 0) sparsity++;
		}

		for (int i = 0; i < param.w.size(); i++) {
			W_num[i] = i;
		}

		double* w_abs = (double*)malloc((cluster_count[nk] + 1) * sizeof(double));
		for (int i = 0; i < param.w.size(); i++) {
			w_abs[i] = fabs(param.w[i]);
		}
		double max_w = w_abs[0], min_w = w_abs[0];
		for (int i = 1; i < param.w.size(); i++) {
			if (max_w < w_abs[i])
				max_w = w_abs[i];
			if (min_w > w_abs[i])
				min_w = w_abs[i];
		}
		double* w_norm = (double*)malloc((cluster_count[nk] + 1) * sizeof(double));
		for (int i = 0; i < param.w.size(); i++) {
			w_norm[i] = (w_abs[i] - min_w) / (max_w - min_w);
		}

		for (int i1 = 0; i1 < cluster_count[nk]; i1++) {
			max_pmi = 0.0;
			for (int i2 = 0; i2 < cluster_count[nk]; i2++) {
				if (i1 != i2) {
					if (max_pmi < pmi[cluster_candidate[nk][i1]][cluster_candidate[nk][i2]])
						max_pmi = pmi[cluster_candidate[nk][i1]][cluster_candidate[nk][i2]];
				}
			}
			W_pmi[i1] = max_pmi;
		}
		// Normalized processing
		double max_npmi = W_pmi[0], min_npmi = W_pmi[0];
		for (int i = 1; i < cluster_count[nk]; i++) {
			if (max_npmi < W_pmi[i])
				max_npmi = W_pmi[i];
			if (min_npmi > W_pmi[i])
				min_npmi = W_pmi[i];
		}
		double* W_npmi = (double*)malloc((cluster_count[nk] + 1) * sizeof(double));
		for (int i = 0; i < cluster_count[nk]; i++) {
			W_npmi[i] = (W_pmi[i] - min_npmi) / (max_npmi - min_npmi);
		}

		for (int i = 0; i < cluster_count[nk]; i++) {
			W[i] = w_norm[i] * W_npmi[i];
		}

		for (int i = 0; i < cluster_count[nk] - 1; i++) {
			for (int j = 0; j < cluster_count[nk] - 1 - i; j++) {
				if (fabs(W[j]) < fabs(W[j + 1])) {
					buf = W[j];
					W[j] = W[j + 1];
					W[j + 1] = buf;

					buf_num = W_num[j];
					W_num[j] = W_num[j + 1];
					W_num[j + 1] = buf_num;
				}
			}
		}

		T = round(double(cluster_count[nk]) / double(nsnp) * topT);
		for (int i = 0; i < T; i++) {
			record = cluster_candidate[nk][W_num[i]];
			candidate.push_back(record);
		}

		free(w_abs);
		free(W);
		free(W_pmi);
		free(W_npmi);
		free(w_norm);
		free(W_num);
		free(total_l1);
		vector<int>().swap(index);
	}

private:
	vector<SNPData> dataSet;
	Param param;
};

/* -------------------------------- MDR --------------------------------------- */
void mdr_init() {
	int i;

	X_case1 = (int**)malloc((ncase / fold) * sizeof(int*));
	for (i = 0; i < ncase / fold; i++)
		X_case1[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_case2 = (int**)malloc((ncase / fold) * sizeof(int*));
	for (i = 0; i < ncase / fold; i++)
		X_case2[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_case3 = (int**)malloc((ncase / fold) * sizeof(int*));
	for (i = 0; i < ncase / fold; i++)
		X_case3[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_case4 = (int**)malloc((ncase / fold) * sizeof(int*));
	for (i = 0; i < ncase / fold; i++)
		X_case4[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_case5 = (int**)malloc((ncase / fold) * sizeof(int*));
	for (i = 0; i < ncase / fold; i++)
		X_case5[i] = (int*)malloc((nsnp + 1) * sizeof(int));

	X_ctrl1 = (int**)malloc((nctrl / fold) * sizeof(int*));
	for (i = 0; i < nctrl / fold; i++)
		X_ctrl1[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_ctrl2 = (int**)malloc((nctrl / fold) * sizeof(int*));
	for (i = 0; i < nctrl / fold; i++)
		X_ctrl2[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_ctrl3 = (int**)malloc((nctrl / fold) * sizeof(int*));
	for (i = 0; i < nctrl / fold; i++)
		X_ctrl3[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_ctrl4 = (int**)malloc((nctrl / fold) * sizeof(int*));
	for (i = 0; i < nctrl / fold; i++)
		X_ctrl4[i] = (int*)malloc((nsnp + 1) * sizeof(int));
	X_ctrl5 = (int**)malloc((nctrl / fold) * sizeof(int*));
	for (i = 0; i < nctrl / fold; i++)
		X_ctrl5[i] = (int*)malloc((nsnp + 1) * sizeof(int));

}

void mdr_free() {
	int i;

	for (i = 0; i < ncase / fold; i++)
		free(X_case1[i]);
	free(X_case1);
	for (i = 0; i < ncase / fold; i++)
		free(X_case2[i]);
	free(X_case2);
	for (i = 0; i < ncase / fold; i++)
		free(X_case3[i]);
	free(X_case3);
	for (i = 0; i < ncase / fold; i++)
		free(X_case4[i]);
	free(X_case4);
	for (i = 0; i < ncase / fold; i++)
		free(X_case5[i]);
	free(X_case5);

	for (i = 0; i < nctrl / fold; i++)
		free(X_ctrl1[i]);
	free(X_ctrl1);
	for (i = 0; i < nctrl / fold; i++)
		free(X_ctrl2[i]);
	free(X_ctrl2);
	for (i = 0; i < nctrl / fold; i++)
		free(X_ctrl3[i]);
	free(X_ctrl3);
	for (i = 0; i < nctrl / fold; i++)
		free(X_ctrl4[i]);
	free(X_ctrl4);
	for (i = 0; i < nctrl / fold; i++)
		free(X_ctrl5[i]);
	free(X_ctrl5);

	for (i = 0; i < ncase; i++)
		free(X_case[i]);
	free(X_case);

	for (i = 0; i < nctrl; i++)
		free(X_ctrl[i]);
	free(X_ctrl);

	for (i = 0; i < nsample; i++)
		free(X[i]);
	free(X);
}

// cross validation
void CV() {
	int i, j, i1, i2, j1, j2, m, n;
	int p, q;
	int tmp;

	// randomly sorting case sample
	i = ncase;
	while (i--) {
		p = rand() % (ncase - 1);
		q = rand() % (ncase - 1);
		for (m = 0; m < (nsnp + 1); m++) {
			tmp = X_case[p][m];
			X_case[p][m] = X_case[q][m];
			X_case[q][m] = tmp;
		}
	}

	// randomly sorting control sample
	j = nctrl;
	while (j--) {
		p = rand() % (nctrl - 1);
		q = rand() % (nctrl - 1);
		for (n = 0; n < (nsnp + 1); n++) {
			tmp = X_ctrl[p][n];
			X_ctrl[p][n] = X_ctrl[q][n];
			X_ctrl[q][n] = tmp;
		}
	}

	i1 = 0;
	for (i = 0; i < ncase / fold; i++) {
		for (m = 0; m < (nsnp + 1); m++) {
			X_case1[i1][m] = X_case[i][m];
		}
		i1++;
	}
	i1 = 0;
	for (i = ncase / fold; i < 2 * (ncase / fold); i++) {
		for (m = 0; m < (nsnp + 1); m++) {
			X_case2[i1][m] = X_case[i][m];
		}
		i1++;
	}
	i1 = 0;
	for (i = 2 * (ncase / fold); i < 3 * (ncase / fold); i++) {
		for (m = 0; m < (nsnp + 1); m++) {
			X_case3[i1][m] = X_case[i][m];
		}
		i1++;
	}
	i1 = 0;
	for (i = 3 * (ncase / fold); i < 4 * (ncase / fold); i++) {
		for (m = 0; m < (nsnp + 1); m++) {
			X_case4[i1][m] = X_case[i][m];
		}
		i1++;
	}
	i1 = 0;
	for (i = 4 * (ncase / fold); i < 5 * (ncase / fold); i++) {
		for (m = 0; m < (nsnp + 1); m++) {
			X_case5[i1][m] = X_case[i][m];
		}
		i1++;
	}

	i2 = 0;
	for (i = 0; i < nctrl / fold; i++) {
		for (n = 0; n < (nsnp + 1); n++) {
			X_ctrl1[i2][n] = X_ctrl[i][n];
		}
		i2++;
	}
	i2 = 0;
	for (i = nctrl / fold; i < 2 * (nctrl / fold); i++) {
		for (n = 0; n < (nsnp + 1); n++) {
			X_ctrl2[i2][n] = X_ctrl[i][n];
		}
		i2++;
	}
	i2 = 0;;
	for (i = 2 * (nctrl / fold); i < 3 * (nctrl / fold); i++) {
		for (n = 0; n < (nsnp + 1); n++) {
			X_ctrl3[i2][n] = X_ctrl[i][n];
		}
		i2++;
	}
	i2 = 0;
	for (i = 3 * (nctrl / fold); i < 4 * (nctrl / fold); i++) {
		for (n = 0; n < (nsnp + 1); n++) {
			X_ctrl4[i2][n] = X_ctrl[i][n];
		}
		i2++;
	}
	i2 = 0;
	for (i = 4 * (nctrl / fold); i < 5 * (nctrl / fold); i++) {
		for (n = 0; n < (nsnp + 1); n++) {
			X_ctrl5[i2][n] = X_ctrl[i][n];
		}
		i2++;
	}
}

void MDR_interactions() {
	int i, j, i1, i2, i3, j1, j2, j3, u, v, flag, count_inter;
	int icase, ictrl;
	uint64 mask1 = 0x0000000000000001;
	int* snps = new int[2];
	double r_train[3][3], r_test[3][3]; // the ratio
	int flag_train[3][3], flag_test[3][3]; // flag=1 denotes high risk, 0 denotes low risk
	double w[3][3];
	double TP_w, FP_w, TN_w, FN_w;
	double buf0, buf1, buf2;    // for sorting
	int o = comb_num(candidate.size(), 2);
	double** per;
	per = (double**)malloc((o + 1) * sizeof(double*));
	for (i = 0; i < o + 1; i++)
		per[i] = (double*)malloc((fold + 1) * sizeof(double));

	int* interaction_sort = (int*)malloc((o + 1) * sizeof(int*));

	uint64 x, * x_c00, * x_c01, * x_c02, * x_c03, * x_c10, * x_c11, * x_c12, * x_c13;
	uint64* y_c00, * y_c01, * y_c02, * y_c03, * y_c10, * y_c11, * y_c12, * y_c13;
	int casefreq_train[4][4], ctrlfreq_train[4][4], casefreq_test[4][4], ctrlfreq_test[4][4];
	int m, n, t, k, h;
	double sum;

	CV();

	for (k = 0; k < fold; k++) {    // k: the k-th CV
		if (k == 0) {              // CV1
			// training
			X_train = (int**)malloc((nsample / fold * (fold - 1)) * sizeof(int*));
			for (i = 0; i < (nsample / fold * (fold - 1)); i++)
				X_train[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i1 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = ncase / fold; i < 2 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 2 * (ncase / fold); i < 3 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 3 * (ncase / fold); i < 4 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case4[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold); i < 4 * (ncase / fold) + (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + (nctrl / fold); i < 4 * (ncase / fold) + 2 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 2 * (nctrl / fold); i < 4 * (ncase / fold) + 3 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 3 * (nctrl / fold); i < 4 * (ncase / fold) + 4 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl4[i1][u];
				}
				i1++;
			}

			// test
			X_test = (int**)malloc((nsample / fold) * sizeof(int*));
			for (i = 0; i < (nsample / fold); i++)
				X_test[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i2 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_case5[i2][v];
				}
				i2++;
			}
			i2 = 0;
			for (i = ncase / fold; i < ncase / fold + nctrl / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_ctrl5[i2][v];
				}
				i2++;
			}
		}

		// CV2
		else if (k == 1) {
			// training
			X_train = (int**)malloc((nsample / fold * (fold - 1)) * sizeof(int*));
			for (i = 0; i < (nsample / fold * (fold - 1)); i++)
				X_train[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i1 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = ncase / fold; i < 2 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 2 * (ncase / fold); i < 3 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 3 * (ncase / fold); i < 4 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case5[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold); i < 4 * (ncase / fold) + (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + (nctrl / fold); i < 4 * (ncase / fold) + 2 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 2 * (nctrl / fold); i < 4 * (ncase / fold) + 3 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 3 * (nctrl / fold); i < 4 * (ncase / fold) + 4 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl5[i1][u];
				}
				i1++;
			}

			// test
			X_test = (int**)malloc((nsample / fold) * sizeof(int*));
			for (i = 0; i < (nsample / fold); i++)
				X_test[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i2 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_case4[i2][v];
				}
				i2++;
			}
			i2 = 0;
			for (i = ncase / fold; i < ncase / fold + nctrl / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_ctrl4[i2][v];
				}
				i2++;
			}
		}

		// CV3
		else if (k == 2) {
			// training
			X_train = (int**)malloc((nsample / fold * (fold - 1)) * sizeof(int*));
			for (i = 0; i < (nsample / fold * (fold - 1)); i++)
				X_train[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i1 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = ncase / fold; i < 2 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 2 * (ncase / fold); i < 3 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case4[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 3 * (ncase / fold); i < 4 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case5[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold); i < 4 * (ncase / fold) + (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + (nctrl / fold); i < 4 * (ncase / fold) + 2 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 2 * (nctrl / fold); i < 4 * (ncase / fold) + 3 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl5[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 3 * (nctrl / fold); i < 4 * (ncase / fold) + 4 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl5[i1][u];
				}
				i1++;
			}

			// test
			X_test = (int**)malloc((nsample / fold) * sizeof(int*));
			for (i = 0; i < (nsample / fold); i++)
				X_test[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i2 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_case3[i2][v];
				}
				i2++;
			}
			i2 = 0;
			for (i = ncase / fold; i < ncase / fold + nctrl / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_ctrl3[i2][v];
				}
				i2++;
			}
		}

		// CV4
		else if (k == 3) {
			// training
			X_train = (int**)malloc((nsample / fold * (fold - 1)) * sizeof(int*));
			for (i = 0; i < (nsample / fold * (fold - 1)); i++)
				X_train[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i1 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = ncase / fold; i < 2 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 2 * (ncase / fold); i < 3 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case4[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 3 * (ncase / fold); i < 4 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case5[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold); i < 4 * (ncase / fold) + (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl1[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + (nctrl / fold); i < 4 * (ncase / fold) + 2 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 2 * (nctrl / fold); i < 4 * (ncase / fold) + 3 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl4[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 3 * (nctrl / fold); i < 4 * (ncase / fold) + 4 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl5[i1][u];
				}
				i1++;
			}

			// test
			X_test = (int**)malloc((nsample / fold) * sizeof(int*));
			for (i = 0; i < (nsample / fold); i++)
				X_test[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i2 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_case2[i2][v];
				}
				i2++;
			}
			i2 = 0;
			for (i = ncase / fold; i < ncase / fold + nctrl / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_ctrl2[i2][v];
				}
				i2++;
			}
		}

		// CV5
		else if (k == 4) {
			// CV5
			X_train = (int**)malloc((nsample / fold * (fold - 1)) * sizeof(int*));
			for (i = 0; i < (nsample / fold * (fold - 1)); i++)
				X_train[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i1 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = ncase / fold; i < 2 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 2 * (ncase / fold); i < 3 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case4[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 3 * (ncase / fold); i < 4 * (ncase / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_case5[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold); i < 4 * (ncase / fold) + (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl2[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + (nctrl / fold); i < 4 * (ncase / fold) + 2 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl3[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 2 * (nctrl / fold); i < 4 * (ncase / fold) + 3 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl4[i1][u];
				}
				i1++;
			}
			i1 = 0;
			for (i = 4 * (ncase / fold) + 3 * (nctrl / fold); i < 4 * (ncase / fold) + 4 * (nctrl / fold); i++) {
				for (u = 0; u < (nsnp + 1); u++) {
					X_train[i][u] = X_ctrl5[i1][u];
				}
				i1++;
			}
			// test
			X_test = (int**)malloc((nsample / fold) * sizeof(int*));
			for (i = 0; i < (nsample / fold); i++)
				X_test[i] = (int*)malloc((nsnp + 1) * sizeof(int));

			i2 = 0;
			for (i = 0; i < ncase / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_case1[i2][v];
				}
				i2++;
			}
			i2 = 0;
			for (i = ncase / fold; i < ncase / fold + nctrl / fold; i++) {
				for (v = 0; v < (nsnp + 1); v++) {
					X_test[i][v] = X_ctrl1[i2][v];
				}
				i2++;
			}
		}

		// training
		genocase_train_c0 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genocase_train_c1 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genocase_train_c2 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genocase_train_c3 = (uint64**)calloc(nsnp, sizeof(uint64*));

		genoctrl_train_c0 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genoctrl_train_c1 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genoctrl_train_c2 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genoctrl_train_c3 = (uint64**)calloc(nsnp, sizeof(uint64*));
		for (i = 0; i < nsnp; i++) {
			genocase_train_c0[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
			genocase_train_c1[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
			genocase_train_c2[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
			genocase_train_c3[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));

			genoctrl_train_c0[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
			genoctrl_train_c1[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
			genoctrl_train_c2[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
			genoctrl_train_c3[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
		}

		icase = -1;
		ictrl = -1;

		for (i = 0; i < nsample / fold * (fold - 1); i++) {
			for (j = 0; j < (nsnp + 1); j++) {
				if (j == 0) {
					if (X_train[i][j] == 1) { icase++; flag = 1; } // case
					else { ictrl++; flag = 0; }  // contral
				}
				else {
					if (flag) {
						switch (X_train[i][j])
						{
						case 0: genocase_train_c0[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						case 1: genocase_train_c1[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						case 2: genocase_train_c2[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						case 3: genocase_train_c3[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						default: break;
						}
					}
					else
					{
						switch (X_train[i][j])
						{
						case 0: genoctrl_train_c0[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						case 1: genoctrl_train_c1[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						case 2: genoctrl_train_c2[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						case 3: genoctrl_train_c3[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						default: break;
						}
					}
				}
			}
		}

		// test
		genocase_test_c0 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genocase_test_c1 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genocase_test_c2 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genocase_test_c3 = (uint64**)calloc(nsnp, sizeof(uint64*));

		genoctrl_test_c0 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genoctrl_test_c1 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genoctrl_test_c2 = (uint64**)calloc(nsnp, sizeof(uint64*));
		genoctrl_test_c3 = (uint64**)calloc(nsnp, sizeof(uint64*));
		for (i = 0; i < nsnp; i++) {
			genocase_test_c0[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
			genocase_test_c1[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
			genocase_test_c2[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));
			genocase_test_c3[i] = (uint64*)calloc(nlongintcase, sizeof(uint64));

			genoctrl_test_c0[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
			genoctrl_test_c1[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
			genoctrl_test_c2[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
			genoctrl_test_c3[i] = (uint64*)calloc(nlongintctrl, sizeof(uint64));
		}

		icase = -1;
		ictrl = -1;

		for (i = 0; i < nsample / fold; i++) {
			for (j = 0; j < (nsnp + 1); j++) {
				if (j == 0) {
					if (X_test[i][j] == 1) { icase++; flag = 1; }
					else { ictrl++; flag = 0; }
				}
				else {
					if (flag) {
						switch (X_test[i][j])
						{
						case 0: genocase_test_c0[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						case 1: genocase_test_c1[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						case 2: genocase_test_c2[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						case 3: genocase_test_c3[j - 1][icase / LengthLongType] |= (mask1 << (icase % LengthLongType)); break;
						default: break;
						}
					}
					else
					{
						switch (X_test[i][j])
						{
						case 0: genoctrl_test_c0[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						case 1: genoctrl_test_c1[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						case 2: genoctrl_test_c2[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						case 3: genoctrl_test_c3[j - 1][ictrl / LengthLongType] |= (mask1 << (ictrl % LengthLongType)); break;
						default: break;
						}
					}
				}
			}
		}

		count_inter = -1;
		for (j1 = 0; j1 < candidate.size() - 1; j1++) {
			for (j2 = j1 + 1; j2 < candidate.size(); j2++) {

				snps[0] = (int)candidate[j1];
				snps[1] = (int)candidate[j2];

				sort(snps, snps + 2);
				count_inter++;

				// training
				x_c00 = genocase_train_c0[snps[0]]; x_c01 = genocase_train_c1[snps[0]];
				x_c02 = genocase_train_c2[snps[0]]; x_c03 = genocase_train_c3[snps[0]];
				x_c10 = genoctrl_train_c0[snps[0]]; x_c11 = genoctrl_train_c1[snps[0]];
				x_c12 = genoctrl_train_c2[snps[0]]; x_c13 = genoctrl_train_c3[snps[0]];

				y_c00 = genocase_train_c0[snps[1]]; y_c01 = genocase_train_c1[snps[1]];
				y_c02 = genocase_train_c2[snps[1]]; y_c03 = genocase_train_c3[snps[1]];
				y_c10 = genoctrl_train_c0[snps[1]]; y_c11 = genoctrl_train_c1[snps[1]];
				y_c12 = genoctrl_train_c2[snps[1]]; y_c13 = genoctrl_train_c3[snps[1]];

				for (m = 0; m < 3; m++)
					for (n = 0; n < 3; n++)
					{
						casefreq_train[m][n] = 0;
						ctrlfreq_train[m][n] = 0;
					}

				for (h = 0; h < nlongintcase; h++) {
					x = x_c00[h] & y_c00[h]; casefreq_train[0][0] += popcount(x);
					x = x_c00[h] & y_c01[h]; casefreq_train[0][1] += popcount(x);
					x = x_c00[h] & y_c02[h]; casefreq_train[0][2] += popcount(x);
					x = x_c01[h] & y_c00[h]; casefreq_train[1][0] += popcount(x);
					x = x_c01[h] & y_c01[h]; casefreq_train[1][1] += popcount(x);
					x = x_c01[h] & y_c02[h]; casefreq_train[1][2] += popcount(x);
					x = x_c02[h] & y_c00[h]; casefreq_train[2][0] += popcount(x);
					x = x_c02[h] & y_c01[h]; casefreq_train[2][1] += popcount(x);
					x = x_c02[h] & y_c02[h]; casefreq_train[2][2] += popcount(x);
				}

				for (h = 0; h < nlongintctrl; h++) {
					x = x_c10[h] & y_c10[h]; ctrlfreq_train[0][0] += popcount(x);
					x = x_c10[h] & y_c11[h]; ctrlfreq_train[0][1] += popcount(x);
					x = x_c10[h] & y_c12[h]; ctrlfreq_train[0][2] += popcount(x);
					x = x_c11[h] & y_c10[h]; ctrlfreq_train[1][0] += popcount(x);
					x = x_c11[h] & y_c11[h]; ctrlfreq_train[1][1] += popcount(x);
					x = x_c11[h] & y_c12[h]; ctrlfreq_train[1][2] += popcount(x);
					x = x_c12[h] & y_c10[h]; ctrlfreq_train[2][0] += popcount(x);
					x = x_c12[h] & y_c11[h]; ctrlfreq_train[2][1] += popcount(x);
					x = x_c12[h] & y_c12[h]; ctrlfreq_train[2][2] += popcount(x);
				}

				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						if (casefreq_train[m][n] == 0 & ctrlfreq_train[m][n] != 0) {
							r_train[m][n] = (double(casefreq_train[m][n]) + constant) / double(ctrlfreq_train[m][n]) * ((double)nctrl / (double)ncase);
							if (r_train[m][n] == mdr_threshold) {
								r_train[m][n] = r_train[m][n] + constant;
								flag_train[m][n] = 1;
							}
							else if (r_train[m][n] > mdr_threshold) flag_train[m][n] = 1;
							else flag_train[m][n] = 0;
						}
						else if (casefreq_train[m][n] != 0 & ctrlfreq_train[m][n] == 0) {
							r_train[m][n] = double(casefreq_train[m][n]) / (double(ctrlfreq_train[m][n]) + constant) * ((double)nctrl / (double)ncase);
							if (r_train[m][n] == mdr_threshold) {
								r_train[m][n] = r_train[m][n] + constant;
								flag_train[m][n] = 1;
							}
							else if (r_train[m][n] > mdr_threshold) flag_train[m][n] = 1;
							else flag_train[m][n] = 0;
						}
						else if (casefreq_train[m][n] == 0 & ctrlfreq_train[m][n] == 0) {
							flag_train[m][n] = -1;
						}
						else {
							r_train[m][n] = double(casefreq_train[m][n]) / double(ctrlfreq_train[m][n]) * ((double)nctrl / (double)ncase);
							if (r_train[m][n] == mdr_threshold) {
								r_train[m][n] = r_train[m][n] + constant;
								flag_train[m][n] = 1;
							}
							else if (r_train[m][n] > mdr_threshold) flag_train[m][n] = 1;
							else flag_train[m][n] = 0;
						}
					}
				}

				// test
				x_c00 = genocase_test_c0[snps[0]]; x_c01 = genocase_test_c1[snps[0]];
				x_c02 = genocase_test_c2[snps[0]]; x_c03 = genocase_test_c3[snps[0]];
				x_c10 = genoctrl_test_c0[snps[0]]; x_c11 = genoctrl_test_c1[snps[0]];
				x_c12 = genoctrl_test_c2[snps[0]]; x_c13 = genoctrl_test_c3[snps[0]];

				y_c00 = genocase_test_c0[snps[1]]; y_c01 = genocase_test_c1[snps[1]];
				y_c02 = genocase_test_c2[snps[1]]; y_c03 = genocase_test_c3[snps[1]];
				y_c10 = genoctrl_test_c0[snps[1]]; y_c11 = genoctrl_test_c1[snps[1]];
				y_c12 = genoctrl_test_c2[snps[1]]; y_c13 = genoctrl_test_c3[snps[1]];

				for (m = 0; m < 4; m++)
					for (n = 0; n < 4; n++)
					{
						casefreq_test[m][n] = 0;
						ctrlfreq_test[m][n] = 0;
					}

				for (h = 0; h < nlongintcase; h++) {
					x = x_c00[h] & y_c00[h]; casefreq_test[0][0] += popcount(x);
					x = x_c00[h] & y_c01[h]; casefreq_test[0][1] += popcount(x);
					x = x_c00[h] & y_c02[h]; casefreq_test[0][2] += popcount(x);
					x = x_c01[h] & y_c00[h]; casefreq_test[1][0] += popcount(x);
					x = x_c01[h] & y_c01[h]; casefreq_test[1][1] += popcount(x);
					x = x_c01[h] & y_c02[h]; casefreq_test[1][2] += popcount(x);
					x = x_c02[h] & y_c00[h]; casefreq_test[2][0] += popcount(x);
					x = x_c02[h] & y_c01[h]; casefreq_test[2][1] += popcount(x);
					x = x_c02[h] & y_c02[h]; casefreq_test[2][2] += popcount(x);
				}

				for (h = 0; h < nlongintctrl; h++) {
					x = x_c10[h] & y_c10[h]; ctrlfreq_test[0][0] += popcount(x);
					x = x_c10[h] & y_c11[h]; ctrlfreq_test[0][1] += popcount(x);
					x = x_c10[h] & y_c12[h]; ctrlfreq_test[0][2] += popcount(x);
					x = x_c11[h] & y_c10[h]; ctrlfreq_test[1][0] += popcount(x);
					x = x_c11[h] & y_c11[h]; ctrlfreq_test[1][1] += popcount(x);
					x = x_c11[h] & y_c12[h]; ctrlfreq_test[1][2] += popcount(x);
					x = x_c12[h] & y_c10[h]; ctrlfreq_test[2][0] += popcount(x);
					x = x_c12[h] & y_c11[h]; ctrlfreq_test[2][1] += popcount(x);
					x = x_c12[h] & y_c12[h]; ctrlfreq_test[2][2] += popcount(x);
				}

				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						r_test[m][n] = double(casefreq_test[m][n]) / double(ctrlfreq_test[m][n]) * ((double)nctrl / (double)ncase);
						if (casefreq_test[m][n] == 0 & ctrlfreq_test[m][n] != 0) {
							r_test[m][n] = (double(casefreq_test[m][n]) + constant) / double(ctrlfreq_test[m][n]) * ((double)nctrl / (double)ncase);
							if (r_test[m][n] == mdr_threshold) {
								r_test[m][n] = r_test[m][n] + constant;
								flag_test[m][n] = 1;
							}
							else if (r_test[m][n] > mdr_threshold) flag_test[m][n] = 1;
							else flag_test[m][n] = 0;
						}
						else if (casefreq_test[m][n] != 0 & ctrlfreq_test[m][n] == 0) {
							r_test[m][n] = double(casefreq_test[m][n]) / (double(ctrlfreq_test[m][n]) + constant) * ((double)nctrl / (double)ncase);
							if (r_test[m][n] == mdr_threshold) {
								r_test[m][n] = r_test[m][n] + constant;
								flag_test[m][n] = 1;
							}
							else if (r_test[m][n] > mdr_threshold) flag_test[m][n] = 1;
							else flag_test[m][n] = 0;
						}
						else if (casefreq_test[m][n] == 0 & ctrlfreq_test[m][n] == 0) {
							flag_test[m][n] = -1;
						}
						else {
							r_test[m][n] = double(casefreq_test[m][n]) / double(ctrlfreq_test[m][n]) * ((double)nctrl / (double)ncase);
							if (r_test[m][n] == mdr_threshold) {
								r_test[m][n] = r_test[m][n] + constant;
								flag_test[m][n] = 1;
							}
							else if (r_test[m][n] > mdr_threshold) flag_test[m][n] = 1;
							else flag_test[m][n] = 0;
						}
					}
				}

				TP_w = FP_w = TN_w = FN_w = 0.0;

				for (m = 0; m < 3; m++) {
					for (n = 0; n < 3; n++) {
						if (flag_train[m][n] != -1 & flag_test[m][n] != -1) {
							if (flag_train[m][n] == 1 & flag_test[m][n] == 1) {

								if (casefreq_train[m][n] < casefreq_test[m][n]) {
									w[m][n] = pow(fabs(log10f(r_train[m][n])), alpha);
								}
								else {
									w[m][n] = pow(fabs(log10f(r_test[m][n])), alpha);
								}
								TP_w += w[m][n] * double(casefreq_train[m][n] + casefreq_test[m][n]);

								if (ctrlfreq_train[m][n] < ctrlfreq_test[m][n]) {
									w[m][n] = pow(fabs(log10f(r_train[m][n])), alpha);
								}
								else {
									w[m][n] = pow(fabs(log10f(r_test[m][n])), alpha);
								}
								FP_w += w[m][n] * double(ctrlfreq_train[m][n] + ctrlfreq_test[m][n]);
							}

							else if (flag_train[m][n] == 0 & flag_test[m][n] == 0) {
								if (casefreq_train[m][n] < casefreq_test[m][n]) {
									w[m][n] = pow(fabs(log10f(r_train[m][n])), alpha);
								}
								else {
									w[m][n] = pow(fabs(log10f(r_test[m][n])), alpha);
								}
								FN_w += w[m][n] * double(casefreq_train[m][n] + casefreq_test[m][n]);

								if (ctrlfreq_train[m][n] < ctrlfreq_test[m][n]) {
									w[m][n] = pow(fabs(log10f(r_train[m][n])), alpha);
								}
								else {
									w[m][n] = pow(fabs(log10f(r_test[m][n])), alpha);
								}
								TN_w += w[m][n] * double(ctrlfreq_train[m][n] + ctrlfreq_test[m][n]);
							}
						}
					}
				}

				if ((FN_w == 0.0) & (TP_w == 0.0) & (FP_w == 0.0) & (TN_w == 0.0)) {
					per[count_inter][k] = 0.5 * (1.0 + 1.0);
				}
				else if ((FN_w == 0.0) & (FP_w == 0.0) & (TN_w == 0.0) & (TP_w != 0.0)) {
					per[count_inter][k] = 0.5 * (0.0 + 1.0);
				}
				else if ((FN_w == 0.0) & (FP_w == 0.0) & (TP_w == 0.0) & (TN_w != 0.0)) {
					per[count_inter][k] = 0.5 * (0.0 + 1.0);
				}
				else if ((FN_w == 0.0) & (TP_w == 0.0) & (TN_w == 0.0) & (FP_w != 0.0)) {
					per[count_inter][k] = 0.5 * (1.0 + 1.0);
				}
				else if ((TN_w == 0.0) & (FP_w == 0.0) & (TP_w == 0.0) & (FN_w != 0.0)) {
					per[count_inter][k] = 0.5 * (1.0 + 1.0);
				}
				else if ((FN_w == 0.0) & (TP_w == 0.0) & (TN_w != 0.0) & (FP_w != 0.0)) {
					per[count_inter][k] = 0.5 * (1.0 + (FP_w / (FP_w + TN_w)));
				}
				else if ((FP_w == 0.0) & (TN_w == 0.0) & (FN_w != 0.0) & (TP_w != 0.0)) {
					per[count_inter][k] = 0.5 * ((FN_w / (TP_w + FN_w)) + 1.0);
				}
				else per[count_inter][k] = 0.5 * ((FN_w / (TP_w + FN_w)) + (FP_w / (FP_w + TN_w)));

				interactionInfos[count_inter][0] = snps[0];
				interactionInfos[count_inter][1] = snps[1];
			}
		}

		for (i = 0; i < nsample / fold * (fold - 1); i++)
			free(X_train[i]);
		free(X_train);

		for (i = 0; i < nsnp; i++) {
			free(genocase_train_c0[i]);
			free(genocase_train_c1[i]);
			free(genocase_train_c2[i]);
			free(genocase_train_c3[i]);
			free(genoctrl_train_c0[i]);
			free(genoctrl_train_c1[i]);
			free(genoctrl_train_c2[i]);
			free(genoctrl_train_c3[i]);
		}

		free(genocase_train_c0);
		free(genocase_train_c1);
		free(genocase_train_c2);
		free(genocase_train_c3);
		free(genoctrl_train_c0);
		free(genoctrl_train_c1);
		free(genoctrl_train_c2);
		free(genoctrl_train_c3);

		for (i = 0; i < nsample / fold; i++)
			free(X_test[i]);
		free(X_test);

		for (i = 0; i < nsnp; i++) {
			free(genocase_test_c0[i]);
			free(genocase_test_c1[i]);
			free(genocase_test_c2[i]);
			free(genocase_test_c3[i]);
			free(genoctrl_test_c0[i]);
			free(genoctrl_test_c1[i]);
			free(genoctrl_test_c2[i]);
			free(genoctrl_test_c3[i]);
		}
		free(genocase_test_c0);
		free(genocase_test_c1);
		free(genocase_test_c2);
		free(genocase_test_c3);
		free(genoctrl_test_c0);
		free(genoctrl_test_c1);
		free(genoctrl_test_c2);
		free(genoctrl_test_c3);

	}

	for (i = 0; i < count_inter + 1; i++) {
		sum = 0.0;
		for (k = 0; k < fold; k++) {
			sum += per[i][k];
		}
		interactionInfos[i][2] = sum / double(fold);
		interaction_sort[i] = i;
	}

	for (i = 0; i < count_inter; i++) {
		for (j = 0; j < count_inter - i; j++) {
			if (interactionInfos[j][2] > interactionInfos[j + 1][2]) {
				buf2 = interactionInfos[j][2];
				interactionInfos[j][2] = interactionInfos[j + 1][2];
				interactionInfos[j + 1][2] = buf2;

				buf0 = interactionInfos[j][0];
				interactionInfos[j][0] = interactionInfos[j + 1][0];
				interactionInfos[j + 1][0] = buf0;

				buf1 = interactionInfos[j][1];
				interactionInfos[j][1] = interactionInfos[j + 1][1];
				interactionInfos[j + 1][1] = buf1;
			}
		}
	}

	for (k = 0; k < topK; k++) {
		fprintf(pairwise_sig_result, "%d\t%d\t%f\n", int(interactionInfos[k][0]), int(interactionInfos[k][1]), interactionInfos[k][2]);
	}

	for (i = 0; i < o + 1; i++)
		free(per[i]);
	free(per);
}

/**
* 2021.04.08
* DADADIDADI
*/

// 建立标识文件夹的方法
static void makeDir(string folderPath) {
	if (0 != _access(folderPath.c_str(), 0))
	{
		// if this folder not exist, create a new one.
		_mkdir(folderPath.c_str());   // 返回 0 表示创建成功，-1 表示失败
	}
}


int main(int argc, char* argv[]) {
	int i, j, k;

	// 基本属性
	percent = 1.0;
	topT = 200;
	topK = 100;
	mdr_threshold = 1;
	fold = 5;
	constant = 0.5;
	alpha = 0.25;
	//*****************修改部分*************************
	// 路径属性
	const char* inputfile;
	const char* outputfile;

	const char* finishedPath;
	const char* finishedPath2;
	string stringFinishedPath;
	string stringFinishedPath2;

	if (argc > 1) {
		percent = strtod(argv[1], NULL);
		topT = atoi(argv[2]);
		topK = atoi(argv[3]);
		mdr_threshold = strtod(argv[4], NULL);
		fold = atoi(argv[5]);
		constant = strtod(argv[6], NULL);
		alpha = strtod(argv[7], NULL);

		inputfile = argv[8];
		outputfile = argv[9];
		finishedPath = argv[10];
		finishedPath2 = argv[11];
		stringFinishedPath = finishedPath;
		stringFinishedPath2 = finishedPath2;

	}
	else {
		inputfile = "G:/SNPalgorithm/DualWMDR/testInputData/test_data_2loci.txt";
		outputfile = "G:/SNPalgorithm/DualWMDR/testResData/resData.txt";
	}


	//******************修改部分结束*********************

	char result1name[10000] = "";
	char* filename = (char*)malloc(10000 * sizeof(char));

	K = 0;

	sprintf(result1name, outputfile);
	pairwise_sig_result = fopen(result1name, "w");
	fprintf(pairwise_sig_result, "SNP1\tSNP2\taverage prediction error rates\n");
	sprintf(filename, inputfile);

	for (i = 0; i < 65536; i++)
	{
		wordbits[i] = bitCount(i);
	}
	GetDataSize(filename, &nsample, &nsnp);
	GetCaseControlSize(filename, &ncase, &nctrl);

	X = (int**)malloc(nsample * sizeof(int*));
	for (i = 0; i < nsample; i++)
		X[i] = (int*)malloc((nsnp + 1) * sizeof(int));

	X_case = (int**)malloc(ncase * sizeof(int*));
	for (i = 0; i < ncase; i++)
		X_case[i] = (int*)malloc((nsnp + 1) * sizeof(int));

	X_ctrl = (int**)malloc(nctrl * sizeof(int*));
	for (i = 0; i < nctrl; i++)
		X_ctrl[i] = (int*)malloc((nsnp + 1) * sizeof(int));

	nlongintcase = (int)ceil(((double)ncase) / LengthLongType);
	nlongintctrl = (int)ceil(((double)nctrl) / LengthLongType);

	pMarginalDistrSNP = (int*)malloc(MarginalDistrSNP_Y_DimensionY * nsnp * sizeof(int));
	pMarginalDistrSNP_Y = (int*)malloc(MarginalDistrSNP_Y_DimensionY * MarginalDistrSNP_Y_DimensionX * nsnp * sizeof(int));

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

	Y_label = (int*)malloc((nsample + 1) * sizeof(int));

	pmi = (double**)malloc((nsnp + 1) * sizeof(double*));
	for (i = 0; i < nsnp + 1; i++)
		pmi[i] = (double*)malloc((nsnp + 1) * sizeof(double));

	printf("\n");
	readData(filename);
	CalculateMarginalDistr(pMarginalDistrSNP, pMarginalDistrSNP_Y);

	Similarity();
	cluster();

	for (nk = 0; nk < K; nk++) {
		Logistic logist;
		logist.logisticRegression();
	}

	// free
	free(Y_label);

	for (i = 0; i < nclust + 1; i++)
		free(cluster_candidate[i]);
	free(cluster_candidate);
	free(cluster_count);

	for (i = 0; i < nsnp + 1; i++)
		free(pmi[i]);
	free(pmi);

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

	mdr_init();

	interactionInfos = (double**)calloc(comb_num(candidate.size(), 3), sizeof(double));
	for (i = 0; i < comb_num(candidate.size(), 3); i++)
		interactionInfos[i] = (double*)calloc(4, sizeof(double));

	MDR_interactions();

	for (i = 0; i < comb_num(candidate.size(), 3); i++)
		free(interactionInfos[i]);
	free(interactionInfos);
	mdr_free();

	fclose(pairwise_sig_result);

	free(pMarginalDistrSNP);
	free(pMarginalDistrSNP_Y);

	vector<int>().swap(candidate);
	// end
	makeDir(stringFinishedPath);
	makeDir(stringFinishedPath2);
}
