//This is the source code of MACOED.
#include <iostream>
#include <Eigen/Dense>
#include <fstream>
#include <string>
#include <sstream>
#include <cmath>
#include <ctime>
#include <algorithm>
#include <iomanip>
#include <gsl/gsl_cdf.h>
#include <corecrt_io.h>
#include <direct.h>


using namespace std;
using namespace Eigen;
using namespace Eigen::internal;
using namespace Eigen::Architecture;

//reading data****************************
clock_t time_start, time_finish;
double totalTime;
double tau = 0;
double threshold = 0;
double rou = 0;
double lambda = 0;
int num_ant = 0;
int max_iter = 0;
int dim_epi = 2;
double alpha = 0;

class SNP
{
public:
	int samplesize;
	int locisize;
	int data_col;
	int** data;
	char** SNPnames;
	int classvalues[2];
	void input_data(const char* path);
	void setpheromone(double level);
	double** pheromone;
	void getcdf(int sel_loci);
	double* cdf;
	void display_pheromone();
	void display_cdf();
	void destroy();
};
void SNP::destroy()
{
	int i;
	for (i = 0; i < locisize; i++)
		delete[]SNPnames[i];
	delete[]SNPnames;
	for (i = 0; i < samplesize; i++)
		delete[]data[i];
	delete[]data;
	for (i = 0; i < locisize; i++)
		delete[]pheromone[i];
	delete[]pheromone;
}

void SNP::input_data(const char* path)
{
	cout << "-------------Reading data-------------" << endl;
	int i, j, temp;
	string line;
	ifstream in(path);
	getline(in, line);
	istringstream test(line);
	i = 0;
	string word;
	while (!test.eof())
	{
		getline(test, word, ',');
		i++;
	}
	data_col = i;
	locisize = i - 1;
	j = 0;
	while (!in.eof())
	{
		getline(in, line);
		j++;
	}
	samplesize = j - 1;
	in.close();
	SNPnames = new char* [data_col];
	for (i = 0; i < data_col; i++)
		SNPnames[i] = new char[20];
	data = new int* [samplesize];
	for (i = 0; i < samplesize; i++)
		data[i] = new int[data_col];
	classvalues[0] = 0;
	classvalues[1] = 0;
	ifstream in1(path);
	getline(in1, line);
	istringstream test1(line);
	i = 0;
	while (!test1.eof())
	{
		if (i == data_col)
			break;
		getline(test1, word, ',');
		strcpy(SNPnames[i], word.c_str());
		i++;
	}
	i = 0;
	while (!in1.eof())
	{
		if (i == samplesize)
		{
			break;
		}
		getline(in1, line);
		istringstream values(line);
		j = 0;
		while (!values.eof())
		{
			getline(values, word, ',');
			istringstream int_iss(word);
			int_iss >> temp;
			data[i][j++] = temp;
			if (j == data_col - 1)
			{
				classvalues[temp]++;
			}

		}
		i++;
	}
	in1.close();
	cout << endl << "-------Reading data completed!--------" << endl;
	cout << "Number of loci: " << locisize << endl;
	cout << "Number of samples: " << samplesize << endl;
}
void SNP::setpheromone(double level)
{
	int i, j;
	pheromone = new double* [locisize];
	for (i = 0; i < locisize; i++) {
		pheromone[i] = new double[locisize];
		for (j = 0; j < locisize; j++)
			pheromone[i][j] = level;
	}
}
void SNP::getcdf(int sel_loci) {
	int i;
	double temp = 0;
	cdf = new double[locisize];
	for (i = 0; i < locisize; i++) {
		temp = temp + pheromone[sel_loci][i];
		cdf[i] = temp;
	}
	for (i = 0; i < locisize; i++) {
		cdf[i] = cdf[i] / temp;
	}
}
void SNP::display_cdf() {
	int i;
	for (i = 0; i < locisize; i++)
		cout << cdf[i] << " ";
	cout << endl;
}
void SNP::display_pheromone() {
	int i, j;
	for (i = 0; i < locisize; i++)
		cout << " " << SNPnames[i];
	cout << endl;
	for (i = 0; i < locisize; i++) {
		cout << SNPnames[i] << " ";
		for (j = 0; j < locisize; j++)
			cout << pheromone[i][j] << " ";
		cout << endl;
	}
}
SNP SNPdata;

// Objevtive function
//**************************************************************************
// K2 score
//**************************************************************************
double My_factorial(double n)
{
	double i;
	double z = 0;
	if (n < 0)
	{
		cout << "Illegal n, n should be a non-negative number." << endl;
		return 0;
	}
	if (n == 0)
		return 0;
	for (i = 1.0; i < (n + 1.0); i++)
		z += log(i);
	return z;
}

double Bayesian_score(int* selectedSNPSet, int k) {
	int comb = (int)pow(3.0, k);
	double** observedValues;
	double* colSumTable;
	int i, j, index;
	double score = 0;
	observedValues = new double* [2];
	for (i = 0; i < 2; i++)
		observedValues[i] = new double[comb];
	colSumTable = new double[comb];
	for (i = 0; i < comb; i++)
	{
		observedValues[0][i] = 0;
		observedValues[1][i] = 0;
		colSumTable[i] = 0;
	}
	/*constructing observed freq table*/
	bool cont;
	for (i = 0; i < SNPdata.samplesize; i++)
	{
		index = 0;
		cont = 1;
		for (j = 0; j < k; j++) {
			if (SNPdata.data[i][selectedSNPSet[j]] == 3)
			{
				cont = 0;
				break;
			}
			else index = index + SNPdata.data[i][selectedSNPSet[j]] * (int)pow(3.0, (k - 1 - j));
		}
		if (cont) {
			observedValues[SNPdata.data[i][SNPdata.data_col - 1]][index]++;
			colSumTable[index]++;
		}
	}
	for (i = 0; i < comb; i++)
		score = score + My_factorial(observedValues[0][i]) + My_factorial(observedValues[1][i]) - My_factorial(colSumTable[i] + 1);
	score = fabs(score);
	for (i = 0; i < 2; i++)
		delete[] observedValues[i];
	delete[] observedValues;
	delete[] colSumTable;
	return score;
}


//**************************************************************************
// AIC score
//**************************************************************************
double logistic_score(int* selectedSNPSet, int k) {
	double delta = 0.001;
	int maxiter = 30;
	double aic = 0;
	double lossold, lossnew, loss;
	int i, j, s, theta_size, iter;
	int testsample = SNPdata.samplesize;
	theta_size = k + 2;
	int** newdata;
	double* ypre, * theta, * pi, * w, * wz, * xwz, * pre;
	MatrixXd xtwx(theta_size, theta_size);
	// allocation
	newdata = new int* [testsample];
	for (i = 0; i < testsample; i++)
		newdata[i] = new int[theta_size];

	theta = new double[theta_size];
	ypre = new double[testsample];
	pi = new double[testsample];
	w = new double[testsample];
	wz = new double[testsample];
	xwz = new double[theta_size];
	pre = new double[testsample];
	// initialization
	for (i = 0; i < testsample; i++) {
		newdata[i][0] = 1;
		newdata[i][1] = SNPdata.data[i][selectedSNPSet[0]];
		newdata[i][2] = SNPdata.data[i][selectedSNPSet[1]];
		newdata[i][3] = newdata[i][1] * newdata[i][2];
	}

	for (i = 0; i < theta_size; i++)
		theta[i] = 0;
	// iteration
	iter = 0;
	loss = 1;
	lossnew = 0;
	while (loss > delta && iter < maxiter) {
		lossold = lossnew;
		for (s = 0; s < testsample; s++) {
			ypre[s] = 0;
			for (j = 0; j < theta_size; j++) {
				ypre[s] += newdata[s][j] * theta[j];
			}
			pi[s] = exp(ypre[s]) / (1 + exp(ypre[s]));
			w[s] = pi[s] * (1 - pi[s]);
			wz[s] = w[s] * ypre[s] + SNPdata.data[s][SNPdata.data_col - 1] - pi[s];
		}
		xtwx.setZero();
		for (i = 0; i < theta_size; i++) {
			for (j = 0; j < theta_size; j++) {
				for (s = 0; s < testsample; s++) {
					xtwx(i, j) += w[s] * newdata[s][i] * newdata[s][j];
				}
			}
		}
		xtwx = xtwx.inverse();

		for (i = 0; i < theta_size; i++) {
			xwz[i] = 0;
			for (s = 0; s < testsample; s++) {
				xwz[i] += newdata[s][i] * wz[s];
			}
		}
		for (i = 0; i < theta_size; i++) {
			theta[i] = 0;
			for (j = 0; j < theta_size; j++) {
				theta[i] += xtwx(i, j) * xwz[j];
			}
		}
		lossnew = 0;
		for (i = 0; i < theta_size; i++)
			lossnew += abs(theta[i]);
		loss = abs(lossnew - lossold);
		iter++;
	}
	for (s = 0; s < testsample; s++) {
		pre[s] = abs(1 - SNPdata.data[s][SNPdata.data_col - 1] - pi[s]);
		aic += -2 * log(pre[s]);
	}
	aic = aic + 2 * theta_size;

	delete[]ypre;
	delete[]theta;
	delete[]pi;
	delete[]w;
	delete[]wz;
	delete[]xwz;
	delete[]pre;
	for (i = 0; i < testsample; i++)
		delete[]newdata[i];
	delete[]newdata;
	return aic;
}


//**************************************************************************
// chi_square score
//**************************************************************************
double chi_square(int* selectedSNPSet, int k)
{
	int comb = (int)pow(3.0, k);
	double** observedValues;
	double* colSumTable;
	double** expectedValues;
	int i, j, index;
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
	/*constructing observed freq table*/
	bool cont;
	for (i = 0; i < SNPdata.samplesize; i++)
	{
		index = 0;
		cont = 1;
		for (j = 0; j < k; j++) {
			if (SNPdata.data[i][selectedSNPSet[j]] == 3)
			{
				cont = 0;
				break;
			}
			else index = index + SNPdata.data[i][selectedSNPSet[j]] * (int)pow(3.0, (k - 1 - j));
		}
		if (cont) {
			observedValues[SNPdata.data[i][SNPdata.data_col - 1]][index]++;
			colSumTable[index]++;
		}
	}
	double x2 = 0;
	for (i = 0; i < comb; i++) {
		expectedValues[0][i] = colSumTable[i] * SNPdata.classvalues[0] / (double)SNPdata.samplesize;
		expectedValues[1][i] = colSumTable[i] * SNPdata.classvalues[1] / (double)SNPdata.samplesize;
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
	return x2;
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

int cdf2locus(double x, int start, int end)
{
	if (start + 1 == end)
	{
		return start;
	}
	else
	{
		int temp = (start + end) / 2;
		if (SNPdata.cdf[temp] <= x)
		{
			return cdf2locus(x, temp, end);
		}
		else
		{
			return cdf2locus(x, start, temp);
		}
	}
}

class ant {
public:
	int* tabu;
	double* score;
	void initiate();
	void antseek(int loci_start);
	void destory();
};
ant* ants;
void ant::initiate() {
	tabu = new int[dim_epi];
	score = new double[2];
}

void ant::destory() {
	delete[]tabu;
	delete[]score;
}

void ant::antseek(int loci_start) {
	int seek_count = 1;
	int i, j, temp, jj, select, key;
	double mRate, roulette;
	temp = loci_start;
	tabu[0] = loci_start;
	for (i = 1; i < dim_epi; i++) {
		jj = -1;
		SNPdata.getcdf(temp);
		while (jj == -1) {
			jj = 0;
			srand((unsigned)time(NULL) + rand());
			mRate = rnd(0, 1);
			roulette = rnd(0, 1);
			if (mRate >= threshold) {
				select = int(roulette * SNPdata.locisize);
			}
			else {
				select = cdf2locus(mRate, 0, SNPdata.locisize);
			}
			for (j = 0; j < i; j++) {
				if (select == tabu[j]) {
					jj = -1;
					break;
				}
			}
		}
		temp = select;
		tabu[i] = temp;
		delete[]SNPdata.cdf;
	}
	if (tabu[0] > tabu[1]) {
		key = tabu[0];
		tabu[0] = tabu[1];
		tabu[1] = key;
	}
}

int* start() {
	int* a, i;
	a = new int[SNPdata.locisize];
	srand((unsigned)time(NULL) + rand());
	for (i = 0; i < SNPdata.locisize; ++i) a[i] = i;
	for (i = SNPdata.locisize - 1; i > 0; --i)
		swap(a[i], a[rand() % i]);
	return a;
}
// main part
//*****************************************************************
void updata_pheromone(double** phe);
long int comb_num(int m, int n);
int* non_dominated(double** multiscore, int number, int& k);

//*****************************************************************

/*
* By: DADADIDADI
*2021.04.06
*/

// 建立标识文件夹的方法
static void makeDir(string folderPath) {
	if (0 != _access(folderPath.c_str(), 0))
	{
		// if this folder not exist, create a new one.
		_mkdir(folderPath.c_str());   // 返回 0 表示创建成功，-1 表示失败
	}
}

void main(int argc, char* argv[]) {
	
	//******************分割线***********************
	// 基本属性
	max_iter = 50;
	num_ant = 100;
	dim_epi = 2;
	alpha = 0.1;
	lambda = 2;
	threshold = 0.8;
	tau = 1;
	rou = 0.9;

	// 路径属性
	const char* inputfile;
	const char* outputfile;

	const char* finishedPath;
	const char* finishedPath2;
	string stringFinishedPath;
	string stringFinishedPath2;
	// todo: 加入finished方法，作为标识



	if (argc > 1) {
		max_iter = atoi(argv[1]);
		num_ant = atoi(argv[2]);
		dim_epi = atoi(argv[3]);
		alpha = strtod(argv[4], NULL);
		lambda = atoi(argv[5]);
		threshold = strtod(argv[6], NULL);
		tau = atoi(argv[7]);
		rou = strtod(argv[8], NULL);

		inputfile = argv[9];
		outputfile = argv[10];
		finishedPath = argv[11];
		finishedPath2 = argv[12];
		stringFinishedPath = finishedPath;
		stringFinishedPath2 = finishedPath2;

	}
	else {
		inputfile = "G:/SNPalgorithm/MACOED/testInputData/test.txt";
		outputfile = "G:/SNPalgorithm/MACOED/testResData/resData.txt";
	}

	//*****************以上**************************

	time_start = clock();
	const char* filename = "parameters.txt";
	cout << "--------Initializing parameters--------" << endl;
	cout << "Max iterations: " << max_iter << endl;
	cout << "Number of ants: " << num_ant << endl;
	cout << "Dimension of epistatic iteraction: " << dim_epi << endl;
	cout << "Input file path: " << inputfile << endl;
	cout << "Output file path: " << outputfile << endl;
	cout << "P-value after Bonferroni correction: " << alpha << endl;
	cout << "Lambda: " << lambda << endl;
	cout << "Threshold: " << threshold << endl;
	cout << "Tau: " << tau << endl;
	cout << "Rou: " << rou << endl;
	if (dim_epi != 2) {
		cout << endl << "Sorry...this version is only for 2-loci epistasis" << endl;
		return;
	}
	SNPdata.input_data(inputfile);
	SNPdata.setpheromone(tau);

	// declaration
	int* initial, ** s, ** l_s, * non_dom_num, * loci, * loci_temp, ** chi;
	long int number, number1;
	double** multiscore, ** l_multiscore, *** flag, x2, pvalue, stdpvalue;
	int i, j, k, iter, score_num, local_num, key, loci_num, r;
	//allocation
	flag = new double** [SNPdata.locisize];
	for (i = 0; i < SNPdata.locisize; i++)
		flag[i] = new double* [SNPdata.locisize];
	for (i = 0; i < SNPdata.locisize; i++) {
		for (j = 0; j < SNPdata.locisize; j++) {
			flag[i][j] = new double[3];
			for (k = 0; k < 3; k++)
				flag[i][j][k] = 0;
		}
	}
	multiscore = new double* [2 * num_ant];
	s = new int* [2 * num_ant];
	for (i = 0; i < 2 * num_ant; i++) {
		multiscore[i] = new double[2];
		s[i] = new int[dim_epi];
	}
	l_multiscore = new double* [num_ant];
	l_s = new int* [num_ant];
	for (i = 0; i < num_ant; i++) {
		l_multiscore[i] = new double[2];
		l_s[i] = new int[dim_epi];
	}
	loci = new int[SNPdata.locisize];
	//initialization
	number = comb_num(SNPdata.locisize, dim_epi);
	stdpvalue = alpha / number;
	iter = 0;
	local_num = 0;
	//iteration
	for (iter = 0; iter < max_iter; iter++) {
		cout << "The iteration: " << iter << endl;
		ants = new ant[num_ant];
		initial = start();
		for (i = 0; i < num_ant; i++) {
			ants[i].initiate();
			ants[i].antseek(initial[i % SNPdata.locisize]);
			if (flag[ants[i].tabu[0]][ants[i].tabu[1]][0] == 1) {
				ants[i].score[0] = flag[ants[i].tabu[0]][ants[i].tabu[1]][1];
				ants[i].score[1] = flag[ants[i].tabu[0]][ants[i].tabu[1]][2];
			}
			else {
				ants[i].score[0] = Bayesian_score(ants[i].tabu, dim_epi);
				ants[i].score[1] = logistic_score(ants[i].tabu, dim_epi);
				flag[ants[i].tabu[0]][ants[i].tabu[1]][0] = 1;
				flag[ants[i].tabu[0]][ants[i].tabu[1]][1] = ants[i].score[0];
				flag[ants[i].tabu[0]][ants[i].tabu[1]][2] = ants[i].score[1];
			}
			for (j = 0; j < dim_epi; j++)
				s[i][j] = ants[i].tabu[j];
			multiscore[i][0] = ants[i].score[0];
			multiscore[i][1] = ants[i].score[1];
		}

		// insert the non-donminated solutions in last iteration
		if (iter > 0) {
			for (k = 0; k < local_num; k++) {
				for (j = 0; j < dim_epi; j++)
					s[i + k][j] = l_s[k][j];
				multiscore[i + k][0] = l_multiscore[k][0];
				multiscore[i + k][1] = l_multiscore[k][1];
			}
		}
		// non-donminated sort
		score_num = local_num + num_ant;
		local_num = 0;
		non_dom_num = new int[score_num];
		non_dom_num = non_dominated(multiscore, score_num, local_num);
		// save the non-donminated solutions
		for (i = 0; i < local_num; i++) {
			l_multiscore[i][0] = multiscore[non_dom_num[i]][0];
			l_multiscore[i][1] = multiscore[non_dom_num[i]][1];
			for (j = 0; j < dim_epi; j++) {
				l_s[i][j] = s[non_dom_num[i]][j];
			}
		}
		delete[]non_dom_num;
		loci_temp = new int[dim_epi * local_num];
		i = 0;
		k = 0;
		while (k < dim_epi * local_num) {
			for (j = 0; j < dim_epi; j++) {
				loci_temp[k] = l_s[i][j];
				k++;
			}
			i++;
		}
		for (i = 1; i < dim_epi * local_num; i++) {
			key = loci_temp[i];
			j = i - 1;
			while (j >= 0 && loci_temp[j] > key) {
				loci_temp[j + 1] = loci_temp[j];
				j--;
			}
			loci_temp[j + 1] = key;
		}
		//unique
		k = 0;
		loci_num = 0;
		for (i = 0; i < dim_epi * local_num; i++) {
			loci[i] = loci_temp[k++];
			loci_num++;
			while (loci_temp[k] == loci_temp[k - 1])
				k++;
			if (k >= dim_epi * local_num)
				break;
		}
		delete[]loci_temp;
		//pheromone iteration
		for (k = 0; k < dim_epi; k++) {
			for (r = 0; r < dim_epi; r++) {
				if (k != r) {
					for (i = 0; i < score_num; i++) {
						SNPdata.pheromone[s[i][k]][s[i][r]] = rou * SNPdata.pheromone[s[i][k]][s[i][r]];
					}
					for (i = 0; i < local_num; i++) {
						SNPdata.pheromone[l_s[i][k]][l_s[i][r]] = SNPdata.pheromone[l_s[i][k]][l_s[i][r]] +
							(1 - rou) * lambda * SNPdata.pheromone[l_s[i][k]][l_s[i][r]];
					}


				}
			}
		}

		delete[]ants;
		delete[]initial;

	}
	//write intermediate resutls
	ofstream result;
	result.open(outputfile, ios::out);
	result << "The nondominated results:" << endl;
	result << left << setw(10) << "Loci 1" << left << setw(10) << "Loci 2" << left << setw(20) << "K2 score" << left << setw(20) << "AIC score" << endl;
	for (i = 0; i < local_num; i++) {
		result << left << setw(10) << SNPdata.SNPnames[l_s[i][0]] << left << setw(10) << SNPdata.SNPnames[l_s[i][1]];
		result << left << setw(20) << l_multiscore[i][0] << left << setw(20) << l_multiscore[i][1] << endl;
	}
	//Post processing and write final results
	cout << "Post processing..." << endl;
	result << endl << "The final results:" << endl;
	result << left << setw(10) << "Loci 1" << left << setw(10) << "Loci 2" << left << setw(20) << "Chi-square score" << left << setw(20) << "Pvalue" << endl;
	number1 = comb_num(loci_num, dim_epi);
	chi = new int* [number1];
	for (i = 0; i < number1; i++)
		chi[i] = new int[dim_epi];
	k = 0;
	for (i = 0; i < loci_num - 1; i++) {
		for (j = i + 1; j < loci_num; j++) {
			chi[k][0] = loci[i];
			chi[k][1] = loci[j];
			x2 = chi_square(chi[k], dim_epi);
			pvalue = 1 - gsl_cdf_chisq_P(x2, pow(3.0, dim_epi) - 1);
			if (pvalue < stdpvalue) {
				result << left << setw(10) << SNPdata.SNPnames[loci[i]] << left << setw(10) << SNPdata.SNPnames[loci[j]];
				result << left << setw(20) << x2 << left << setw(20) << pvalue << endl;
			}
			k++;
		}
	}

	for (i = 0; i < 2 * num_ant; i++) {
		delete[]multiscore[i];
		delete[]s[i];
	}
	delete[]multiscore;
	delete[]s;


	for (i = 0; i < num_ant; i++) {
		delete[]l_multiscore[i];
		delete[]l_s[i];
	}
	delete[]l_multiscore;
	delete[]l_s;


	for (i = 0; i < SNPdata.locisize; i++)
		for (j = 0; j < SNPdata.locisize; j++)
			delete[]flag[i][j];
	for (i = 0; i < SNPdata.locisize; i++)
		delete[]flag[i];
	delete[]flag;

	for (k = 0; k < number1; k++)
		delete[]chi[k];
	delete[]chi;

	delete[]loci;

	SNPdata.destroy();

	time_finish = clock();
	totalTime = (double)(time_finish - time_start) / CLOCKS_PER_SEC;
	cout << "The total time: " << totalTime << endl;
	makeDir(stringFinishedPath);
	makeDir(stringFinishedPath2);
}

//non-dominated sort
int* non_dominated(double** multiscore, int number, int& k) {
	int flag, i, j, * F;
	F = new int[number];
	for (i = number - 1; i >= 0; i--) {
		flag = 0;
		for (j = number - 1; j >= 0; j--) {
			if (((multiscore[j][0] < multiscore[i][0]) && (multiscore[j][1] < multiscore[i][1])) ||
				((multiscore[j][0] == multiscore[i][0]) && (multiscore[j][1] < multiscore[i][1])) ||
				((multiscore[j][0] < multiscore[i][0]) && (multiscore[j][1] == multiscore[i][1])))
			{
				flag = 1;
				break;
			}
			if ((multiscore[j][0] == multiscore[i][0]) && (multiscore[j][1] == multiscore[i][1]) && (i < j)) {
				flag = 1;
				break;
			}
		}
		if (flag == 0) {
			F[k] = i;
			k++;
		}
	}
	return F;
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


