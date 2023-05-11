package dev.groupb.m306groupb.model.SDATFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Observation implements Comparable<Observation> {
    private int position;
    private float volume;

    @Override
    public int compareTo(Observation o) {
        return Integer.compare(this.position, o.position);
    }
}
