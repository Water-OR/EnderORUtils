package io.github.enderor.client.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ColorUtils {
  protected int red   = 0;
  protected int green = 0;
  protected int blue  = 0;
  
  protected double H = 0;
  protected double S = 0;
  protected double V = 0;
  
  protected int alpha = 0;
  
  protected boolean enableRainbow = false;
  protected long rainbowSpeed = 1000L;
  protected long rainbowOffset = 0L;
  
  public static int getRed  (int RGBA) { return (RGBA >>> 24) & 255; }
  public static int getGreen(int RGBA) { return (RGBA >>> 16) & 255; }
  public static int getBlue (int RGBA) { return (RGBA >>>  8) & 255; }
  public static int getAlpha(int RGBA) { return (RGBA       ) & 255; }
  
  public static double getRedPerCent  (int RGBA) { return getRed  (RGBA) / 255D; }
  public static double getGreenPerCent(int RGBA) { return getGreen(RGBA) / 255D; }
  public static double getBluePerCent (int RGBA) { return getBlue (RGBA) / 255D; }
  public static double getAlphaPerCent(int RGBA) { return getAlpha(RGBA) / 255D; }
  
  public static void setGLColor4d(int RGBA) {
    glColor4d(
      getRedPerCent  (RGBA),
      getGreenPerCent(RGBA),
      getBluePerCent (RGBA),
      getAlphaPerCent(RGBA)
    );
  }
  
  public static Color RGBA_to_Color(int RGBA) {
    return new Color(
      getRed  (RGBA),
      getGreen(RGBA),
      getBlue (RGBA),
      getAlpha(RGBA)
    );
  }
  
  public static int RGBA_to_ARGB(int RGBA) { return ((RGBA & 0xFFFFFF00) >>> 8) | ((RGBA & 0x000000FF) << 24); }
  public static int ARGB_to_RGBA(int ARGB) { return ((ARGB & 0x00FFFFFF) << 8) | ((ARGB & 0xFF000000) >>> 24); }
  
  public ColorUtils fromRGBA(int RGBA) {
    red   = (RGBA >>> 24) & 255;
    green = (RGBA >>> 16) & 255;
    blue  = (RGBA >>> 8 ) & 255;
    alpha = (RGBA       ) & 255;
    saveHVS();
    return this;
  }
  
  public ColorUtils fromRGBA(int red, int green, int blue, int alpha) {
    this.red   = red  ;
    this.blue  = blue ;
    this.green = green;
    this.alpha = alpha;
    saveHVS();
    return this;
  }
  
  public ColorUtils fromARGB(int ARGB) {
    alpha = (ARGB >>> 24) & 255;
    red   = (ARGB >>> 16) & 255;
    green = (ARGB >>> 8 ) & 255;
    blue  = (ARGB       ) & 255;
    saveHVS();
    return this;
  }
  
  public ColorUtils fromARGB(int alpha, int red, int green, int blue) {
    this.alpha = alpha;
    this.red   = red  ;
    this.blue  = blue ;
    this.green = green;
    saveHVS();
    return this;
  }
  
  public ColorUtils fromColor(@NotNull Color color) {
    red   = color.getRed  ();
    green = color.getGreen();
    blue  = color.getBlue ();
    alpha = color.getAlpha();
    saveHVS();
    return this;
  }
  
  public static ColorUtils makeFromRGBA(int RGBA) { return new ColorUtils().fromRGBA(RGBA); }
  
  public static ColorUtils makeFromRGBA(int red, int green, int blue, int alpha) { return new ColorUtils().fromRGBA(red, green, blue, alpha); }
  
  public static ColorUtils makeFromARGB(int ARGB) { return new ColorUtils().fromARGB(ARGB); }
  
  public static ColorUtils makeFromARGB(int alpha, int red, int green, int blue) { return new ColorUtils().fromARGB(alpha, red, green, blue); }
  
  public static ColorUtils makeFromColor(Color color) { return new ColorUtils().fromColor(color); }
  
  public int getRGBA() {
    return red   << 24 |
           green << 16 |
           blue  <<  8 |
           alpha;
  }
  
  public int getARGB() {
    return alpha << 24 |
           red   << 16 |
           green <<  8 |
           blue;
  }
  
  public Color getColor() { return new Color(red, green, blue, alpha); }
  
  protected void saveHVS() {
    int max=Math.max((red),Math.max(green,blue));
    int min=Math.min((red),Math.min(green,blue));
    double/*#E%W&*/nowBlue,/*%#*/nowGreen,nowRed;
    nowRed/**/=(((red-min)*255.D))/((max)-(min));
    nowBlue=0+(((blue-min)*255.D))/((max)-(min));
    nowGreen=(((green-min)*255.D))/((max)-(min));
    V/*#E*/=((/*#E*/(max))*100.0D)/(255.0+0.00D);
    S/*#E*/=(((max)-(min))*100.0D)/(1>max?max:1);
    if(255D-nowRed/*#E%M*/<.001D&&nowBlue<1e-3D){
    H=000.D+(nowGreen*60D)/(255D/*#E%W&G*/);}else
    if(255D-nowGreen/*#%*/<.001D&&nowBlue<1e-3D){
    H=120.D-(nowRed*60.0D)/(255D/*#E%W&G*/);}else
    if(255D-nowGreen/*#%*/<.001&&(nowRed)<1e-3D){
    H=120.D+(nowBlue*60.D)/(255D/*#E%W&G*/);}else
    if(255D-nowBlue/*#E%*/<.001&&(nowRed)<1e-3D){
    H=240.0-(nowGreen*60D)/(255D/*#E%W&G*/);}else
    if(255D-nowBlue/*#E%*/<.001&&nowGreen<1e-3D){
    H=240.D+(nowRed*60.0D)/(255D/*#E%W&G*/);}else
    if(255D-nowRed/*#E%M*/<.001&&nowGreen<1e-3D){
    H=360.D-(nowBlue*60.D)/(255D/*#E%W&G@/$R*/);}
  }
  
  protected void loadHVS() {
    if(0.0D<=H&&H<60.D){red  =(int)((255*V)/100);
    green=(int)((255-(15300-(0x0+H)*255)*S/(6e3))
    *V/100);blue =(int)(255*V*(100-S)/1e4D);}else
    if(60.D<=H&&H<120D){green=(int)((255*V)/100);
    red  =(int)((255-(15300-(120-H)*255)*S/(6e3))
    *V/100);blue =(int)(255*V*(100-S)/1e4D);}else
    if(120D<=H&&H<180D){green=(int)((255*V)/100);
    blue =(int)((255-(15300-(120+H)*255)*S/(6e3))
    *V/100);red  =(int)(255*V*(100-S)/1e4D);}else
    if(180D<=H&&H<240D){blue =(int)((255*V)/100);
    green=(int)((255-(15300-(240-H)*255)*S/(6e3))
    *V/100);red  =(int)(255*V*(100-S)/1e4D);}else
    if(240D<=H&&H<300D){blue =(int)((255*V)/100);
    red  =(int)((255-(15300-(240+H)*255)*S/(6e3))
    *V/100);green=(int)(255*V*(100-S)/1e4D);}else
    if(300D<=H&&H<360D){red  =(int)((255*V)/100);
    blue =(int)((255-(15300-(360-H)*255)*S/(6e3))
    *V/100);green=(int)(255*V*(100-S)/1e4D);}/**/
  }
  
  public ColorUtils setEnableRainbow(boolean enableRainbow) { this.enableRainbow = enableRainbow; return this; }
  public boolean    isEnableRainbow()                       { return enableRainbow; }
  public ColorUtils changeEnableRainbow()                   { return setEnableRainbow(!enableRainbow); }
  
  /**
   * Change Color if rainbow is enabled
   */
  public void changeColor() {
    saveHVS();
    if (isEnableRainbow()) { H = (System.nanoTime() + rainbowOffset) % rainbowSpeed * 360D / rainbowSpeed; }
    loadHVS();
  }
  
  public ColorUtils setRainbow(int rainbowSpeed, int rainbowOffset) {
    setEnableRainbow(true);
    this.rainbowSpeed = rainbowSpeed;
    this.rainbowOffset = rainbowOffset;
    changeColor();
    return this;
  }
  
  public ColorUtils setRed(int red)         { this.red = red; saveHVS(); return this; }
  
  public ColorUtils setGreen(int green)     { this.green = green; saveHVS(); return this; }
  
  public ColorUtils setBlue(int blue)       { this.blue = blue; saveHVS(); return this; }
  
  public ColorUtils setAlpha(int alpha)     { this.alpha = alpha; return this; }
  
  public ColorUtils setHue(double h)        { H = h; loadHVS(); return this; }
  
  public ColorUtils setSaturation(double s) { S = s; loadHVS(); return this; }
  
  public ColorUtils setValue(double v)      { V = v; loadHVS(); return this; }
  
  public int getRed  () { return red  ; }
  public int getGreen() { return green; }
  public int getBlue () { return blue ; }
  public int getAlpha() { return alpha; }
  
  public double getHue       () { return H; }
  public double getSaturation() { return S; }
  public double getValue     () { return V; }
}
