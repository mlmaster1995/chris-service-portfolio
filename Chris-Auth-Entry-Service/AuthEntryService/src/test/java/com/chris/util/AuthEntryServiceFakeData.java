package com.chris.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AuthEntryServiceFakeData {

    /**
     * remove comment when generating test data
     */
    //@Test
    public void genFakeData() {
        String base = "INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('%s','%s','%s2024%s@chrismember.ca', 1);";

        BCryptPasswordEncoder _encoder = new BCryptPasswordEncoder();

        try {
            File myObj = new File("src/test/resources/fake-name-data.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] nameParts = line.split(" ");
                String firstName = nameParts[0].toLowerCase();
                String lastName = nameParts[1].toLowerCase();
                String username = String.format("%s%s", firstName, lastName);
                String password = _encoder.encode(username);
                String query = String.format(base, username, password, firstName, lastName);
                System.out.println(query);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
