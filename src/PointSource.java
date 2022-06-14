import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointSource {
    private final String sourceName, stackHeight; //stackHeight should be float in SILAM, no unit added here
    private String sourceType;//sourceType = eljaam or fact3vah (my type names), or respectively A_PublicPower or B_Industry (database type names)
    private String xySize, zVelocity, tempr, bottom, top; //all these straight from https://klabgis.klab.ee/eerc/, no units added here, no conversion to SILAM units needed
    private final String sourceLat, sourceLon; //must be GCS
    private final String pollutant; //SO2, NOX and else that SILAM reads
    private String emission; //fwd run specific, g/s
    private String[] hourInDayIndex, dayInWeekIndex, monthInYearIndex; //time variation indices
    private List<String> sourceRows; //par_str_point rows that go straight to .v5 file

    //constructors
    public PointSource(String row) { //constructor for FORWARD run, gets row from "Ida-Virumaa allikad [yyyy]_[N or S].txt"

        String[] params = row.split("\t");
        sourceLat = params[0].replace(',', '.'); //GCS, decimal change too
        sourceLon = params[1].replace(',', '.');//todo https://gpa.maaamet.ee/calc/geo-lest/url/ or https://gpa.maaamet.ee/calc/geo-lest/files/Lambert.pdf
        sourceName = params[4]; //almost arbitrary: must be unique
        tempr = Double.toString(Double.parseDouble(params[5].replace(',', '.')) + 273); //SILAM should be Kelvin => converted
        zVelocity = params[6].replace(',', '.'); //should be m/s
        xySize = params[7].replace(',', '.'); //stack diameter, unit m
        stackHeight = params[9].replace(',', '.'); //don't know what it does exactly but is same as bottom and top just in case
        bottom = stackHeight; //bottom and top are to provide emitting pollutant in a vertical column
        top = stackHeight;
        emission = params[10].replace(',', '.'); //must be at the moment g/s
        pollutant = params[12]; //name (SILAM readable)
        sourceType = params[13]; //eljaam or fact3vah OR respectively A_PublicPower or B_Industry
    }

    //constr for INVERSE run: one point source
    public PointSource(String sourceName, String stackHeight, String sourceLat, String sourceLon, String pollutant) {
        this.sourceName = sourceName;
        this.stackHeight = stackHeight;//m
        this.sourceLat = sourceLat; //GCS
        this.sourceLon = sourceLon;//GCS
        this.pollutant = pollutant;//SILAM readable
    }

    //getters
    public String getSourceName() {
        return sourceName;
    }

    public String getStackHeight() {
        return stackHeight;
    }

    public String getSourceLat() {
        return sourceLat;
    }

    public String getSourceLon() {
        return sourceLon;
    }

    public String[] getHourInDayIndex() {
        return hourInDayIndex;
    }

    public String[] getDayInWeekIndex() {
        return dayInWeekIndex;
    }

    public String[] getMonthInYearIndex() {
        return monthInYearIndex;
    }

    public List<String> getSourceRows() {
        return sourceRows;
    }

    //setters
    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public void setTop(String top) {
        this.top = top;
    }

    //methods
    public void generateParStrPoint(String filename, String startTime, String endTime, String mode) throws IOException { //filename = "" if forward run
        //generates source rows (par_str_point rows). Emissions (fwd run) or concentrations (inv run) in time.
        sourceRows = new ArrayList<>();
        DateTimeFormatter toSourceRows = DateTimeFormatter.ofPattern("yyyy MM dd HH mm");
        LocalDateTime start = LocalDateTime.parse(startTime, toSourceRows);
        LocalDateTime end = LocalDateTime.parse(endTime, toSourceRows);

        if (mode.equals("FORWARD")) {
            int nrOfLines = Math.round(ChronoUnit.HOURS.between(start, end)); //finding neccessary number of rows
            for (int i = 0; i < nrOfLines; i++) { //dateTime, second, xy, bot, top, zvel, tempr, name: .v5 sourcefile row according to manual
                sourceRows.add("par_str_point = " + start.plusHours(i).format(toSourceRows) + " 0.0  " + xySize + " " + bottom + " " +
                        top + " " + zVelocity + " " + tempr + " " + pollutant + " " + emission);
            }
        } else if (mode.equals("INVERSE")) {
            DateTimeFormatter fromFile = DateTimeFormatter.ofPattern("d.MM.yyyy H:mm");
            LocalDateTime thisDate;

            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                br.readLine(); //header ignored
                String[] params;
                String row = br.readLine();
                while (row != null && !row.equals("")) {
                    params = row.split("\t");
                    thisDate = LocalDateTime.parse(params[0], fromFile);

                    //checking if thisDate is during observed period: finds period data from bigger file (ex: hourly data for full year)
                    if ((thisDate.isAfter(start) || thisDate.isEqual(start)) && (thisDate.isBefore(end) || thisDate.isEqual(end))) {
                        sourceRows.add("par_str_point = " + thisDate.format(toSourceRows) + " 0.0    0.0    " + bottom + " " + top + "   0.00   0.0  " +
                                pollutant + " " + params[1].replace(',', '.')); //replaces decimal
                    }
                    row = br.readLine();
                }
                sourceRows.add(System.lineSeparator()); //finally adds empty row
            }
        }
    }

    public void generateFwdTimeVarIndices() { //indices for FORWARD run only. fon INVERSE run all indices are equal to one (generated in SILAMRun class)
        if (sourceType.equals("eljaam") || sourceType.equals("A_PublicPower")) {
            monthInYearIndex = new String[]{"1.30", "1.03", "1.08", "0.96", "0.86",
                    "0.79", "0.85", "1.02", "1.", "1.04", "1.10", "0.98"};
            dayInWeekIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1."};
            hourInDayIndex = new String[]{"0.86747", "0.86747", //shifted to match UTC (UTC+2 in airviro)
                    "0.86747", "0.86747", "0.925301", "0.925301", "1.040964", "1.098795",
                    "1.156627", "1.156627", "1.156627", "1.156627", "1.098795", "1.098795",
                    "1.040964", "1.040964", "1.040964", "1.040964", "0.983133", "0.983133", "0.925301", "0.925301", "0.86747", "0.86747"};
        } else if (sourceType.equals("fact3vah") || sourceType.equals("B_Industry")) {
            monthInYearIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1."};
            dayInWeekIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1."};
            hourInDayIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1."
                    , "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1."};
        } else System.out.println("time variation indices not added! this will cause problems.");
    }

    @Override
    public String toString() { //can print out pointSource params for checking if needed
        return "PointSource{" +
                "sourceName='" + sourceName + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", stackHeight='" + stackHeight + '\'' +
                ", xySize='" + xySize + '\'' +
                ", zVelocity='" + zVelocity + '\'' +
                ", tempr='" + tempr + '\'' +
                ", bottom='" + bottom + '\'' +
                ", top='" + top + '\'' +
                ", sourceLat='" + sourceLat + '\'' +
                ", sourceLon='" + sourceLon + '\'' +
                ", pollutant='" + pollutant + '\'' +
                ", heide='" + emission + '\'' +
                ", hourInDayIndex=" + Arrays.toString(hourInDayIndex) +
                ", dayInWeekIndex=" + Arrays.toString(dayInWeekIndex) +
                ", monthInYearIndex=" + Arrays.toString(monthInYearIndex) +
                ", sourceRows=" + sourceRows +
                '}';
    }
}