#include <iostream>

#include <gsl/gsl_sf.h>

int main()

{

    std::cout << gsl_sf_gamma_inc(1.5, 0.5) << std::endl;

    std::cout << gsl_sf_gamma_inc_Q(1.5, 0.5) << std::endl;

    std::cout << gsl_sf_gamma_inc_P(1.5, 0.5) << std::endl;

    std::cin.get();

    return 0;

}