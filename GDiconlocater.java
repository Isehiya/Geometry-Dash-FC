
public class GDiconlocater {
    public static String GDiconLocater(
            int icon,
            boolean red1, boolean orange1, boolean yellow1, boolean green1, boolean blue1,
            boolean indigo1, boolean violet1, boolean white1, boolean black1,
            boolean red2, boolean orange2, boolean yellow2, boolean green2, boolean blue2,
            boolean indigo2, boolean violet2, boolean white2, boolean black2
    ) {
        String firstColorName = "";
        char firstLetter = 'X';
        if (red1)    { firstColorName = "red";    firstLetter = 'R'; }
        if (orange1) { firstColorName = "orange"; firstLetter = 'O'; }
        if (yellow1) { firstColorName = "yellow"; firstLetter = 'Y'; }
        if (green1)  { firstColorName = "green";  firstLetter = 'G'; }
        if (blue1)   { firstColorName = "blue";   firstLetter = 'B'; }
        if (indigo1) { firstColorName = "indigo"; firstLetter = 'I'; }
        if (violet1) { firstColorName = "violet"; firstLetter = 'V'; }
        if (white1)  { firstColorName = "white";  firstLetter = 'W'; }
        if (black1)  { firstColorName = "black";  firstLetter = 'B'; }

        String secondColorName = "";
        char secondLetter = 'X';
        if (red2)    { secondColorName = "red";    secondLetter = 'R'; }
        if (orange2) { secondColorName = "orange"; secondLetter = 'O'; }
        if (yellow2) { secondColorName = "yellow"; secondLetter = 'Y'; }
        if (green2)  { secondColorName = "green";  secondLetter = 'G'; }
        if (blue2)   { secondColorName = "blue";   secondLetter = 'B'; }
        if (indigo2) { secondColorName = "indigo"; secondLetter = 'I'; }
        if (violet2) { secondColorName = "violet"; secondLetter = 'V'; }
        if (white2)  { secondColorName = "white";  secondLetter = 'W'; }
        if (black2)  { secondColorName = "black";  secondLetter = 'B'; }

        String twoLetterCode = "" + firstLetter + secondLetter;
        return "GDprojectimages/GDicons/"
                + icon + "/"
                + firstColorName + "/"
                + twoLetterCode + ".png";
    }
}
