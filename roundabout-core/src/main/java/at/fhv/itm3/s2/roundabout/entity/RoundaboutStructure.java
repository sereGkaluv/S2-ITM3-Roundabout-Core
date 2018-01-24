package at.fhv.itm3.s2.roundabout.entity;

import at.fhv.itm3.s2.roundabout.api.entity.*;
import desmoj.core.simulator.Model;

import java.util.*;

public class RoundaboutStructure implements IRoundaboutStructure {
    private final Model model;
    private Set<IStreetConnector> connectors;
    private Set<IRoute> routes;
    private Set<Street> streets;
    private Map<String, String> parameters;
    private Set<AbstractSource> sources;
    private Set<AbstractSink> sinks;

    public RoundaboutStructure(Model model) {
        this.model = model;
        connectors = new HashSet<>();
        routes = new HashSet<>();
        streets = new HashSet<>();
        parameters = new HashMap<>();
        sources = new HashSet<>();
        sinks = new HashSet<>();
    }

    @Override
    public void addStreetConnector(IStreetConnector streetConnector) {
        connectors.add(streetConnector);
    }

    @Override
    public void addRoute(IRoute route) {
        routes.add(route);
    }

    @Override
    public void addParameter(String key, String value) {
        if (!key.isEmpty() && !value.isEmpty() && !parameters.containsKey(key)) {
            parameters.put(key, value);
        }
    }

    @Override
    public void addStreet(Street street) {
        streets.add(street);
    }

    @Override
    public void addSink(AbstractSink sink) {
        sinks.add(sink);
    }

    @Override
    public void addSource(AbstractSource source) {
        sources.add(source);
    }

    @Override
    public Set<IStreetConnector> getStreetConnectors() {
        return Collections.unmodifiableSet(connectors);
    }

    @Override
    public Set<IRoute> getRoutes() {
        return Collections.unmodifiableSet(routes);
    }

    @Override
    public Set<Street> getStreets() {
        return Collections.unmodifiableSet(streets);
    }

    @Override
    public Set<AbstractSink> getSinks() {
        return sinks;
    }

    @Override
    public Set<AbstractSource> getSources() {
        return sources;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public Model getModel() {
        return model;
    }
}
