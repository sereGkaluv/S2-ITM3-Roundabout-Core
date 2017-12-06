package at.fhv.itm3.s2.roundabout.test.event;

import at.fhv.itm3.s2.roundabout.RoundaboutModel;
import at.fhv.itm3.s2.roundabout.entity.StreetSection;
import at.fhv.itm3.s2.roundabout.event.CarGenerateEvent;
import desmoj.core.simulator.Experiment;
import org.junit.Assert;
import org.junit.Test;

public class CarGenerateEventTest {

    @Test
    public void eventRoutineTest() throws Exception {
        RoundaboutModel model = new RoundaboutModel(null, "", false, false);

        // create experiment and connect it with the model
        // this is necessary because otherwise there is a NullPointerException thrown
        Experiment exp = new Experiment("RoundaboutModel Experiment");
        model.connectToExperiment(exp);

        StreetSection section = new StreetSection(10.0, null, null, model, null, false);
        Assert.assertTrue(section.isEmpty());

        CarGenerateEvent event = new CarGenerateEvent(model, null, false);

        event.eventRoutine(section);
        Assert.assertFalse(section.isEmpty());
    }
}