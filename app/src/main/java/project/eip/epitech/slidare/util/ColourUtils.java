package project.eip.epitech.slidare.util;

public class ColourUtils {

    public static String calcTintValue(String origColour, double tintFactorR, double tintFactorG, double tintFactorB){
        int color = (int)Long.parseLong(origColour, 16);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        double newR = r+(tintFactorR*(255-r));
        double newG = g+(tintFactorG*(255-g));
        double newB = b+(tintFactorB*(255-b));

        String out = Long.toHexString(Math.round(newR))+Long.toHexString(Math.round(newG))+Long.toHexString(Math.round(newB));

        if(out.length() < 6){
            String rVal = Long.toHexString(Math.round(newR));
            String gVal = Long.toHexString(Math.round(newG));
            String bVal = Long.toHexString(Math.round(newB));

            if(rVal.length() < 2){
                rVal = "0"+rVal;
            }
            if(gVal.length() < 2){
                gVal = "0"+gVal;
            }
            if(bVal.length() < 2){
                bVal = "0"+bVal;
            }
            out = rVal+bVal+gVal;
        }

        return out;
    }

    public static String calcShadeValue(String origColour, double shadeFactorR, double shadeFactorG, double shadeFactorB){
        int color = (int)Long.parseLong(origColour, 16);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        double newR = r*shadeFactorR;
        double newG = g*shadeFactorG;
        double newB = b*shadeFactorB;

        String out = Long.toHexString(Math.round(newR))+Long.toHexString(Math.round(newG))+Long.toHexString(Math.round(newB));

        if(out.length() < 6){
            String rVal = Long.toHexString(Math.round(newR));
            String gVal = Long.toHexString(Math.round(newG));
            String bVal = Long.toHexString(Math.round(newB));

            if(rVal.length() < 2){
                rVal = "0"+rVal;
            }
            if(gVal.length() < 2){
                gVal = "0"+gVal;
            }
            if(bVal.length() < 2){
                bVal = "0"+bVal;
            }
            out = rVal+bVal+gVal;
        }

        return out;
    }
}
