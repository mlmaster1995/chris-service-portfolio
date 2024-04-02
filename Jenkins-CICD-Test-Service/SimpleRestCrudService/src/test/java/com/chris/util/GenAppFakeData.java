package com.chris.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * this class is used to work with the test like generating the test data
 */

public class GenAppFakeData {
    @Test
    public void genFakeData() {
        String base = "INSERT INTO `member` (`first_name`,`last_name`,`email`) VALUES ('%s','%s','%s2024%s@chrismember.ca');";

        try {
            File myObj = new File("src/test/resources/fake-name-data.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] nameParts = line.split(" ");
                String firstName = nameParts[0].toLowerCase();
                String lastName = nameParts[1].toLowerCase();
                String query = String.format(base, firstName, lastName, firstName, lastName);
                System.out.println(query);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
