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

        if(props.containsKey("ACE05_SECTION_LIST")){
            Parameters.ACE05_SECTION_LIST = props.getProperty("ACE05_SECTION_LIST");
            System.out.println("ACE05_SECTION_LIST : " + Parameters.ACE05_SECTION_LIST);
        }

        if(props.containsKey("ACE04_SECTION_LIST")){
            Parameters.ACE04_SECTION_LIST = props.getProperty("ACE04_SECTION_LIST");
            System.out.println("ACE04_SECTION_LIST : " + Parameters.ACE04_SECTION_LIST);
        }

        if(props.containsKey("ACE04_SERIALIZED_DOCS")){
            Parameters.ACE04_SERIALIZED_DOCS = props.getProperty("ACE04_SERIALIZED_DOCS");
            System.out.println("ACE04_SERIALIZED_DOCS : " + Parameters.ACE04_SERIALIZED_DOCS);
        }

        if(props.containsKey("ACE05_SERIALIZED_DOCS")){
            Parameters.ACE05_SERIALIZED_DOCS = props.getProperty("ACE05_SERIALIZED_DOCS");
            System.out.println("ACE05_SERIALIZED_DOCS : " + Parameters.ACE05_SERIALIZED_DOCS);
        }

        if (props.containsKey("SL_PARAMETER_CONFIG_FILE")) {
            Parameters.SL_PARAMETER_CONFIG_FILE = props.getProperty("SL_PARAMETER_CONFIG_FILE");
            System.out.println("SL_PARAMETER_CONFIG_FILE : " + Parameters.SL_PARAMETER_CONFIG_FILE);
        }
    }
}
