import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataFromCDO { //klass võtab sisendiks CDO andmed tabeli kujul ja viib erinevad mõõdetud suurused eraldi veergudesse, siis failidesse
    //väljastab iga sisendfaili jaoks kontsentratsioonide, temperatuuri ja tuuleandmete tekstifailid
    //klass on kirjutatud põhimõttel "write code fast", mitte "write fast code". Põhiline oli, et andmed kätte saaks.
    public static void readCncFromN(String failinimi, String output) throws IOException { //NO ja NO2 sisse lugemine

        try (BufferedReader br = new BufferedReader(new FileReader(failinimi));
             BufferedWriter bw = new BufferedWriter(new FileWriter(output))) { //avatakse fail, tehakse lugeja ja kirjutaja, andmed loetakse listidesse
            List<String> date = new ArrayList<>();
            List<String> time = new ArrayList<>();
            List<String> NO_10m = new ArrayList<>();
            List<String> NO_30m = new ArrayList<>();
            List<String> NO_50m = new ArrayList<>();
            List<String> NO_70m = new ArrayList<>();
            List<String> NO_90m = new ArrayList<>();
            List<String> NO_110m = new ArrayList<>();
            List<String> NO_160m = new ArrayList<>();
            List<String> NO_300m = new ArrayList<>();
            List<String> NO_600m = new ArrayList<>();
            List<String> NO_1400m = new ArrayList<>();
            List<String> NO_3000m = new ArrayList<>();

            List<String> NO2_10m = new ArrayList<>();
            List<String> NO2_30m = new ArrayList<>();
            List<String> NO2_50m = new ArrayList<>();
            List<String> NO2_70m = new ArrayList<>();
            List<String> NO2_90m = new ArrayList<>();
            List<String> NO2_110m = new ArrayList<>();
            List<String> NO2_160m = new ArrayList<>();
            List<String> NO2_300m = new ArrayList<>();
            List<String> NO2_600m = new ArrayList<>();
            List<String> NO2_1400m = new ArrayList<>();
            List<String> NO2_3000m = new ArrayList<>();

            br.readLine();//esimene rida (päis) ignoreeritakse
            //CDO rida: [, yyyy-mm-dd, hh:MM:ss, lon, lat, lev, name, value]
            String rida;
            while ((rida = br.readLine()) != null) { //loetakse läbi kõik CDO faili read, andmefailist listidesse
                double teisendConstNO = 1e9*0.03; //mol/m3 => ug/m3
                double teisendConstNO2 = 1e9*0.046;//mol/m3 => ug/m3
                String[] values = rida.split("\\s+"); //andmete eraldajaks on üks või mitu tühikut
                String name_lev = values[6] + "_" + values[5]; //saasteaine nimi ja kõrgus maapinnast
                switch (name_lev) { //listidesse kuupäev, aeg ja kontsentratsioonid
                    case "cnc_NO_gas_10" -> {
                        NO_10m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                        date.add(values[1]); //ühekordselt kuupäev ja kellaaeg
                        time.add(values[2]);
                    }
                    case "cnc_NO_gas_30" -> NO_30m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_50" -> NO_50m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_70" -> NO_70m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_90" -> NO_90m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_110" -> NO_110m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_160" -> NO_160m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_300" -> NO_300m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_600" -> NO_600m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_1400" -> NO_1400m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));
                    case "cnc_NO_gas_3000" -> NO_3000m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO));

                    case "cnc_NO2_gas_10" -> NO2_10m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_30" -> NO2_30m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_50" -> NO2_50m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_70" -> NO2_70m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_90" -> NO2_90m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_110" -> NO2_110m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_160" -> NO2_160m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_300" -> NO2_300m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_600" -> NO2_600m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_1400" -> NO2_1400m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));
                    case "cnc_NO2_gas_3000" -> NO2_3000m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConstNO2));

                }
            }

            //päis
            bw.write("date time NO_10 NO_30 NO_50 NO_70 NO_90 NO_110 NO_160 NO_300 NO_600 NO_1400 NO_3000 NO2_10 NO2_30 NO2_50 NO2_70 NO2_90 NO2_110 NO2_160 NO2_300 NO2_600 NO2_1400 NO2_3000");
            bw.newLine();
            for (int i = 0; i < time.size(); i++) { //ridade kirjutamine uude faili
                bw.write(date.get(i) + " " + time.get(i) + " " + NO_10m.get(i) + " " + NO_30m.get(i) + " "
                        + NO_50m.get(i) + " " + NO_70m.get(i) + " " + NO_90m.get(i) + " " + NO_110m.get(i) + " " + NO_160m.get(i) + " "
                        + NO_300m.get(i) + " " + NO_600m.get(i) + " " + NO_1400m.get(i) + " " + NO_3000m.get(i) +
                        " " + NO2_10m.get(i) + " " + NO2_30m.get(i) + " "
                        + NO2_50m.get(i) + " " + NO2_70m.get(i) + " " + NO2_90m.get(i) + " " + NO2_110m.get(i) + " " + NO2_160m.get(i) + " "
                        + NO2_300m.get(i) + " " + NO2_600m.get(i) + " " + NO2_1400m.get(i) + " " + NO2_3000m.get(i)
                );
                bw.newLine();
            }
        }
    }

    public static void readCncFromS(String failinimi, String output) throws IOException { //SO2 sisse lugemine, sarnane eelmisele

        try (BufferedReader br = new BufferedReader(new FileReader(failinimi));
             BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
            List<String> date = new ArrayList<>();
            List<String> time = new ArrayList<>();

            List<String> SO2_10m = new ArrayList<>();
            List<String> SO2_30m = new ArrayList<>();
            List<String> SO2_50m = new ArrayList<>();
            List<String> SO2_70m = new ArrayList<>();
            List<String> SO2_90m = new ArrayList<>();
            List<String> SO2_110m = new ArrayList<>();
            List<String> SO2_160m = new ArrayList<>();
            List<String> SO2_300m = new ArrayList<>();
            List<String> SO2_600m = new ArrayList<>();
            List<String> SO2_1400m = new ArrayList<>();
            List<String> SO2_3000m = new ArrayList<>();

            br.readLine();
            String rida;
            while ((rida = br.readLine()) != null) {
                double teisendConst = 1e9*0.064; //mol/m3 => ug/m3
                String[] values = rida.split("\\s+");
                String name_lev = values[6] + "_" + values[5];
                switch (name_lev) {
                    case "cnc_SO2_gas_10" -> {
                        SO2_10m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));//teisendab ja lisab listi kontsentratsioonid
                        date.add(values[1]);//lisab date => ühekordne
                        time.add(values[2]);
                    }//lisab time => ühekordne

                    case "cnc_SO2_gas_30" -> SO2_30m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_50" -> SO2_50m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_70" -> SO2_70m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_90" -> SO2_90m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_110" -> SO2_110m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_160" -> SO2_160m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_300" -> SO2_300m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_600" -> SO2_600m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_1400" -> SO2_1400m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));
                    case "cnc_SO2_gas_3000" -> SO2_3000m.add(String.valueOf(Double.parseDouble(values[7]) * teisendConst));

                }
            }

            bw.write("date time SO2_10 SO2_30 SO2_50 SO2_70 SO2_90 SO2_110 SO2_160 SO2_300 SO2_600 SO2_1400 SO2_3000");
            bw.newLine();
            for (int i = 0; i < time.size(); i++) {
                bw.write(date.get(i) + " " + time.get(i) + " "+ SO2_10m.get(i) + " " + SO2_30m.get(i) + " "
                        + SO2_50m.get(i) + " " + SO2_70m.get(i) + " " + SO2_90m.get(i) + " " + SO2_110m.get(i) + " " + SO2_160m.get(i) + " "
                        + SO2_300m.get(i) + " " + SO2_600m.get(i) + " " + SO2_1400m.get(i) + " " + SO2_3000m.get(i)
                );
                bw.newLine();
            }
        }
    }

    public static void readWind(String failinimi, String output) throws IOException { //tuule komponentide ja Kz koefitsendi sisse lugemine

        try (BufferedReader br = new BufferedReader(new FileReader(failinimi));
             BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
            List<String> date = new ArrayList<>();
            List<String> time = new ArrayList<>();
            List<Double> U_10m = new ArrayList<>();//U_wind
            List<Double> U_30m = new ArrayList<>();
            List<Double> U_50m = new ArrayList<>();
            List<Double> U_70m = new ArrayList<>();
            List<Double> U_90m = new ArrayList<>();
            List<Double> U_110m = new ArrayList<>();
            List<Double> U_160m = new ArrayList<>();
            List<Double> U_300m = new ArrayList<>();
            List<Double> U_600m = new ArrayList<>();
            List<Double> U_1400m = new ArrayList<>();
            List<Double> U_3000m = new ArrayList<>();

            List<Double> V_10m = new ArrayList<>();//V_wind
            List<Double> V_30m = new ArrayList<>();
            List<Double> V_50m = new ArrayList<>();
            List<Double> V_70m = new ArrayList<>();
            List<Double> V_90m = new ArrayList<>();
            List<Double> V_110m = new ArrayList<>();
            List<Double> V_160m = new ArrayList<>();
            List<Double> V_300m = new ArrayList<>();
            List<Double> V_600m = new ArrayList<>();
            List<Double> V_1400m = new ArrayList<>();
            List<Double> V_3000m = new ArrayList<>();

            List<String> Kz = new ArrayList<>();

            br.readLine();
            //[, 2019-07-31, 18:00:00, 27.3082, 58.2776, 10, temperature, 286.8608]
            String rida;
            while ((rida = br.readLine()) != null) {
                String[] values = rida.split("\\s+");
                String name_lev = values[6] + "_" + values[5];
                switch (name_lev) {
                    case "Kz_1m_0" -> Kz.add(values[7]);
                    case "U_wind_10" -> {
                        U_10m.add(Double.parseDouble(values[7]));//teisendab ja lisab listi tuuled
                        date.add(values[1]);//lisab date => ühekordne
                        time.add(values[2]);
                    }//lisab time => ühekordne
                    case "U_wind_30" -> U_30m.add(Double.parseDouble(values[7]));
                    case "U_wind_50" -> U_50m.add(Double.parseDouble(values[7]));
                    case "U_wind_70" -> U_70m.add(Double.parseDouble(values[7]));
                    case "U_wind_90" -> U_90m.add(Double.parseDouble(values[7]));
                    case "U_wind_110" -> U_110m.add(Double.parseDouble(values[7]));
                    case "U_wind_160" -> U_160m.add(Double.parseDouble(values[7]));
                    case "U_wind_300" -> U_300m.add(Double.parseDouble(values[7]));
                    case "U_wind_600" -> U_600m.add(Double.parseDouble(values[7]));
                    case "U_wind_1400" -> U_1400m.add(Double.parseDouble(values[7]));
                    case "U_wind_3000" -> U_3000m.add(Double.parseDouble(values[7]));

                    case "V_wind_10" -> V_10m.add(Double.parseDouble(values[7]));
                    case "V_wind_30" -> V_30m.add(Double.parseDouble(values[7]));
                    case "V_wind_50" -> V_50m.add(Double.parseDouble(values[7]));
                    case "V_wind_70" -> V_70m.add(Double.parseDouble(values[7]));
                    case "V_wind_90" -> V_90m.add(Double.parseDouble(values[7]));
                    case "V_wind_110" -> V_110m.add(Double.parseDouble(values[7]));
                    case "V_wind_160" -> V_160m.add(Double.parseDouble(values[7]));
                    case "V_wind_300" -> V_300m.add(Double.parseDouble(values[7]));
                    case "V_wind_600" -> V_600m.add(Double.parseDouble(values[7]));
                    case "V_wind_1400" -> V_1400m.add(Double.parseDouble(values[7]));
                    case "V_wind_3000" -> V_3000m.add(Double.parseDouble(values[7]));

                }
            }

            bw.write("date time Kz_1m U_wind_10 U_wind_30 U_wind_50 U_wind_70 U_wind_90 U_wind_110 U_wind_160 U_wind_300 U_wind_600 U_wind_1400 U_wind_3000 V_wind_10 V_wind_30 V_wind_50 V_wind_70 V_wind_90 V_wind_110 V_wind_160 V_wind_300 V_wind_600 V_wind_1400 V_wind_3000");
            bw.newLine();
            for (int i = 0; i < time.size(); i++) {
                bw.write(date.get(i) + " " + time.get(i) + " " + Kz.get(i) + " " + U_10m.get(i) + " " + U_30m.get(i) + " "
                        + U_50m.get(i) + " " + U_70m.get(i) + " " + U_90m.get(i) + " " + U_110m.get(i) + " " + U_160m.get(i) + " "
                        + U_300m.get(i) + " " + U_600m.get(i) + " " + U_1400m.get(i) + " " + U_3000m.get(i) +
                        " " + V_10m.get(i) + " " + V_30m.get(i) + " "
                        + V_50m.get(i) + " " + V_70m.get(i) + " " + V_90m.get(i) + " " + V_110m.get(i) + " " + V_160m.get(i) + " "
                        + V_300m.get(i) + " " + V_600m.get(i) + " " + V_1400m.get(i) + " " + V_3000m.get(i)
                );
                bw.newLine();
            }
        }
    }

    public static void readTemp(String failinimi, String output) throws IOException { //temperatuuride sisse lugemine

        try (BufferedReader br = new BufferedReader(new FileReader(failinimi));
             BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
            List<String> date = new ArrayList<>();
            List<String> time = new ArrayList<>();
            List<Double> temp_10m = new ArrayList<>();//olgu temp
            List<Double> temp_30m = new ArrayList<>();
            List<Double> temp_50m = new ArrayList<>();
            List<Double> temp_70m = new ArrayList<>();
            List<Double> temp_90m = new ArrayList<>();
            List<Double> temp_110m = new ArrayList<>();
            List<Double> temp_160m = new ArrayList<>();
            List<Double> temp_300m = new ArrayList<>();
            List<Double> temp_600m = new ArrayList<>();
            List<Double> temp_1400m = new ArrayList<>();
            List<Double> temp_3000m = new ArrayList<>();

            List<Double> potTemp_10m = new ArrayList<>();//olgu pot_temp
            List<Double> potTemp_30m = new ArrayList<>();
            List<Double> potTemp_50m = new ArrayList<>();
            List<Double> potTemp_70m = new ArrayList<>();
            List<Double> potTemp_90m = new ArrayList<>();
            List<Double> potTemp_110m = new ArrayList<>();
            List<Double> potTemp_160m = new ArrayList<>();
            List<Double> potTemp_300m = new ArrayList<>();
            List<Double> potTemp_600m = new ArrayList<>();
            List<Double> potTemp_1400m = new ArrayList<>();
            List<Double> potTemp_3000m = new ArrayList<>();

            List<String> temp_2m = new ArrayList<>();

            br.readLine();//skip first
            //[, 2019-07-31, 18:00:00, 27.3082, 58.2776, 10, temperature, 286.8608]
            String rida;
            while ((rida = br.readLine()) != null) {
                String[] values = rida.split("\\s+");
                String name_lev = values[6] + "_" + values[5];//check
                switch (name_lev) {
                    case "temp_2m_0" -> temp_2m.add(String.valueOf(Double.parseDouble(values[7]) - 273.15)); //teisendab ka
                    case "temperature_10" -> {
                        temp_10m.add(Double.parseDouble(values[7]) - 273.15);//teisendab ja lisab arraysse konts
                        date.add(values[1]);//lisab date => ühekordne
                        time.add(values[2]);
                    }//lisab time => ühekordne
                    case "temperature_30" -> temp_30m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_50" -> temp_50m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_70" -> temp_70m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_90" -> temp_90m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_110" -> temp_110m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_160" -> temp_160m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_300" -> temp_300m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_600" -> temp_600m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_1400" -> temp_1400m.add(Double.parseDouble(values[7]) - 273.15);
                    case "temperature_3000" -> temp_3000m.add(Double.parseDouble(values[7]) - 273.15);

                    case "pot_temp_10" -> potTemp_10m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_30" -> potTemp_30m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_50" -> potTemp_50m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_70" -> potTemp_70m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_90" -> potTemp_90m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_110" -> potTemp_110m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_160" -> potTemp_160m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_300" -> potTemp_300m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_600" -> potTemp_600m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_1400" -> potTemp_1400m.add(Double.parseDouble(values[7]) - 273.15);
                    case "pot_temp_3000" -> potTemp_3000m.add(Double.parseDouble(values[7]) - 273.15);

                }
            }

            bw.write("date time temp_2m temp_10 temp_30 temp_50 temp_70 temp_90 temp_110 temp_160 temp_300 temp_600 temp_1400 temp_3000 pot_temp_10 pot_temp_30 pot_temp_50 pot_temp_70 pot_temp_90 pot_temp_110 pot_temp_160 pot_temp_300 pot_temp_600 pot_temp_1400 pot_temp_3000");
            bw.newLine();
            for (int i = 0; i < time.size(); i++) {
                bw.write(date.get(i) + " " + time.get(i) + " " + temp_2m.get(i) + " " + temp_10m.get(i) + " " + temp_30m.get(i) + " "
                        + temp_50m.get(i) + " " + temp_70m.get(i) + " " + temp_90m.get(i) + " " + temp_110m.get(i) + " " + temp_160m.get(i) + " "
                        + temp_300m.get(i) + " " + temp_600m.get(i) + " " + temp_1400m.get(i) + " " + temp_3000m.get(i) +
                        " " + potTemp_10m.get(i) + " " + potTemp_30m.get(i) + " "
                        + potTemp_50m.get(i) + " " + potTemp_70m.get(i) + " " + potTemp_90m.get(i) + " " + potTemp_110m.get(i) + " " + potTemp_160m.get(i) + " "
                        + potTemp_300m.get(i) + " " + potTemp_600m.get(i) + " " + potTemp_1400m.get(i) + " " + potTemp_3000m.get(i)
                );
                bw.newLine();
            }
        }
    }

    public static void readData(String failinimi) throws IOException { //kogu andmete sisse lugemine standardse failinime järgi
        if (failinimi.endsWith("S.txt")){
            readCncFromS(failinimi, "cnc_" + failinimi);
        }else if (failinimi.endsWith("N.txt")){
            readCncFromN(failinimi, "cnc_" + failinimi);
        }
        readWind(failinimi, "wind_" + failinimi);
        readTemp(failinimi, "temp_" + failinimi);
    }

    public static void main(String[] args) throws IOException { //töös kasutatud failide sisse lugemine
        readData("forward_13_10_16_N.txt");
        readData("forward_16_05_17_N.txt");
        readData("forward_24_07_17_N.txt");
        readData("forward_27_08_17_N.txt");
        readData("forward_04_03_18_N.txt");
        readData("forward_24_01_19_N.txt");
        readData("forward_01_08_19_N.txt");

        readData("forward_13_10_16_S.txt");
        readData("forward_16_05_17_S.txt");
        readData("forward_24_07_17_S.txt");
        readData("forward_27_08_17_S.txt");
        readData("forward_04_03_18_S.txt");
        readData("forward_24_01_19_S.txt");
        readData("forward_01_08_19_S.txt");
    }
}