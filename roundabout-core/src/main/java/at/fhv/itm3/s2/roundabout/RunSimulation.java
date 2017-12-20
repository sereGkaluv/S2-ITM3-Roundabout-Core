package at.fhv.itm3.s2.roundabout;

import at.fhv.itm14.trafsim.persistence.model.ScenarioDTO;
import at.fhv.itm14.trafsim.util.ScenarioReadException;
import at.fhv.itm14.trafsim.util.XMLParser;
import at.fhv.itm3.s2.roundabout.util.ConfigParser;
import at.fhv.itm3.s2.roundabout.util.ConfigParserException;
import at.fhv.itm3.s2.roundabout.util.ILogger;

public class RunSimulation implements ILogger {
    private static final String DEFAULT_ROUNDABOUT_CONFIG_FILENAME = "roundabout.xml";
    private static final String DEFAULT_TRAFSIM_CONFIG_FILENAME = "trafsim.xml";

    public static void main(String[] args){
        String roundaboutConfigFileName = getArgOrDefault(args, 0, DEFAULT_ROUNDABOUT_CONFIG_FILENAME);
        ConfigParser configParser = new ConfigParser(roundaboutConfigFileName);
        try {
            RoundaboutSimulationModel roundAboutConfig = configParser.generateModel();
        } catch (ConfigParserException e) {
            LOGGER.error(e);
        }

        String trafsimConfigFileName = getArgOrDefault(args, 0, DEFAULT_TRAFSIM_CONFIG_FILENAME);
        ScenarioDTO trafsimScenario;
        try {
            trafsimScenario = XMLParser.loadScenario(trafsimConfigFileName);
        } catch (ScenarioReadException e) {
            LOGGER.error(e);
        }
    }

    private static String getArgOrDefault(String[] array, int index, String defaultValue){
        if(array.length > index){
            return array[index];
        }
        return defaultValue;
    }
}