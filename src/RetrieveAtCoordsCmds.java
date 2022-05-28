public class RetrieveAtCoordsCmds { //CDO käskluste genereerimine automaatseks andmete väljavõtteks Järvselja koordinaatidelt kõikidest tehtud jooksudest

    public static void concSomeHeightsAtCoords(String[] kuup2ev, String[] pollutant) { //käsklused osadelt kõrgustelt andmete võtmiseks, hiljem ei kasutatud
        //pollutant on S või N
        for (String kuup : kuup2ev) {//kuupäevad
            for (String pollut : pollutant) {//pollutants
                System.out.println("cd /usr/airviro/users/raido/UPT/output_forward_" + kuup + "_" + pollut + "\n");
                System.out.println("cdo outputtab,date,time,name,lon,lat,lev,value " +//siin peab levels ise õigeks panema/kõigilt võtma
                        "-remapbil,lon=27.308214/lat=58.277617 -sellevel,12.5,37.5,62.5,87.5,125 -selvar,cnc_" +
                        pollut + "O2_gas forward_" + kuup + "_" + pollut + ".nc4 > fwd_" + kuup + "_" + pollut + ".txt\n");
            }
        }
    }

    public static void concPlusParamsAllHeightsAtCoords(String[] kuup2ev, String[] pollutant) { //kogu vajaliku andmestiku saamise jaoks käsklused
        for (String kuup : kuup2ev) {//episoodide kuupäevad (failinime osad)
            for (String pollut : pollutant) {//pollutants
                if (pollut.equals("S")) {
                    System.out.println("cdo outputtab,date,time,lon,lat,lev,name,value " +
                            "-remapbil,lon=27.308214/lat=58.277617 " +
                            "-selvar,cnc_SO2_gas,temperature,temp_2m,pot_temp,U_wind,V_wind,Kz_1m " +
                            "forward_" + kuup + "_S.nc4 > forward_" + kuup + "_S.txt");
                } else if (pollut.equals("N")) {
                    System.out.println("cdo outputtab,date,time,lon,lat,lev,name,value " +
                            "-remapbil,lon=27.308214/lat=58.277617 " +
                            "-selvar,cnc_NO2_gas,cnc_NO_gas,temperature,temp_2m,pot_temp,U_wind,V_wind,Kz_1m " +
                            "forward_" + kuup + "_N.nc4 > forward_" + kuup + "_N.txt");
                } else System.out.println("jama");
            }
        }
    }

    public static void main(String[] args) { //käskluste genereerimine kõigi episoodide jaoks
        String[] kuup2ev = {"13_10_16", "16_05_17", "24_07_17", "27_08_17", "04_03_18", "24_01_19", "01_08_19"};
        String[] pollutant = {"N", "S"}; //NOx või SO2
        concPlusParamsAllHeightsAtCoords(kuup2ev, pollutant);
    }
}