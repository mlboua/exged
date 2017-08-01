package config.json.mapping.headers;

import config.json.mapping.headers.multiHeader.MultiHeaders;
import config.json.mapping.headers.uniqueHeader.UniqueHeader;

import java.util.List;

public class MappingHeaders {

    private List<MultiHeaders> multiHeaders;
    private List<UniqueHeader> uniqueHeader;

    public MappingHeaders() {
    }

    public List<MultiHeaders> getMultiHeaders() {
        return multiHeaders;
    }

    public void setMultiHeaders(List<MultiHeaders> multiHeaders) {
        this.multiHeaders = multiHeaders;
    }

    public List<UniqueHeader> getUniqueHeader() {
        return uniqueHeader;
    }

    public void setUniqueHeader(List<UniqueHeader> uniqueHeader) {
        this.uniqueHeader = uniqueHeader;
    }

    @Override
    public String toString() {
        return "MappingFold{" +
                "multiHeaders=" + multiHeaders +
                ", uniqueHeader=" + uniqueHeader +
                '}';
    }
}
