import java.util.ArrayList;
import java.util.List;

public class GeneratorInv {  //main runnable class that is used to generate a standard .control file and point source file (.v5)
//for INVERSE run
    public static void fastRun(String start, String end, String date) throws Exception { //method for generating similar runs fast
        //date in format _dd_MM_yy, start and end are SILAM time format (specifying period modelled)
        //fastrun is for generating input files for one monitoring station (point "source") during different periods
        //(all other parameters are the same)

        //new run
        SILAMRun run = new SILAMRun("INVERSE", "inverse" + date + "_0_50",
                "inverse_src_point03" + date + "_0_50", end, start);

        run.setLayerThickness("25. 50. 50. 50. 100. 100. 400. 750. 1200. 2000.");//same as in GeneratorFwd
        run.generateCtrlFile();

        PointSource source = new PointSource("Jarvselja",
                "110", "58.277617", "27.308214", "SO2");
        //stackHeight (110) might be actually redundant in inverse runs according to Rostislav
        source.setBottom("0");//MANDATORY, bottom = top = stack height for a stack.
        source.setTop("50");//MANDATORY, bottom and top exist to provide possibility to emit pollution from vertical column

        source.generateParStrPoint("20" + date.substring(date.length() - 2) + "_SO2_invRunile.txt",//standard filename, "invRunile" = "for inverse run"
                start, end, "INVERSE"); //here start and end are in right order

        List<PointSource> sourceInList = new ArrayList<>(List.of(source)); //must be in list
        run.generatePointSourceFile(sourceInList);
    }

    public static void main(String[] args) throws Exception {//firstly follows inverse run template, down below is code that ran
        /*
        //INVERSE RUN TEMPLATE if fastRun is not used

        SILAMRun inverseRun = new SILAMRun("INVERSE", "inverse_[dd_MM_yy_height]m", "inverse_src_point03_[dd_MM_yy_height]m", "2017 10 13 03 00", "2017 10 11 12 00");
        //ctrlFileName, pointSourceFileName WITHOUT extension (extensions .control and .v5 are added)
        //INVERSE => startTime is AFTER endTime, max time accuracy is one minute, SILAM time format is yyyy MM dd HH mm
        //all SILAM time is UTC!!!

        inverseRun.setLayerThickness("25. 50. 50. 50. 100. 100. 400. 750. 1200. 2000."); //was different for INV and FWD run
        inverseRun.generateCtrlFile();

        PointSource invSource = new PointSource("Jarvselja", "110", "58.277617", "27.308214", "SO2");
        //sourceName is arbitrary
        //stackHeight without unit (in metres)
        //sourceLat, sourceLon must be in GCS, 58. ... is 7 numbers long in L-Est = latitude, 23. ... is 6 long = longitude
        //pollutant must be supported by SILAM (ex: SO2, NOX)

        invSource.generateParStrPoint("inverse_test.txt", "2017 10 11 12 00", "2017 10 13 03 00", "INVERSE");
        //filename where 1. column is datetime: d.MM.yyyy H:mm, 2. column is concentrations, 1. row (header) is ignored
        //startTime, endTime in right order, minute accuracy, SILAM time format yyyy MM dd HH mm

        List<PointSource> allSources = new ArrayList<>(List.of(invSource));//always must be in list
        inverseRun.generatePointSourceFile(allSources);*/

        //in SILAM time is UTC!!!
        //input files generation for all validation episodes:
        fastRun("2016 10 11 12 00", "2016 10 14 00 00", "_13_10_16");
        fastRun("2017 05 15 18 00", "2017 05 17 16 00", "_16_05_17");
        fastRun("2017 07 23 20 00", "2017 07 25 12 00", "_24_07_17");
        fastRun("2017 08 26 18 00", "2017 08 28 16 00", "_27_08_17");
        fastRun("2018 03 03 18 00", "2018 03 05 16 00", "_04_03_18");
        fastRun("2019 01 23 12 00", "2019 01 26 00 00", "_24_01_19");
        fastRun("2019 07 31 18 00", "2019 08 03 00 00", "_01_08_19");
        fastRun("2020 04 19 00 00", "2020 04 21 20 00", "_20_04_20");
        fastRun("2020 05 24 00 00", "2020 05 27 18 00", "_25_05_20");
        fastRun("2020 05 29 00 00", "2020 05 30 16 00", "_29_05_20");
        fastRun("2020 07 15 00 00", "2020 07 18 00 00", "_16_07_20");
    }
}