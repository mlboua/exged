package reader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.json.mapping.headers.MappingFold;
import exception.ExgedParserException;
import identifier.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonConfigReader {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }


    private JsonConfigReader() {
    }

    public static List<Identifier> readJsonIdentifier(File file) throws IOException, ExgedParserException {

        List<Identifier> identifierUnorderedList = new ArrayList<>(Arrays.asList(mapper.readValue(file, Identifier[].class)));

        if (identifierUnorderedList.stream().filter(identifier -> !identifier.getReplacedBy().isPresent()).count() > 1) {
            throw new ExgedParserException("Plusieurs identifiants n'ont pas de valeur de remplacement, il faut qu'un seul identifiant qui ne soit pas remplacé");
        }

        List<Identifier> reversedList = new ArrayList<>();
        Optional<Identifier> lastIdentifier = identifierUnorderedList.stream().filter(identifier -> !identifier.getReplacedBy().isPresent()).findFirst();
        if (lastIdentifier.isPresent()) {
            reversedList.add(lastIdentifier.get());
            identifierUnorderedList.forEach(identifier -> identifierUnorderedList.stream()
                    .filter(identifierSearch ->
                            identifierSearch.getReplacedBy().isPresent()
                                    && reversedList.get(reversedList.size()-1).getName().equals(identifierSearch.getReplacedBy().get()))
                    .findFirst()
                    .ifPresent(reversedList::add)
            );
        } else {
            throw new ExgedParserException("Aucun identifiant sans remplacant trouvé, il faut un identifiant sans remplaçant");
        }
        Collections.reverse(reversedList);
        return reversedList;
    }

    public static void readJsonMapperHeaders(File file) throws IOException {
        MappingFold mappingFold = mapper.readValue(file, MappingFold.class);
        System.out.println(mappingFold);

    }
}
