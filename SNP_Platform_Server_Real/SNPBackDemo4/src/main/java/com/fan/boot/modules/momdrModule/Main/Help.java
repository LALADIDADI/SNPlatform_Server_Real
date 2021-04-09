package com.fan.boot.modules.momdrModule.Main;

public class Help
{
	public Help()
	{
		String help = "\nThe MOMDR can be used from the command line using the following syntaxes:\n\n";
		help += "\tjava -jar MOMDR.jar [-s] [-o] [-c] [-help] [data_path]\n\n\n";
		help += "* -s - the seed (default = 1). E.g. -s 1\n\n";
		help += "* -o - the order of SNP-SNP interaction (default = 2). E.g. -o 2\n\n";
		help += "* -c - the number of fold CV (default = 5). E.g. -o 5\n\n";
		help += "* data_path - the path to a file, a folder or a leading part of the path. E.g. D:\\dataset";
		System.out.println( help );
	}
}
