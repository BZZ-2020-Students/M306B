package dev.groupb.m306groupb.model.SDATFile;

import dev.groupb.m306groupb.enums.EconomicActivity;
import dev.groupb.m306groupb.enums.MeasureUnit;
import dev.groupb.m306groupb.model.Resolution;
import lombok.Builder;
import lombok.Data;

import java.util.SortedSet;

@Data
@Builder
public class SDATFile {
    private EconomicActivity economicActivity;
    private Resolution resolution;
    private MeasureUnit measureUnit;
    private SortedSet<Observation> observations;

    public String observationsToCSV() {
        StringBuilder sb = new StringBuilder();
        for (Observation observation : observations) {
            sb.append(observation.getVolume());
            sb.append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
