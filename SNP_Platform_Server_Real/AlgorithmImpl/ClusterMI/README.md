typeOfSearch:	 0	// 0_for_exhuastive_search,_1_for_ACO_search

alpha:	                 1.00	// weight_given_to_pheromone_deposited_by_ants

sig_threshold:	   0.05	//the_significance_level

topK:	                 100	// number_of_the_most_significant_interactions

rou:	                 0.01	// evaporation_rate_in_Ant_Colony_Optimizaion

level/phe:	                500	// initial_pheromone_level_for_each_locus

nAntCount:	 1000	// number_of_ants

IterCount:	                 100          // number_of_iterations

kLociSet:                    2	// number_of_pairwise_SNPs_selected_by_an_ant_in_each_iteration

kEpiModel:                3	// number_of_SNPs_in_an_epistatic_interaction

kTopModel:              1000          // number_of_top_ranking_haplotypes_in_the_ACO_search_stage

kCluster                      3   // 默认为3



ClusterMIRun.exe 0 1 0.05 100 0.01 500 1000 100 2 3 1000 3 G:\SNPalgorithm\ClusterMI\inputData\data.txt 1110000





