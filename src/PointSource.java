import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointSource { //siin kõik mis pole default faili stuff
    private final String sourceName, stackHeight; //stackHeight = number, ühikuta
    private String sourceType;//sourceType = eljaam või fact3vah (minu nimetused), või A_PublicPower, B_Industry
    private String xySize, zVelocity, tempr, bottom, top; //kõik otse kaardirakenduse väljundist, ühikuteta, ise teisendada pole vaja
    private final String sourceLat, sourceLon; //peavad olema geograafilised koordinaadid
    private final String pollutant; //SO2, NOx ja muu mis SILAMile sobib
    private String heide; //fwd run specific, g/s
    private String[] hourInDayIndex, dayInWeekIndex, monthInYearIndex; //ajaline käik
    private List<String> allikajoru; //siin allikaread mis lähevad otse faili

    //konstruktorid
    public PointSource(String rida) { //konstruktor FORWARD run jaoks, saab rea allika parameetritega failist

        String[] params = rida.split("\t");
        sourceLat = params[0].replace(',', '.'); //GCS, koma vahetus punktiks ka
        sourceLon = params[1].replace(',', '.');//todo https://gpa.maaamet.ee/calc/geo-lest/url/ saaks vist implementeerida
        sourceName = params[4];
        tempr = Double.toString(Double.parseDouble(params[5].replace(',', '.')) + 273); //eeldan SILAMis kelvinit, teisendasin (juhend lk 18)
        zVelocity = params[6].replace(',', '.'); //eeldan m/s
        xySize = params[7].replace(',', '.'); //eeldan et korsten on ring, ühik m
        stackHeight = params[9].replace(',', '.'); //ühik m, stackHeight on midagi analoogset bottom ja topile
        bottom = stackHeight; //kui poleks tegu punktallikaga vaid sambaga
        top = stackHeight;
        heide = params[10].replace(',', '.'); //ühik peab g/s
        pollutant = params[12]; //saasteaine nimetus (SILAMi oma)
        sourceType = params[13]; //eljaam või fact3vah
    }

    //konstr INVERSE run jaoks: üks punktallikas
    public PointSource(String sourceName, String stackHeight, String sourceLat, String sourceLon, String pollutant) {
        this.sourceName = sourceName;
        this.stackHeight = stackHeight;//m
        this.sourceLat = sourceLat; //GCS
        this.sourceLon = sourceLon;//GCS
        this.pollutant = pollutant;//SILAMi oma
    }

    //getterid
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

    public List<String> getAllikajoru() {
        return allikajoru;
    }

    //setterid
    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public void setTop(String top) {
        this.top = top;
    }

    //methods
    public void generateParStrPoint(String filename, String startTime, String endTime, String mode) throws IOException { //olgu filename "" kui forward run
        //genereerib allikaread (heide ajas, kontentratsioonid või heide). Kasutusel nii forward kui inverse jooksu jaoks
        allikajoru = new ArrayList<>();
        DateTimeFormatter jorusse = DateTimeFormatter.ofPattern("yyyy MM dd HH mm");
        LocalDateTime start = LocalDateTime.parse(startTime, jorusse);
        LocalDateTime end = LocalDateTime.parse(endTime, jorusse);

        if (mode.equals("FORWARD")) {
            int nrOfLines = Math.round(ChronoUnit.HOURS.between(start, end)); //ridade arvu leidmine
            for (int i = 0; i < nrOfLines; i++) { //aeg, sekund, xy, bot, top, zvel, tempr, name - allikafaili rida vastavalt juhendile
                allikajoru.add("par_str_point = " + start.plusHours(i).format(jorusse) + " 0.0  " + xySize + " " + bottom + " " + top + " " + zVelocity + " " + tempr + " " + pollutant + " " + heide);
            }
        } else if (mode.equals("INVERSE")) {
            DateTimeFormatter failist = DateTimeFormatter.ofPattern("d.MM.yyyy H:mm");
            LocalDateTime hetkeKuup2ev;

            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                br.readLine(); //päist ei loe
                String[] params; //parameetrid
                String rida = br.readLine();
                while (rida != null && !rida.equals("")) {
                    params = rida.split("\t");
                    hetkeKuup2ev = LocalDateTime.parse(params[0], failist);

                    //kontroll kas ajavahemiku sees: leiab vaadeldava ajavahemiku näiteks kogu aasta andmetest
                    if ((hetkeKuup2ev.isAfter(start) || hetkeKuup2ev.isEqual(start)) && (hetkeKuup2ev.isBefore(end) || hetkeKuup2ev.isEqual(end))) {
                        allikajoru.add("par_str_point = " + hetkeKuup2ev.format(jorusse) + " 0.0    0.0    " + bottom + " " + top + "   0.00   0.0  " +
                                pollutant + " " + params[1].replace(',', '.')); //replaces decimal
                    }
                    rida = br.readLine();
                }
                allikajoru.add(System.lineSeparator()); //lisab lõpuks tühja rea ka
            }
        }
    }

    public void generateFwdTimeVarIndices() { //ajalise käigu koefitsiendid
        if (sourceType.equals("eljaam") || sourceType.equals("A_PublicPower")) { //siin tehakse ainult FORWARD runi jaoks, INVERSE jaoks tehakse otse SILAMRun klassis (kõik ühed)
            monthInYearIndex = new String[]{"1.30", "1.03", "1.08", "0.96", "0.86",
                    "0.79", "0.85", "1.02", "1.", "1.04", "1.10", "0.98"};
            dayInWeekIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1."};
            hourInDayIndex = new String[]{"0.86747", "0.86747", //CONVERTED TO UTC (airviros on UTC+2)
                    "0.86747", "0.86747", "0.925301", "0.925301", "1.040964", "1.098795",
                    "1.156627", "1.156627", "1.156627", "1.156627", "1.098795", "1.098795",
                    "1.040964", "1.040964", "1.040964", "1.040964", "0.983133", "0.983133", "0.925301", "0.925301", "0.86747", "0.86747"};
        } else if (sourceType.equals("fact3vah") || sourceType.equals("B_Industry")) {
            monthInYearIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1."};
            dayInWeekIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1."};
            hourInDayIndex = new String[]{"1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1."
                    , "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1.", "1."};
        } else System.out.println("jama ajalise käiguga");
    }

    @Override
    public String toString() { //parameetrite kontrollimiseks vajadusel väljastamine
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
                ", heide='" + heide + '\'' +
                ", hourInDayIndex=" + Arrays.toString(hourInDayIndex) +
                ", dayInWeekIndex=" + Arrays.toString(dayInWeekIndex) +
                ", monthInYearIndex=" + Arrays.toString(monthInYearIndex) +
                ", allikajoru=" + allikajoru +
                '}';
    }
}