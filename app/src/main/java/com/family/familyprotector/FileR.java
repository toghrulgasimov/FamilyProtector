package com.family.familyprotector;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileR {
    public String read(String Filename) throws IOException {


        BufferedReader reader = new BufferedReader(new FileReader(Filename));
        String line;
        StringBuilder ans = new StringBuilder("");
        while ((line = reader.readLine()) != null)
        {
            ans.append(line);
        }
        reader.close();
        return ans.toString();
    }
}
