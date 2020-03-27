package com.family.familyprotector;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileR {
    public String read(String Filename) throws IOException {


        BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+Filename));
        String line;
        StringBuilder ans = new StringBuilder("");
        while ((line = reader.readLine()) != null)
        {
            ans.append(line);
        }
        reader.close();
        return ans.toString();
    }
    public void write(String name, String txt) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        fw = new FileWriter(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+ name, true);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        pw.print(txt+"&");
        pw.flush();
        pw.close();
        bw.close();
        fw.close();


    }
}
