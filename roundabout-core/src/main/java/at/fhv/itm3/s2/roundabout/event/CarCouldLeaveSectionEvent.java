package at.fhv.itm3.s2.roundabout.event;

import at.fhv.itm3.s2.roundabout.RoundaboutModel;
import at.fhv.itm3.s2.roundabout.api.entity.IStreetSection;
import at.fhv.itm3.s2.roundabout.entity.StreetSection;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class CarCouldLeaveSectionEvent extends Event<StreetSection> {

    /**
     * A reference to the RoundaboutModel the CarCouldLeaveSectionEvent is part of
     */
    private RoundaboutModel myModel;

    /**
     * Constructs a new CarCouldLeaveSectionEvent
     *
     * @param model         the model this event belongs to
     * @param name          this event's name
     * @param showInTrace   flag to indicate if this event shall produce output for the trace
     */
    public CarCouldLeaveSectionEvent(Model model, String name, boolean showInTrace) {
        super(model, name, showInTrace);
        myModel = (RoundaboutModel)model;
    }

    /**
     * The event routine describes the moving of a car from one section to the next section.
     *
     * If the given section has a car at its exit point, it is checked if this car could leave this
     * section and enter the next one. If that is true, a new CarCouldLeaveSectionEvent for the next
     * section (the section the car enters) is scheduled after the time the car needs to traverse that
     * section. After that the car is moved from this section to the next one.
     * If the current section (the section in which the car was before moving) is not empty, a new
     * CarCouldLeaveSectionEvent is scheduled for the current section after the transition time of the
     * first car (only if this time is passed it makes sense to update all car positions on this time and
     * check if there is another car to leave the section).
     * Moreover for all previous sections of the current section a new CarCouldLeaveSectionEvent is immediately
     * scheduled to check if the previous sections have a car which could enter the current section.
     *
     * @param section
     * @throws SuspendExecution
     */
    public void eventRoutine(StreetSection section) throws SuspendExecution {
        if(section.isFirstCarOnExitPoint()) {
            if(section.carCouldEnterNextSection()) {
                new CarCouldLeaveSectionEvent(myModel, "CarCouldLeaveSectionEvent", true).schedule((StreetSection)section.getFirstCar().getNextSection(), new TimeSpan(section.getFirstCar().getTimeToTraverseSection(section.getFirstCar().getNextSection()), TimeUnit.SECONDS));
                section.moveFirstCarToNextSection();

                if(!section.isEmpty()) {
                    section.updateAllCarsPositions();
                    new CarCouldLeaveSectionEvent(myModel, "CarCouldLeaveSectionEvent", true).schedule(section, new TimeSpan(section.getFirstCar().getTransitionTime(), TimeUnit.SECONDS));
                }

                for (IStreetSection prevSection : section.getPreviousStreetConnector().getPreviousSections()) {
                    new CarCouldLeaveSectionEvent(myModel, "CarCouldLeaveSectionEvent", true).schedule((StreetSection)prevSection, new TimeSpan(0, TimeUnit.SECONDS));
                }
            }
        }
    }
}