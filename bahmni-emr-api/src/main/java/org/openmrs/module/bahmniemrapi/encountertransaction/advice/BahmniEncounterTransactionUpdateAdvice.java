package org.openmrs.module.bahmniemrapi.encountertransaction.advice;

import groovy.lang.GroovyClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.obscalculator.ObsValueCalculator;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.aop.MethodBeforeAdvice;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

public class BahmniEncounterTransactionUpdateAdvice implements MethodBeforeAdvice {

    private static Logger logger = LogManager.getLogger(BahmniEncounterTransactionUpdateAdvice.class);
    
    private static String BAHMNI_OBS_VALUE_CALCULATOR_FILENAME = "BahmniObsValueCalculator.groovy";
    
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        logger.info( "{}: Start", this.getClass().getName());
        GroovyClassLoader gcl = new GroovyClassLoader();
        String fileName = Paths.get(
        		OpenmrsUtil.getApplicationDataDirectory(),
        		"obscalculator",
        		BAHMNI_OBS_VALUE_CALCULATOR_FILENAME
        		).toString();
        Class clazz;
        try {
            clazz = gcl.parseClass(new File(fileName));
        } catch (FileNotFoundException fileNotFound) {
            logger.error("Could not find {} : {}. Possible system misconfiguration. {} ", ObsValueCalculator.class.getName(), fileName, fileNotFound);
            return;
        }
        logger.info(  "{} : Using rules in {}", this.getClass().getName(), clazz.getName());
        ObsValueCalculator obsValueCalculator = (ObsValueCalculator) clazz.newInstance();
        obsValueCalculator.run((BahmniEncounterTransaction) args[0]);
        logger.info( " {}: Done", this.getClass().getName());
    }
    
}
