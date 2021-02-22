#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>

using namespace std;

void CreatTxt(char* pathName, int argc , char* argv[], int a, double b)//创建txt文件
{
	ofstream fout(pathName);
	if (fout) { // 如果创建成功
		fout << argc << endl;
		for (int i = 0; i < argc; i++)
		{
			fout << argv[i] << endl;
		}
		fout << "int a: " << a << endl;
		fout << "double b: " << b << endl;
		fout.close();  // 执行完操作后关闭文件句柄
	}
}

//int main(int argc, char* argv[]) {
//	int a = 0;
//	double b = 0;
//	double c;
//	printf("有%d个参数\n", argc);
//	for (int c = 0; c < argc; ++c)
//	{
//		printf("%s\n", argv[c]);
//	}
//	if (argc > 1) {
//		a = atoi(argv[1]); // char类型转为int
//		cout << a << endl;
//	}
//
//	if (argc > 2) {
//		b = strtod(argv[2],NULL); // char类型转为double
//		cout << b << endl;
//	}
//	// 创建并写入文件测试
//	char path1[] = "G:\\SNPalgorithm\\HiSeeker\\ResultData\\2.txt"; // 你要创建文件的路径
//	char* path = path1;
//
//	CreatTxt(path, argc, argv, a , b);
//
//	// next:将参数读入exe程序
//}