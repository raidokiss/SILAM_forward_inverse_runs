import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeneratorFwd { //edasiarvutuse failide generaator

    public static List<PointSource> readInFWDPointSources(String filename) throws IOException { //FORWARD jooksu allikate failist sisse lugemine listi
        List<PointSource> allSources = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            br.readLine(); //päist ei loe
            String rida = br.readLine(); //loeb esimese allika
            while (rida != null && !rida.equals("")) {
                allSources.add(new PointSource(rida));
                rida = br.readLine();
            }
        }
        return allSources;
    }

    public static void fastRun(String start, String end, String date, String pollutant) throws Exception { //kiirelt kõigi failide genereerimiseks
        //uue jooksu tekitamine
        SILAMRun jooks = new SILAMRun("FORWARD", "forward_" + date + "_" + pollutant, //pollutant on S või N
                "forward_src_point03_" + date + "_" + pollutant, start, end);

        jooks.setLayerThickness("20. 20. 20. 20. 20. 20. 80. 200. 400. 1200. 2000."); //seda saab vajadusel muuta setteriga
        //kõik kõrgused eri kihtides
        jooks.generateCtrlFile();

        //
        List<PointSource> allikad = readInFWDPointSources("Ida-Virumaa allikad 20"
                + date.substring(date.length() - 2) + "_" + pollutant + ".txt"); //allikate lugemine failist

        for (PointSource fwdSource : allikad) {//igale allikale allikajoru genereerimine

            fwdSource.generateParStrPoint("", start, end, "FORWARD");
            fwdSource.generateFwdTimeVarIndices(); //igale allikale ajalise käigu genereerimine
        }
        jooks.generatePointSourceFile(allikad); //allikafaili tegemine
    }

    public static void main(String[] args) throws Exception {//allpool on jooksnud käsklused
        /*
        //FORWARD RUN TEMPLATE kui mitte kasutada fastRun

        SILAMRun forwardRun = new SILAMRun("FORWARD", "forward_[dd_MM_yy]", "forward_src_point03_[dd_MM_yy]", "[yyyy MM dd HH mm]", "[yyyy MM dd HH mm]");
        //ctrlFileName, pointSourceFileName ILMA laiendita (laiendid .control ja .v5 lisatakse)
        //FORWARD => startTime ON ENNE endTime, neil hetkel minuti täpsus, SILAMi formaat yyyy MM dd HH mm

        forwardRun.setLayerThickness("25. 25. 25. 25. 50. 100. 400. 750. 1200. 2000."); //erinev INV ja FWD run jaoks
        forwardRun.generateCtrlFile();

        List<PointSource> fwdSources = readInFWDPointSources("forward_test.txt");
        //filename kus gis rakenduse väljund (allikad), koordinaadid peab veel ise olema tehtud GCS, nimedes pole täpitähti,
        pollutant asendatud SILAMiga ühilduvaga, lisatud veerg "type" (viimane veerg), kus kas eljaam või fact3vah

        //58 on 7 pikk L-Est = latitude, 23 on 6 pikk L-Est = longitude

        for (PointSource fwdSource : fwdSources) {
            fwdSource.generateParStrPoint("", "2017 10 11 12 00", "2017 10 13 03 00", "FORWARD");
            //filename siin FORWARD run puhul on tühi "".
            //startTime, endTime õiges järjekorras, minuti täpsus, SILAMi formaat yyyy MM dd HH mm

            fwdSource.generateFwdTimeVarIndices();
            //selle meetodi jaoks on kõik varasemast olemas (allikate faili viimane veerg eelkõige)
        }

        forwardRun.generatePointSourceFile(fwdSources);*/

        //  parem oleks loomulikult mingi readcsv asi

        //start, end, date: //mastide jaoks liidetud
        // 03 viimasest P-st kuni 10 viimase P-ni 3h, else 2h

        //episoodide ajad
        //"2016 10 11 14 00", "2016 10 14 02 00", "_13_10_16" //kuupäevad, eesti tarbeajas
        //"2017 05 15 21 00", "2017 05 17 19 00", "_16_05_17"
        //"2017 07 23 23 00", "2017 07 25 15 00", "_24_07_17"
        //"2017 08 26 21 00", "2017 08 28 19 00", "_27_08_17"
        //"2018 03 03 20 00", "2018 03 05 18 00", "_04_03_18"
        //"2019 01 23 14 00", "2019 01 26 02 00", "_24_01_19"
        //"2019 07 31 21 00", "2019 08 03 03 00", "_01_08_19"

        //jooksud kõigi uuritud episoodide jaoks
        fastRun("2016 10 11 12 00", "2016 10 14 00 00", "13_10_16", "N");
        fastRun("2017 05 15 18 00", "2017 05 17 16 00", "16_05_17", "N");
        fastRun("2017 07 23 20 00", "2017 07 25 12 00", "24_07_17", "N");
        fastRun("2017 08 26 18 00", "2017 08 28 16 00", "27_08_17", "N");
        fastRun("2018 03 03 18 00", "2018 03 05 16 00", "04_03_18", "N"); //saadud projekti fwd run animatsioonist
        fastRun("2019 01 23 12 00", "2019 01 26 00 00", "24_01_19", "N"); //järgmised ainult hysplit kasutades
        fastRun("2019 07 31 18 00", "2019 08 03 00 00", "01_08_19", "N");

        //fastRun("2020 07 15 00 00", "2020 07 18 00 00", "16_07_20", "N");

        fastRun("2016 10 11 12 00", "2016 10 14 00 00", "13_10_16", "S");
        fastRun("2017 05 15 18 00", "2017 05 17 16 00", "16_05_17", "S");
        fastRun("2017 07 23 20 00", "2017 07 25 12 00", "24_07_17", "S");
        fastRun("2017 08 26 18 00", "2017 08 28 16 00", "27_08_17", "S");
        fastRun("2018 03 03 18 00", "2018 03 05 16 00", "04_03_18", "S");   //saadud projekti fwd run animatsioonist
        fastRun("2019 01 23 12 00", "2019 01 26 00 00", "24_01_19", "S");   //järgmised ainult hysplit kasutades
        fastRun("2019 07 31 18 00", "2019 08 03 00 00", "01_08_19", "S");

        //fastRun("2020 04 19 00 00", "2020 04 21 20 00", "20_04_20", "S");
        //fastRun("2020 05 24 00 00", "2020 05 27 18 00", "25_05_20", "S");
        //fastRun("2020 05 29 00 00", "2020 05 30 16 00", "29_05_20", "S");
    }
}