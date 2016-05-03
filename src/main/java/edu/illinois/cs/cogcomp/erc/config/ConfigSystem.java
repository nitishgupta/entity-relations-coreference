package edu.illinois.cs.cogcomp.erc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class ConfigSystem {

    public static String configFile = Parameters.configFile;

    private static final Logger logger = LoggerFactory.getLogger(ConfigSystem.class);

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

        logger.info("\t *** Parameters ***");

        // Use reflection to populate static items in Parameters
        Field[] declaredMembers = Parameters.class.getDeclaredFields();
        for (Field field : declaredMembers) {
            if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                try {
                    if (props.containsKey(field.getName())) {
                        field.set(null, props.getProperty(field.getName()));
                        logger.info(field.getName() + " : " + props.getProperty(field.getName()));
                    }
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
