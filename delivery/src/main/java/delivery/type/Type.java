package delivery.type;

import data.Fold;

import java.io.File;
import java.util.List;

@FunctionalInterface
public interface Type {
    void makeDelivery(Fold fold, List<File> files);
}