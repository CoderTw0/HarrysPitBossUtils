package com.lukflug.examplemod8forge.module.helpers;

public class QuadrentLabelHelper {
    public QuadrentLabelHelper() {
    }


    public static String getLabelForQuadrant(String mapName, String quadrant) {
        String label = quadrant;

        if ("Seasons".equals(mapName)) {
            switch (quadrant) {
                case "+,+":
                    label = "Winter";
                    break;
                case "+,-":
                    label = "Autumn";
                    break;
                case "-,-":
                    label = "Summer";
                    break;
                case "-,+":
                    label = "Spring";
                    break;
            }
        } else if ("Kings Map".equals(mapName)) {
            switch (quadrant) {
                case "+,+":
                    label = "City";
                    break;
                case "+,-":
                    label = "Farms";
                    break;
                case "-,-":
                    label = "Forest";
                    break;
                case "-,+":
                    label = "Port";
                    break;
            }
        }
        return label;
    }
}
