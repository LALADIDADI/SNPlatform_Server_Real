#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>

using namespace std;

void CreatTxt(char* pathName, int argc , char* argv[], int a, double b)//����txt�ļ�
{
	ofstream fout(pathName);
	if (fout) { // ��������ɹ�
		fout << argc << endl;
		for (int i = 0; i < argc; i++)
		{
			fout << argv[i] << endl;
		}
		fout << "int a: " << a << endl;
		fout << "double b: " << b << endl;
		fout.close();  // ִ���������ر��ļ����
	}
}

//int main(int argc, char* argv[]) {
//	int a = 0;
//	double b = 0;
//	double c;
//	printf("��%d������\n", argc);
//	for (int c = 0; c < argc; ++c)
//	{
//		printf("%s\n", argv[c]);
//	}
//	if (argc > 1) {
//		a = atoi(argv[1]); // char����תΪint
//		cout << a << endl;
//	}
//
//	if (argc > 2) {
//		b = strtod(argv[2],NULL); // char����תΪdouble
//		cout << b << endl;
//	}
//	// ������д���ļ�����
//	char path1[] = "G:\\SNPalgorithm\\HiSeeker\\ResultData\\2.txt"; // ��Ҫ�����ļ���·��
//	char* path = path1;
//
//	CreatTxt(path, argc, argv, a , b);
//
//	// next:����������exe����
//}