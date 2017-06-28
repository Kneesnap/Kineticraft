package net.kineticraft.lostcity.utils;

/**
 * A combination of both Consumer and Supplier interfaces.
 *
 * @param <R> - Return type
 * @param <P> - Input type
 * Created by Kneesnap on 6/27/2017.
 */
@FunctionalInterface
public interface AdvancedSupplier<R, P> {
    R accept(P input);
}
