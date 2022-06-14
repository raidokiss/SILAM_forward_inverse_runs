import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeneratorFwd { //main runnable class that is used to generate a standard .control file and point source file (.v5)
//for FORWARD run
    public static List<PointSource> readInFWDPointSources(String filename) throws IOException {//reading in all point source data from file
        //(example files: Ida-Virumaa allikad [yyyy].txt) for FORWARD run
        List<PointSource> allSources = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            br.readLine(); //ignoring header
            String rida = br.readLine(); //reads in first source
            while (rida != null && !rida.equals("")) {//reads all other sources and adds to list
                allSources.add(new PointSource(rida));
                rida = br.readLine();
            }
        }
        return allSources;
    }

    public static void fastRun(String start, String end, String date, String pollutant) throws Exception { //method for generating similar runs fast
        //making a new run
        SILAMRun run = new SILAMRun("FORWARD", "forward_" + date + "_" + pollutant, //pollutant is S (SO2) or N (NOx)
                "forward_src_point03_" + date + "_" + pollutant, start, end);

        run.setLayerThickness("20. 20. 20. 20. 20. 20. 80. 200. 400. 1200. 2000."); //vertical layer thickness,
        //layer midpoints are here at 10, 30, 50, 70, 90, 110, 160, 300, 600, 1400, 3000 (m)
        run.generateCtrlFile(); //standard, has all parameters for generation

        List<PointSource> sources = readInFWDPointSources("Ida-Virumaa allikad 20"
                + date.substring(date.length() - 2) + "_" + pollutant + ".txt"); //reading sources from file

        for (PointSource fwdSource : sources) {//generating source emission variation in time for each source (par_str_point rows)
            fwdSource.generateParStrPoint("", start, end, "FORWARD");//filename should be empty string for fwd run
            //if pointsource is not constant in time, INVERSE run code (from PointSource class) should be modified and used here instead
            fwdSource.generateFwdTimeVarIndices(); //generating time variation for all sources based on source type
            //(A_PublicPower varies, B_Industry is constant in time)
        }
        run.generatePointSourceFile(sources); //generating point source file
    }

    public static void main(String[] args) throws Exception {//firstly follows forward run template, down below is code that ran
        /*
        //FORWARD RUN TEMPLATE if fastrun is not used

        SILAMRun forwardRun = new SILAMRun("FORWARD", "forward_[dd_MM_yy]", "forward_src_point03_[dd_MM_yy]", "[yyyy MM dd HH mm]", "[yyyy MM dd HH mm]");
        //ctrlFileName and pointSourceFileName WITHOUT extension (extensions .control and .v5 are added)
        //FORWARD => startTime is BEFORE endTime, max time accuracy is one minute, SILAM time format is yyyy MM dd HH mm
        //all SILAM time is UTC!!!

        forwardRun.setLayerThickness("25. 25. 25. 25. 50. 100. 400. 750. 1200. 2000."); //was different for INV and FWD runs
        forwardRun.generateCtrlFile();

        List<PointSource> fwdSources = readInFWDPointSources("forward_test.txt");
        //filename is modified .csv output from https://klabgis.klab.ee/eerc/.
        //examples are files "Ida-Virumaa allikad [yyyy].txt"
        //modifications: coordinates from L-Est system to lat-lon via https://gpa.maaamet.ee/calc/geo-lest/;
        //no funky characters in names; pollutant made SILAM readable (SO2 or NOX); added a column named "type" to
        //specify source type (necessary to make time variations)

        //58. ... is 7 numbers long in L-Est = latitude, 23. ... is 6 numbers long in L-Est = longitude

        for (PointSource fwdSource : fwdSources) {
            fwdSource.generateParStrPoint("", "2017 10 11 12 00", "2017 10 13 03 00", "FORWARD");
            //filename is empty string for FORWARD runs.
            //startTime before endTime, max time accuracy is one minute, SILAMi time format: yyyy MM dd HH mm

            fwdSource.generateFwdTimeVarIndices();
        }

        forwardRun.generatePointSourceFile(fwdSources);*/

        //implementing some kind of readcsv would be better of course

        //input files generation for all validation episodes
        fastRun("2016 10 11 12 00", "2016 10 14 00 00", "13_10_16", "N");
        fastRun("2017 05 15 18 00", "2017 05 17 16 00", "16_05_17", "N");
        fastRun("2017 07 23 20 00", "2017 07 25 12 00", "24_07_17", "N");
        fastRun("2017 08 26 18 00", "2017 08 28 16 00", "27_08_17", "N");
        fastRun("2018 03 03 18 00", "2018 03 05 16 00", "04_03_18", "N");
        fastRun("2019 01 23 12 00", "2019 01 26 00 00", "24_01_19", "N");
        fastRun("2019 07 31 18 00", "2019 08 03 00 00", "01_08_19", "N");

        fastRun("2016 10 11 12 00", "2016 10 14 00 00", "13_10_16", "S");
        fastRun("2017 05 15 18 00", "2017 05 17 16 00", "16_05_17", "S");
        fastRun("2017 07 23 20 00", "2017 07 25 12 00", "24_07_17", "S");
        fastRun("2017 08 26 18 00", "2017 08 28 16 00", "27_08_17", "S");
        fastRun("2018 03 03 18 00", "2018 03 05 16 00", "04_03_18", "S");
        fastRun("2019 01 23 12 00", "2019 01 26 00 00", "24_01_19", "S");
        fastRun("2019 07 31 18 00", "2019 08 03 00 00", "01_08_19", "S");
    }
}