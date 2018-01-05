package at.fhv.itm3.s2.roundabout.mocks;

import at.fhv.itm14.trafsim.model.ModelFactory;
import at.fhv.itm14.trafsim.model.entities.AbstractConsumer;
import at.fhv.itm14.trafsim.model.entities.AbstractProSumer;
import at.fhv.itm14.trafsim.model.entities.IConsumer;
import at.fhv.itm14.trafsim.model.entities.IProducer;
import at.fhv.itm14.trafsim.model.entities.intersection.FixedCirculationController;
import at.fhv.itm3.s2.roundabout.RoundaboutSimulationModel;
import at.fhv.itm3.s2.roundabout.RoundaboutSink;
import at.fhv.itm3.s2.roundabout.api.entity.AbstractSource;
import at.fhv.itm3.s2.roundabout.api.entity.IRoute;
import at.fhv.itm3.s2.roundabout.api.entity.Street;
import at.fhv.itm3.s2.roundabout.controller.IntersectionController;
import at.fhv.itm3.s2.roundabout.entity.RoundaboutIntersection;
import at.fhv.itm3.s2.roundabout.entity.Route;
import at.fhv.itm3.s2.roundabout.entity.StreetConnector;
import at.fhv.itm3.s2.roundabout.entity.StreetSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static at.fhv.itm3.s2.roundabout.mocks.RouteType.*;

public class RouteGeneratorMock {

    private Map<RouteType, IRoute> routes;
    private RoundaboutSimulationModel model;

    public RouteGeneratorMock(RoundaboutSimulationModel model) {
        routes = new HashMap<>();
        this.model = model;

        initializeRouteWithTwoStreetSections(TWO_STREETSECTIONS_ONE_CAR);
        initializeRouteWithTwoStreetSections(TWO_STREETSECTIONS_TWO_CARS);
        initializeRouteWithIntersection(STREETSECTION_INTERSECTION_STREETSECTION_ONE_CAR);
        initializeRouteWithIntersection(STREETSECTION_INTERSECTION_STREETSECTION_TWO_CARS);
    }

    public IRoute getRoute(RouteType type) {
        return routes.getOrDefault(type, null);
    }

    private void initializeRouteWithTwoStreetSections(RouteType routeType) {

        // INITIALIZE ROUTE WITH TWO STREETSECTIONS
        // initialize streets and sink
        Street street1_1 = new StreetSection(10.0, model, "", false);
        Street street1_2 = new StreetSection(10.0, model, "", false);
        RoundaboutSink roundaboutSink1 = new RoundaboutSink(model, "", false);

        // initialize connectors
        Set<IProducer> prevStreetsForConnector1_1 = new HashSet<>();
        prevStreetsForConnector1_1.add(street1_1);

        Set<IConsumer> nextStreetsForConnector1_1 = new HashSet<>();
        nextStreetsForConnector1_1.add(street1_2);

        StreetConnector connector1_1 = new StreetConnector(prevStreetsForConnector1_1, nextStreetsForConnector1_1);
        street1_1.setNextStreetConnector(connector1_1);
        street1_2.setPreviousStreetConnector(connector1_1);

        Set<IProducer> prevStreetsForConnector1_2 = new HashSet<>();
        prevStreetsForConnector1_2.add(street1_2);

        Set<IConsumer> nextStreetsForConnector1_2 = new HashSet<>();
        nextStreetsForConnector1_2.add(roundaboutSink1);

        StreetConnector connector1_2 = new StreetConnector(prevStreetsForConnector1_2, nextStreetsForConnector1_2);
        street1_2.setNextStreetConnector(connector1_2);
        roundaboutSink1.setPreviousStreetConnector(connector1_2);

        // initialize source and route
        AbstractSource source1 = new RoundaboutSourceMock(
                model,
                "",
                false,
                street1_1,
                routeType.getCarsToGenerate(),
                this,
                routeType
        );

        IRoute route = new Route();
        route.addSource(source1);
        route.addSection(street1_1);
        route.addSection(street1_2);
        route.addSection(roundaboutSink1);

        routes.put(routeType, route);
    }

    private void initializeRouteWithIntersection(RouteType routeType) {
        float turnaroundTime = 60;
        float[] phaseShiftTimes = new float[]{0.0F, 10.0F, 20.0F};
        double intersectionTraverseTime = 5.0;
        double accelerationTime = 2.0;
        double yellowDuration = turnaroundTime / 8;
        double greenDuration = turnaroundTime / 2 - yellowDuration;
        int outDirection = 1;
        int inDirection = 0;

        // initialize intersection
        RoundaboutIntersection intersection = new RoundaboutIntersection(model, "", false, 2);
        intersection.setServiceDelay(accelerationTime);
        FixedCirculationController ic = ModelFactory.getInstance(model).createOneWayController(
                intersection,
                greenDuration,
                yellowDuration,
                phaseShiftTimes[0]
        );
        intersection.attachController(ic);

        // initialize streets
        AbstractProSumer street1 = new StreetSection(10.0, model, "", false);
        AbstractProSumer street2 = new StreetSection(10.0, model, "", false);

        // initialize roundaboutSink
        RoundaboutSink roundaboutSink = new RoundaboutSink(model, "", false);

        // initialize source
        AbstractSource source = new RoundaboutSourceMock(
                model,
                "",
                false,
                (StreetSection) street1,
                routeType.getCarsToGenerate(),
                this,
                routeType
        );

        // connect streets with intersection
        IntersectionController.getInstance().setIntersectionInDirectionMapping(intersection, street1, inDirection);
        IntersectionController.getInstance().setIntersectionOutDirectionMapping(intersection, street2, outDirection);
        intersection.attachProducer(inDirection, street1.toProducer());
        intersection.attachConsumer(outDirection, street2.toConsumer());
        intersection.createConnectionQueue(
                street1.toProducer(),
                new AbstractConsumer[]{street2.toConsumer()},
                new double[]{intersectionTraverseTime},
                new double[]{1.0}
        );

        // initialize connectors
        Set<IProducer> prevStreetsForConnector1 = new HashSet<>();
        prevStreetsForConnector1.add(street2);

        Set<IConsumer> nextStreetsForConnector1 = new HashSet<>();
        nextStreetsForConnector1.add(roundaboutSink);

        StreetConnector connector1 = new StreetConnector(prevStreetsForConnector1, nextStreetsForConnector1);
        ((StreetSection)street2).setNextStreetConnector(connector1);
        roundaboutSink.setPreviousStreetConnector(connector1);

        // initialize route
        IRoute route = new Route();
        route.addSource(source);
        route.addSection(street1);
        route.addSection(intersection);
        route.addSection(street2);
        route.addSection(roundaboutSink);

        routes.put(routeType, route);
    }
}
