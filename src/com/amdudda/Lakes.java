package com.amdudda;

import java.io.*;
import java.util.*;

public class Lakes {

    public static void main(String[] args) throws IOException {
        // write your code here
        // program to report running times.
        // store our data in a HashMap - the key is a lake name
        // and the value is an ArrayList of lap times.
        HashMap<String, ArrayList<Double>> laketimes = new HashMap<String, ArrayList<Double>>();

        // explain the program and gather data
        System.out.println("This program processes times taken to run around lakes and returns your best times.");

        // DONE: error trapping!
        // get the data we'll be processing
        try {
            runProgram(laketimes);
        }
        catch (Exception e) {
            // I *think* I've caught all the likely problems with my data, but let's put this here
            // so the program can gracefully report other errors, then merrily proceed to the report.
            System.out.println("Something broke!  Here's the program's error message, which may or may not be useful:");
            System.out.println(e.toString());
        } // end try-catch

        // report the best times for each lake
        System.out.println("\nThis is a list of your best times around each lake.");
        System.out.println(reportBestTimes(laketimes));


    }// end main

    private static void runProgram(HashMap<String, ArrayList<Double>> laketimes) throws IOException {
        // this is the heart of the program - gathers data so it can then be processed into a report
        // set up a scanner and intialize variable to store its input
        Scanner o = new Scanner(System.in);
        String option;

        while (true) {
            System.out.println("\nSelect an option number: \n1. Enter lakes and times. \n2. Read data from \"times.txt\" in data " +
                    "directory. \n\tNB: times.txt should list each lake and time on a new line, in format 'Lakename, time in minutes'.");
            option = o.nextLine();

            // act on the user's choice - make sure there's valid input, or we have to ask for it to be re-entered.
            if (option.equals("1")) {
                collectData(laketimes);
                break;
            } else if (option.equals("2")) {
                // DONE: function to read in data from file instead of getting user input (not asked for in assignment but useful)
                fetchData(laketimes);
                break;
            } else {
                System.out.println("You have not made a valid selection.  Please try again.");
            }
        } // end while

        // close our scanner
        o.close();
    } // end runProgram

    private static void collectData(HashMap<String, ArrayList<Double>> lt) {
        // later, we'll write a function that gathers this data.
        // initialize scanner and variables
        Scanner s = new Scanner(System.in), t = new Scanner(System.in);
        String lake, newlake = "y";
        Double runtime;

        // collect user data
        System.out.println("OK, let's start entering your data!");
        while (newlake.equals("y")) {
            // get the name of the lake - convert to lower case for data cleanup.
            System.out.println("Which lake would you like to enter a time for?");
            lake = s.nextLine().toLowerCase();

            // get the time for the run in question
            while (true) {
                // DONE: incorporate graceful error trapping for user input
                try {
                    System.out.println(String.format("How long did it take you to run around %s for this run?",
                        titleCase(lake)));
                    runtime = t.nextDouble();
                    break;
                } catch (Exception e) {
                    System.out.println("That doesn't look like a number.  Please try again.");
                    // reset the scanner so it can accept fresh input
                    t = new Scanner(System.in);
                }  // end try-catch
            } // end while

            //Append data to relevant lake
            putData(lt, lake, runtime);

            // ask the user if they want to provide more data
            System.out.println("Would you like to add another running time (y/n)?");
            newlake = s.nextLine().toLowerCase();
        }
        //close the scanners
        s.close();
        t.close();
    }  // end collectData

    private static void     fetchData(HashMap<String, ArrayList<Double>> lt) throws IOException {
        // reads in data file instead of prompting for user input
        // data stored in data directory
        // initialize variables and set up file reader
        String fn = "./data/times.txt";
        File f = new File(fn);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String lake;
        Double runtime;

        // read in our data and add it to the hashmap
        String line = br.readLine();
        while (line != null) {
            // graceful error handling in case the datafile doesn't quite have the format we're looking for.
            try {
                lake = line.substring(0, line.indexOf(","));
                runtime = Double.parseDouble(line.substring(line.indexOf(",") + 1));
                putData(lt, lake, runtime);
            }
            catch (StringIndexOutOfBoundsException sioobe) {
                System.out.println("You seem to have forgotten to separate a location-time pair with a comma.");
                System.out.println(sioobe.toString());
                System.out.println("\nThe line causing this error was: " + line);
            }
            catch (NumberFormatException nfe) {
                System.out.println("You tried to use text when the program was expecting a number, " +
                        "or you accidentally put two times on the same line.");
                System.out.println(nfe.toString());
                System.out.println("\nThe line causing this error was: " + line);
            } // end try-catch

            // move to next line of file
            line = br.readLine();
        } // end while loop

        // close our data streams
        br.close();
        fr.close();
    } // end fetchData

    private static void putData(HashMap<String, ArrayList<Double>> lt, String lake, Double runtime) {
        /*
            Append data to relevant lake
            see final comment at http://stackoverflow.com/questions/3626752/key-existence-check-in-hashmap
            an easy way to check if a HashMap key is already there is to see if its value is null.
        */
        if (lt.get(lake) == null) {
            //no data associated with lake, put key and value into hashmap
            ArrayList<Double> newdata = new ArrayList<Double>();
            newdata.add(runtime);
            lt.put(lake, newdata);
        } else {
            // Append the value to the arraylist already in the hashmap
            // in pseudocode English, "get the current lake's data and add the new runtime to it"
            lt.get(lake).add(runtime);
        } // end if
    } // end putData

    private static String reportBestTimes(HashMap<String, ArrayList<Double>> lt) {
        // reads in hashmap lt, returns a string with each lake's best time.
        String best_times = "", capLake;
        ArrayList<Double> times;
        // get the list of keys in the hashmap
        // then find our best times and append them to the string reporting the best times.
        for (String k : lt.keySet()) {
            times = lt.get(k);
            double minTime = times.get(0);
            for (Double t : times) {
                minTime = Math.min(minTime, t);
            }
            best_times += String.format("%s: %.2f \n", titleCase(k), minTime);
        } // end for loop
        return best_times;
    } // end reportBestTimes

    private static String titleCase(String lk_name){
        // accepts a string and convertes it to title case
        // strip out excess white space
        lk_name = lk_name.trim();
        // capitalize the first letter
        lk_name = lk_name.substring(0,1).toUpperCase() + lk_name.substring(1);
        // loop: look for spaces and capitalize following letters.
        // We've already trimmed the string, so we don't need to worry about there being spaces at the end of the string.
        for (int i=0; i<lk_name.length(); i++) {
            if (lk_name.substring(i,i+1).equals(" ")) {
                lk_name = lk_name.substring(0,i+1) + lk_name.substring(i+1,i+2).toUpperCase() + lk_name.substring(i+2);
            } // end if
        } // end for
        return lk_name;
    } // end titleCase

} // end Lakes
