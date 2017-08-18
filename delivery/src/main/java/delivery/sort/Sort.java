package delivery.sort;

import data.Fold;

import java.io.File;
import java.util.List;

@FunctionalInterface
public interface Sort {
    void sortFold(Fold fold, List<File> files);
}