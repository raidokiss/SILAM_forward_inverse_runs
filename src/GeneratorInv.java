import java.util.ArrayList;
import java.util.List;

public class GeneratorInv { //tagasiarvutuse failide generaator

    public static void fastRun(String start, String end, String date) throws Exception { //kiirelt kõigi failide genereerimiseks
        //date formaadis _dd_MM_yy, start ja end SILAM time format
        //fastrun on 1 seirejaama jaoks eri aegadel jooksufailide genereerimiseks (kõik muud parameetrid samad)

        //uus jooks
        SILAMRun jooks = new SILAMRun("INVERSE", "inverse" + date + "_0_50",
                "inverse_src_point03" + date + "_0_50", end, start); //end ja start on ajavahemik

        jooks.setLayerThickness("25. 50. 50. 50. 100. 100. 400. 750. 1200. 2000.");
        //kõik kõrgused eri kihtides
        jooks.generateCtrlFile();

        PointSource allikas = new PointSource("Jarvselja",
                "110", "58.277617", "27.308214", "SO2");//SH on 110 aga inverse ei mängi rolli
        allikas.setBottom("0");//MANDATORY, neid saab praegu muuta vaid siin. Hetkel tundlikkusuuringu väärtused
        allikas.setTop("50");//MANDATORY

        allikas.generateParStrPoint("20" + date.substring(date.length() - 2) + "_SO2_invRunile.txt",//(standardse) failinime saab olemasolevast infost
                start, end, "INVERSE"); //siin start, end ajaliselt õiges järjekorras

        List<PointSource> allikasListis = new ArrayList<>(List.of(allikas)); //peab panema listi
        jooks.generatePointSourceFile(allikasListis);
    }

    public static void main(String[] args) throws Exception {//allpool on jooksnud käsklused
        /*
        //INVERSE RUN TEMPLATE kui mitte kasutada fastRun

        SILAMRun inverseRun = new SILAMRun("INVERSE", "inverse_[dd_MM_yy_height]m", "inverse_src_point03_[dd_MM_yy_height]m", "2017 10 13 03 00", "2017 10 11 12 00");
        //ctrlFileName, pointSourceFileName ILMA laiendita (laiendid .control ja .v5 lisatakse)
        //INVERSE => startTime ON PÄRAST endTime, neil hetkel minuti täpsus

        inverseRun.setLayerThickness("25. 50. 50. 50. 100. 100. 400. 750. 1200. 2000."); //erinev INV ja FWD run jaoks
        inverseRun.generateCtrlFile();

        PointSource invSource = new PointSource("Jarvselja", "110", "58.277617", "27.308214", "SO2"); //prg Järvselja
        //sourceName suht vaba
        //stackHeight ILMA ühikuta (meetrites)
        //sourceLat, Lon peab GCS, 58 on 7 pikk = latitude, 23 on 6 pikk = longitude
        //pollutant peab supported by SILAM

        invSource.generateParStrPoint("inverse_test.txt", "2017 10 11 12 00", "2017 10 13 03 00", "INVERSE");
        //filename kus 1. veerg ajad d.MM.yyyy H:mm, 2. konts. 1. rida ignoreeritakse (seal veergude nimed).
        //FAILIS 15 MIN ISE LAHUTADA!!
        //startTime, endTime õiges järjekorras, minuti täpsus, SILAMi formaat yyyy MM dd HH mm

        List<PointSource> allSources = new ArrayList<>(List.of(invSource));//vaja alati listi panna
        inverseRun.generatePointSourceFile(allSources);*/

        //episoodide ajad
        //start, end, date:
        //"2016 10 11 12 00", "2016 10 14 00 00", "_13_10_16"
        //"2017 05 15 18 00", "2017 05 17 16 00", "_16_05_17"
        //"2017 07 23 20 00", "2017 07 25 12 00", "_24_07_17"
        //"2017 08 26 18 00", "2017 08 28 16 00", "_27_08_17"
        //"2018 03 03 18 00", "2018 03 05 16 00", "_04_03_18" //ajad siin saadud projekti fwd run animatsioonist
        //"2019 01 23 12 00", "2019 01 26 00 00", "_24_01_19" //järkad aind hysplit kasutades
        //"2019 07 31 18 00", "2019 08 03 00 00", "_01_08_19"
        //"2020 04 19 00 00", "2020 04 21 20 00", "_20_04_20"
        //"2020 05 24 00 00", "2020 05 27 18 00", "_25_05_20"
        //"2020 05 29 00 00", "2020 05 30 16 00", "_29_05_20"
        //"2020 07 15 00 00", "2020 07 18 00 00", "_16_07_20"

        //SILAMis UTC aeg!!!

        fastRun("2016 10 11 12 00", "2016 10 14 00 00", "_13_10_16");
        fastRun("2017 05 15 18 00", "2017 05 17 16 00", "_16_05_17");
        fastRun("2017 07 23 20 00", "2017 07 25 12 00", "_24_07_17");
        fastRun("2017 08 26 18 00", "2017 08 28 16 00", "_27_08_17");
        fastRun("2018 03 03 18 00", "2018 03 05 16 00", "_04_03_18"); //saadud projekti fwd run animatsioonist
        fastRun("2019 01 23 12 00", "2019 01 26 00 00", "_24_01_19"); //järgmised ainult hysplit kasutades
        fastRun("2019 07 31 18 00", "2019 08 03 00 00", "_01_08_19");
        fastRun("2020 04 19 00 00", "2020 04 21 20 00", "_20_04_20");
        fastRun("2020 05 24 00 00", "2020 05 27 18 00", "_25_05_20");
        fastRun("2020 05 29 00 00", "2020 05 30 16 00", "_29_05_20");
        fastRun("2020 07 15 00 00", "2020 07 18 00 00", "_16_07_20");
    }
}