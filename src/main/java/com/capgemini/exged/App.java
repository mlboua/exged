package com.capgemini.exged;

import data.Data;
import data.Fold;
import exception.ExgedParserException;
import identifier.csv.CsvIdentifier;
import reader.csv.CsvReader;
import reader.json.JsonConfigReader;
import validator.GenericValidator;
import validator.Reject;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class App {

    public static void main(String... args) {



        try {
            List<CsvIdentifier> identifierList = JsonConfigReader.readJsonIdentifier(new File("config/foldDetection.json")).stream().map(CsvIdentifier::new).collect(Collectors.toList());

            new GenericValidator(JsonConfigReader.readJsonMapperHeaders(new File("config/mappingHeaders.json")));


            CsvReader reader = new CsvReader(true, identifierList);
            Instant b = Instant.now();

            Map<Optional<Reject>, List<Fold>> data;
            //reader.splitFile(new File("test.csv"), new File("testFolder"), 10000, identifierList);
            reader.readFolderParallel(new File("testFolder")) // Read lines to -> List<List<String>>
                    .flatMap(Data::foldStream) // FilesRows -> Rows -> Folds
            ;

            System.out.println(Arrays.stream(Package.getPackages()).filter(aPackage -> aPackage.getName().contains("validator")).findFirst());

            Instant e = Instant.now();
            Duration timeElapsed = Duration.between(b, e);
            System.out.println("elapsed time: "+timeElapsed.toMillis() / 1000f);
        } catch (ExgedParserException | IOException e) {
            e.printStackTrace();
        }
    }
}
