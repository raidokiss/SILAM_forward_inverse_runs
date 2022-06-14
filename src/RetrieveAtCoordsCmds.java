public class RetrieveAtCoordsCmds {
    //a class for generating CDO commands: taking out certain data at certain coordinates
    //not much use outside my thesis I imagine

    public static void concSomeHeightsAtCoords(String[] dates, String[] pollutant) { //commands to take concentrations out from some heights
        //pollutant is "S"(O2) or "N"(OX)
        for (String date : dates) {
            for (String pollut : pollutant) {
                System.out.println("cd /usr/airviro/users/raido/UPT/output_forward_" + date + "_" + pollut + "\n");
                System.out.println("cdo outputtab,date,time,name,lon,lat,lev,value " +
                        "-remapbil,lon=27.308214/lat=58.277617 -sellevel,12.5,37.5,62.5,87.5,125 -selvar,cnc_" +
                        pollut + "O2_gas forward_" + date + "_" + pollut + ".nc4 > fwd_" + date + "_" + pollut + ".txt\n");
            }
        }
    }

    public static void concPlusParamsAllHeightsAtCoords(String[] dates, String[] pollutant) { //commands to take data out from all heights
        for (String date : dates) {
            for (String pollut : pollutant) {
                if (pollut.equals("S")) {
                    System.out.println("cdo outputtab,date,time,lon,lat,lev,name,value " +
                            "-remapbil,lon=27.308214/lat=58.277617 " +
                            "-selvar,cnc_SO2_gas,temperature,temp_2m,pot_temp,U_wind,V_wind,Kz_1m " +
                            "forward_" + date + "_S.nc4 > forward_" + date + "_S.txt");
                } else if (pollut.equals("N")) {
                    System.out.println("cdo outputtab,date,time,lon,lat,lev,name,value " +
                            "-remapbil,lon=27.308214/lat=58.277617 " +
                            "-selvar,cnc_NO2_gas,cnc_NO_gas,temperature,temp_2m,pot_temp,U_wind,V_wind,Kz_1m " +
                            "forward_" + date + "_N.nc4 > forward_" + date + "_N.txt");
                } else System.out.println("jama");
            }
        }
    }

    public static void main(String[] args) { //generating commands for all inversion periods
        String[] dates = {"13_10_16", "16_05_17", "24_07_17", "27_08_17", "04_03_18", "24_01_19", "01_08_19"};
        String[] pollutant = {"N", "S"}; //NOX or SO2
        concPlusParamsAllHeightsAtCoords(dates, pollutant);
    }
}