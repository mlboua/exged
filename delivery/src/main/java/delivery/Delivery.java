package delivery;

import data.Fold;

import java.io.File;

@FunctionalInterface
public interface Delivery {
    void makeDelivery(Fold fold, File file);
}