public class GDiconlocater {

    /**
     * Returns a string of the form "icon/firstColor/XY", where:
     *  - icon is the icon number (as an integer).
     *  - firstColor is the name of the selected first color (e.g. "yellow").
     *  - XY is the two-letter code made from the first letter of the first color
     *    and the first letter of the second color (both uppercase).
     *
     * You must pass exactly one true among (red1, orange1, yellow1, green1, blue1,
     * indigo1, violet1, white1, black1) and exactly one true among
     * (red2, orange2, yellow2, green2, blue2, indigo2, violet2, white2, black2).
     *
     * Example:
     *   GDiconLocator(1,
     *       false, false, true,  false, false, false, false, false, false,
     *       false, false, false, false, false, false, false, true,  false)
     *   → "1/yellow/YW"
     */
    public static String GDiconLocater(
            int icon,
            boolean red1, boolean orange1, boolean yellow1, boolean green1, boolean blue1,
            boolean indigo1, boolean violet1, boolean white1, boolean black1,
            boolean red2, boolean orange2, boolean yellow2, boolean green2, boolean blue2,
            boolean indigo2, boolean violet2, boolean white2, boolean black2
    ) {
        // Determine the first color name and its code letter
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

        // Determine the second color name and its code letter
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

        // Build the two-letter code
        String twoLetterCode = "" + firstLetter + secondLetter;

        // Return "GDprojectimages/GDicons/{icon}/{firstColor}/{twoLetterCode}.png"
        return "GDprojectimages/GDicons/"
                + icon
                + "/" + firstColorName
                + "/" + twoLetterCode
                + ".png";
    }

}
