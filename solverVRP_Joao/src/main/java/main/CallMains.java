 package main;

        import org.apache.commons.lang.StringUtils;
        import org.apache.maven.shared.utils.io.FileUtils;

        import java.io.*;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;
        import java.util.Random;

public class CallMains {

    /////////////////////////////////////EDIT HERE//////////////////////////////////////////////
    protected static double bestTotalMINError = 70.000;//%
    protected static double upperBondErrorPercentage = 10.0;//%
    ////////////////////////////////////////////////////////////////////////////////////////////

    protected static int REPETITION_C = 10;
    protected static int counter;
    protected static String[] files  = {"RC101", "RC102", "RC103", "RC104", "RC105", "RC106", "RC107", "RC108",
                                        "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208",
                                        "C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108", "C109",
                                        "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208"};

    protected static String[] bestSol = {"1619.8","1457.4","1258","1132.3","1513.7","1372.7","1207.8","1114.2",
                                         "1261.8","1092.3","923.7","783.5","1154","1051.1","962.9","776.1",
                                         "827.3","827.3","826.3","822.9","827.3","827.3","827.3","827.3","827.3",
                                         "589.1","589.1","588.7","588.1","586.4","586","585.8","585.8"};

    protected static String FILE_CHOICE;
    protected String INSTANCE_FILE = FILE_CHOICE+".txt";
    protected static List<String[]> allParameters;
    protected static String[] sol;
    protected static String codice;
    protected static double sumError;
    protected static double sumMeanError;
    protected static double sumTotalTime;
    protected static String toWrite;



    public static void main(String[] args) {

        while(true) {

            counter = 0;
            sumError = 0;
            sumMeanError = 0;
            sumTotalTime = 0;
            toWrite = new String();

            allParameters = new ArrayList<String[]>();
            allParameters.add(files);
            Random rand = new Random();

            /////////////////////////////////////EDIT HERE//////////////////////////////////////////////

            //Random Select  between (max & min) number of iterations
            int max = 1536, min = 512;
            int numberIterations = rand.nextInt((max - min) + 1) + min;
            String[] nIter = {"" + numberIterations + ""};

            allParameters.add(nIter);

            //randomRuinAndRecreate
            String[] randomRuinSelectors = {"selectBest", "selectRandomly"};
            String[] randomRuinAcceptors = {"acceptNewRemoveWorst",  //, "schrimpfAcceptance", "experimentalSchrimpfAcceptance",
                    "acceptNewRemoveFirst", "greedyAcceptance", "greedyAcceptance_minVehFirst"};

            String[] randomshare = {"" + Math.random() + ""};           //Between 0 and 1
            String[] randomRuinInsertions = {"bestInsertion", "regretInsertion"};
            String[] randomRuinProbability = {"" + Math.random() + ""}; //Between 0 and 1

            allParameters.add(new String[]{"" + randomRuinSelectors[rand.nextInt(randomRuinSelectors.length)] + ""});
            allParameters.add(new String[]{"" + randomRuinAcceptors[rand.nextInt(randomRuinAcceptors.length)] + ""});
            allParameters.add(randomshare);
            allParameters.add(new String[]{"" + randomRuinInsertions[rand.nextInt(randomRuinInsertions.length)] + ""});
            allParameters.add(randomRuinProbability);

            //radialRuinAndRecreates
            String[] radialRuinSelectors = randomRuinSelectors;
            String[] radialRuinAcceptors = randomRuinAcceptors;
            String[] radialshare = {"" + Math.random() + ""};           //Between 0 and 1
            String[] radialRuinInsertions = randomRuinInsertions;

            allParameters.add(new String[]{"" + radialRuinSelectors[rand.nextInt(radialRuinSelectors.length)] + ""});
            allParameters.add(new String[]{"" + radialRuinAcceptors[rand.nextInt(radialRuinAcceptors.length)] + ""});
            allParameters.add(radialshare);
            allParameters.add(new String[]{"" + radialRuinInsertions[rand.nextInt(radialRuinInsertions.length)] + ""});

            ////////////////////////////////////////////////////////////////////////////////////////////

            sol = new String[allParameters.size()];
            setParemetersAndRun(0, args);
            backup();
        }
    }

    public static void backup() {
        File theDir = new File("output/Backup");

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + "Backup");
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("DIR created");
            }
        }

        //Backup the files
        File theDirFile = new File("output/randomSolutions.csv");

        for(int i = 1; ;i++){
            File check = new File("output/Backup/randomSolutions"+i+".csv");
            if(check.exists() && !check.isDirectory()){

            }else{
                try {
                    FileUtils.copyFile(theDirFile, check);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                break;
            }
        }
    }

    public static void setParemetersAndRun(int pos, String[] args) {

        int n = sol.length;
        if(pos >= n){

            FILE_CHOICE = new String(files[counter].replaceAll(".txt",""));
            //System.out.println(FILE_CHOICE);
            //System.out.println(sol[0]);
            codice = "";
            for(int i = 1; i < n; i++){
                codice+=sol[i]+"|";
            }
            //System.out.println();
            //System.out.println(codice);
            AutoChangerXML.main(sol);
            Main.main(args);
            editcsv();
            counter++;

            return;
        }

        for(int i = 0; i < allParameters.get(pos).length ; i++){
            sol[pos] = allParameters.get(pos)[i];
            setParemetersAndRun(pos + 1, args);
        }

    }

    public static void editcsv() {
        try{
            File file = new File("output/randomSolutions.csv");

            if(file.exists()){

                //Get number of rows and indexes
                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);

                int linenumber = 0;

                while (lnr.readLine() != null){
                    linenumber++;
                }

                //System.out.println("Total number of lines : " + linenumber);
                int firstNewIndex = (linenumber - (REPETITION_C - 1));

                lnr.close();

                //Read data from specific indexes
                FileReader fr2 = new FileReader(file);
                LineNumberReader lnr2 = new LineNumberReader(fr2);

                int linenumber2 = 0;

                while (linenumber2 < (firstNewIndex - 1) && lnr2.readLine() != null){
                    linenumber2++;
                }

                List<String> displayList = new ArrayList<String>();

                if(counter == 0) {
                    displayList.add(codice);
                    displayList.add("0.01");
                }

                double sumCost = 0;

                    displayList.add("");
                    displayList.add(files[counter]);
                    displayList.add("Repetitions:");

                    double sumTime = 0;
                    double minCost = 0;
                    int minCostIndex = 1;
                    double minTime = 0;
                    int minTimeIndex = 1;
                    int localCounter = 0;

                    for (int i = 0; i < REPETITION_C; i++) {
                        //displayList.add("#" + (i + 1) + "");
                        String para = lnr2.readLine();
                        para = para.replaceAll(";", ",");
                        List<String> lineList = Arrays.asList(para.split(","));

                        displayList.add(lineList.get(1));
                        //displayList.add(lineList.get(2));
                        //displayList.add(lineList.get(3));

                        sumCost += Double.parseDouble(lineList.get(1));
                        sumTime += Double.parseDouble(lineList.get(2));

                        if (localCounter == 0) {
                            minCost = Double.parseDouble(lineList.get(1));
                            minTime = Double.parseDouble(lineList.get(2));

                        }

                        if (Double.parseDouble(lineList.get(1)) < minCost) {
                            minCost = Double.parseDouble(lineList.get(1));
                            minCostIndex = i + 1;
                        }

                        if (Double.parseDouble(lineList.get(2)) < minTime) {
                            minTime = Double.parseDouble(lineList.get(2));
                            minTimeIndex = i + 1;
                        }

                        localCounter++;
                    }

                    sumTotalTime += sumTime;

                    displayList.add("MEAN Cost:");
                    displayList.add(new DecimalFormat("#.000").format((sumCost / REPETITION_C)));
                    displayList.add("MIN Cost:");
                    displayList.add(new DecimalFormat("#.000").format(minCost));
                    //displayList.add("#" + minCostIndex + "");
                    displayList.add("MEAN Time:");
                    displayList.add(new DecimalFormat("#.000").format((sumTime / REPETITION_C)));
                    displayList.add("MIN Time:");
                    displayList.add(new DecimalFormat("#.000").format(minTime));
                    //displayList.add("#" + minTimeIndex + "");
                    displayList.add("MEAN Error:");
                    displayList.add(new DecimalFormat("#.000").format(100*((sumCost - (REPETITION_C*(Double.parseDouble(bestSol[counter])) )) / (REPETITION_C*Double.parseDouble(bestSol[counter])) )) + "%");
                    displayList.add("MIN Error:");
                    displayList.add(new DecimalFormat("#.000").format(100*((minCost - Double.parseDouble(bestSol[counter])) / Double.parseDouble(bestSol[counter]))) + "%");
                    displayList.add("");

                    sumError += (100*((minCost - Double.parseDouble(bestSol[counter])) / Double.parseDouble(bestSol[counter])));
                    sumMeanError += (100*((sumCost - (REPETITION_C*(Double.parseDouble(bestSol[counter])) )) / (REPETITION_C*Double.parseDouble(bestSol[counter])) ));

                if(100*(((minCost - Double.parseDouble(bestSol[counter])) / Double.parseDouble(bestSol[counter]))) > upperBondErrorPercentage){
                    System.out.println("Abort! Repetition with too big error...");
                    String[] args = new String[0];
                    CallMains.main(args);
                }else if(sumError > bestTotalMINError){
                    System.out.println("Abort! Total MIN Error Superior to what looking for...");
                    String[] args = new String[0];
                    CallMains.main(args);
                }

                if(counter == (files.length - 1)) {

                    List<String> lineList2 = Arrays.asList(toWrite.split(","));
                    lineList2.set(1,"");
                    toWrite = StringUtils.join(lineList2, ',');

                    displayList.add("Total Time:");
                    displayList.add(new DecimalFormat("#.000").format(sumTotalTime));
                    displayList.add("Total MEAN Error:");
                    displayList.add(new DecimalFormat("#.000").format(sumMeanError) + "%");
                    displayList.add("Total MIN Error:");
                    displayList.add(new DecimalFormat("#.000").format(sumError) + "%");
                }

                toWrite += StringUtils.join(displayList, ',');

                lnr2.close();

                //Write the repetitions in only one line

                    FileReader fr3 = new FileReader(file);
                    LineNumberReader lnr3 = new LineNumberReader(fr3);

                    BufferedWriter writer = getAppender("output/temp.csv");
                    try {
                            int linenumber3 = 0;
                            while (linenumber3 < (firstNewIndex - 1)) {
                                String para = lnr3.readLine();
                                List<String> lineList = Arrays.asList(para.split(","));

                                try {
                                    sumCost += Double.parseDouble(lineList.get(1));
                                    System.out.println(sumCost);
                                } catch (Exception e) {
                                    writer.write(para + '\n');
                                }

                                linenumber3++;
                            }
                            writer.write(toWrite + '\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    close(writer);

                    file.delete();
                    File fileTemp = new File("output/temp.csv");
                    File file2 = new File("output/randomSolutions.csv");
                    fileTemp.renameTo(file2);

            }else{
                System.out.println("File does not exists!");
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private static BufferedWriter getAppender(String file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return writer;
    }

    private static void close(BufferedWriter writer)  {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}