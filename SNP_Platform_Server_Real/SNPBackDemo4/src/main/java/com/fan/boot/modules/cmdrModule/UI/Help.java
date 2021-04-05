package com.fan.boot.modules.cmdrModule.UI;

public class Help {
    public Help() {
        String help = "\nThe DECMDR can be used from the command line using the following syntaxes:\n\n";
        help = help + "\tjava -jar DECMDR.jar [-s] [-p] [-g] [-m] [-r] [-o] [data_path]\n\n\n";
        help = help + "* data_path - the path to a file, a folder or a leading part of the path. E.g. D:\\dataset\n\n";
        help = help + "* -s - the seed (default = 1). E.g. -s 1\n\n";
        help = help + "* -p - the population size (default = 100). E.g. -p 100\n\n";
        help = help + "* -g - the max generation (default = 300). E.g. -g 300\n\n";
        help = help + "* -m - the mutation factor (default = 0.5). E.g. -m 0.5\n\n";
        help = help + "* -r - the recombination CR factor (default = 0.5). E.g. -r 0.5\n\n";
        help = help + "* -o - the order of SNP-SNP interaction (default = 2). E.g. -o 2";
        System.out.println(help);
    }
}
