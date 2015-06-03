package jmetal.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.problems.Incremental2Objetives;
import jmetal.util.JMException;

public class Incremental_SPEA2_2obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        //Softwares
        String[] softwares = {
            "OA_AJHotDraw", 
            "OA_AJHsqldb", 
            "OA_HealthWatcher",
            "OA_TollSystems"
        };

        for (String filename : softwares) {

            int runsNumber = 30;
            int populationSize = 300;
            int maxEvaluations = 60000;
            int archiveSize = 250;
            double crossoverProbability = 0.95;
            double mutationProbability = 0.02; //0.2;
            String context = "_Inc_2obj";

            File directory = new File("resultado/spea2/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.exit(0);
                }
            }

            Incremental2Objetives problem = new Incremental2Objetives("problemas/" + filename + ".txt");
            Algorithm algorithm = new SPEA2(problem);
            SolutionSet todasRuns = new SolutionSet();
            Operator crossover;
            Operator mutation;
            Operator selection;

            // Crossover
            crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossoverIncremental");
            crossover.setParameter("probability", crossoverProbability);
            // Mutation
            mutation = MutationFactory.getMutationOperator("SwapMutationIncremental");
            mutation.setParameter("probability", mutationProbability);
            // Selection
            selection = SelectionFactory.getSelectionOperator("BinaryTournament");
            // Algorithm params
            algorithm.setInputParameter("populationSize", populationSize);
            algorithm.setInputParameter("maxEvaluations", maxEvaluations);
            algorithm.setInputParameter("archiveSize", archiveSize);
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);

            System.out.println("\n================ SPEA2 ================");
            System.out.println("Software: " + filename);
            System.out.println("Context: " + context);
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tMaxEva -> "+maxEvaluations);
            System.out.println("\tCross -> "+crossoverProbability);
            System.out.println("\tMuta -> "+mutationProbability);
            System.out.println("Number of elements: " + problem.numberOfElements_);
            System.out.println("Number of Aspects: " + problem.getAspects().size());
            System.out.println("Aspects: " + problem.getAspects().toString());

            long heapSize = Runtime.getRuntime().totalMemory();
            heapSize = (heapSize / 1024) / 1024;
            System.out.println("Heap Size: " + heapSize + "Mb\n");

            for (int runs = 0; runs < runsNumber; runs++) {
                // Execute the Algorithm
                long initTime = System.currentTimeMillis();
                SolutionSet resultFront = algorithm.execute();
                long estimatedTime = System.currentTimeMillis() - initTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);

                resultFront = problem.removeDominadas(resultFront);
                resultFront = problem.removeRepetidas(resultFront);
                resultFront.printObjectivesToFile("resultado/spea2/" + filename + context + "/FUN_spea2" + "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/spea2/" + filename + context + "/VAR_spea2" + "-" + filename + "-" + runs + ".NaoDominadas");

                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
            }

            todasRuns = problem.removeDominadas(todasRuns);
            todasRuns = problem.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/spea2/" + filename + context + "/All_FUN_spea2" + "-" + filename);
            todasRuns.printVariablesToFile("resultado/spea2/" + filename + context + "/All_VAR_spea2" + "-" + filename);

            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_spea2");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}