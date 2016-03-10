package edu.illinois.cs.cogcomp.erc.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class ConfigSystem {

    public static String configFile = Parameters.configFile;

    /**
     * THIS FUNCTION ASSUMES THAT THE DATASET HAS BEEN READ
     *
     * @param cF : Configuration File for LSH
     * @throws Exception
     */
    public static void initialize(String cF) {
        configFile = cF;
        parseProps(configFile);
    }

    public static void initialize() {
        parseProps(configFile);
    }

    public static void parseProps(String configFileName){
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(configFileName));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        System.out.println("\t *** Parameters ***");

        if(props.containsKey("ACE04_DATA_DIR")){
            Parameters.ACE04_DATA_DIR= props.getProperty("ACE04_DATA_DIR");
            System.out.println("ACE04_DATA_DIR : " + Parameters.ACE04_DATA_DIR);
        }

        if(props.containsKey("ACE05_DATA_DIR")){
            Parameters.ACE05_DATA_DIR= props.getProperty("ACE05_DATA_DIR");
            System.out.println("ACE05_DATA_DIR : " + Parameters.ACE05_DATA_DIR);
        }

        if(props.containsKey("ACE05_FILELIST")){
            Parameters.ACE05_FILELIST = props.getProperty("ACE05_FILELIST");
            System.out.println("ACE05_FILELIST : " + Parameters.ACE05_FILELIST);
        }

        if(props.containsKey("ACE05_TRAIN_FILELIST")){
            Parameters.ACE05_TRAIN_FILELIST = props.getProperty("ACE05_TRAIN_FILELIST");
            System.out.println("ACE05_TRAIN_FILELIST : " + Parameters.ACE05_TRAIN_FILELIST);
        }
        if(props.containsKey("ACE05_DEV_FILELIST")){
            Parameters.ACE05_DEV_FILELIST = props.getProperty("ACE05_DEV_FILELIST");
            System.out.println("ACE05_DEV_FILELIST : " + Parameters.ACE05_DEV_FILELIST);
        }
        if(props.containsKey("ACE05_TEST_FILELIST")){
            Parameters.ACE05_TEST_FILELIST = props.getProperty("ACE05_TEST_FILELIST");
            System.out.println("ACE05_TEST_FILELIST : " + Parameters.ACE05_TEST_FILELIST);
        }


        if(props.containsKey("ACE04_FILELIST")){
            Parameters.ACE04_FILELIST = props.getProperty("ACE04_FILELIST");
            System.out.println("ACE04_FILELIST : " + Parameters.ACE04_FILELIST);
        }

        if(props.containsKey("ACE04_SERIALIZED_DOCS")){
            Parameters.ACE04_SERIALIZED_DOCS = props.getProperty("ACE04_SERIALIZED_DOCS");
            System.out.println("ACE04_SERIALIZED_DOCS : " + Parameters.ACE04_SERIALIZED_DOCS);
        }

        if(props.containsKey("ACE05_SERIALIZED_DOCS")){
            Parameters.ACE05_SERIALIZED_DOCS = props.getProperty("ACE05_SERIALIZED_DOCS");
            System.out.println("ACE05_SERIALIZED_DOCS : " + Parameters.ACE05_SERIALIZED_DOCS);
        }

    }


}
