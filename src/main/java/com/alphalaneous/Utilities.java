package com.alphalaneous;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utilities {

    public static void sleep(int milliseconds) {
        sleep(milliseconds, 0);
    }

    public static void sleep(int milliseconds, int nano) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
            TimeUnit.NANOSECONDS.sleep(nano);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static float round(float value, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }
        float tmp = value * pow;
        float tmpSub = tmp - (int) tmp;

        return ((float) ((int) (value >= 0
                ? (tmpSub >= 0.5f ? tmp + 1 : tmp)
                : (tmpSub >= -0.5f ? tmp : tmp - 1)
        ))) / pow;
    }

    //int array to byte array
    public static byte[] intToByteArray(int[] array) {
        byte[] bytes = new byte[array.length * 4];
        for (int i = 0; i < array.length; i++) {
            int val = array[i];
            bytes[i * 4] = (byte) (val >>> 24);
            bytes[i * 4 + 1] = (byte) (val >>> 16);
            bytes[i * 4 + 2] = (byte) (val >>> 8);
            bytes[i * 4 + 3] = (byte) val;
        }
        return bytes;
    }

    public static boolean isBetween(float val, float a, float b, float maxDiff) {
        float temp;

        if (a > b) {
            temp = a;
            a = b;
            b = temp;
        }

        if (b - a > maxDiff) return false;

        return val >= a && val <= b;
    }

    public static String[] splitEveryOther(String s, char c){

        String[] split = s.split(c + "");

        ArrayList<String> recombine = new ArrayList<>();

        int i = 0;
        StringBuilder combine = new StringBuilder();
        for(String s1 : split){

            combine.append(s1);
            i++;

            if((i) % 2 == 0){
                recombine.add(combine.toString());
                combine = new StringBuilder();
                continue;
            }
            combine.append(c);

        }
        return recombine.toArray(new String[0]);
    }


    public static int countOccurrences(String s, char c){
        return s.length() - s.replace(c + "", "").length();
    }

    public static String xorBasic(String inputString, int key) {
        StringBuilder outputString = new StringBuilder();
        int j = 0;

        for(char c : inputString.toCharArray()) {
            outputString.append((char) (c ^ key));

        }

        return outputString.toString();
    }

    public static String xor(String inputString, String key) {
        StringBuilder outputString = new StringBuilder();
        int j = 0;

        for(char c : inputString.toCharArray()) {
            outputString.append((char) (c ^ key.charAt(j)));
            j++;
            if (j == key.length()) j = 0;
        }

        return outputString.toString();
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static byte[] compress(String str){
        try {
            if (str == null || str.length() == 0) {
                return null;
            }
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            return obj.toByteArray();
        }
        catch (Exception e){
            return null;
        }
    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis;
        gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }

    public static <T> String join(char delim, T[] array){
        StringBuilder sb = new StringBuilder();
        for (T n : array) {
            if (sb.length() > 0) sb.append(delim);
            sb.append(n);
        }
        return sb.toString();
    }

    public static String generateRS(int amount){
        String allowedChars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            sb.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }

        return sb.toString();
    }

    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public static String generateUDID(){
        return "S" + new Random().nextInt((100000000 - 100000) + 1) + 100000;
    }
}
