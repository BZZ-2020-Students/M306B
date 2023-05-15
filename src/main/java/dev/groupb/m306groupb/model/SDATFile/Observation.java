package dev.groupb.m306groupb.model.SDATFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Observation implements Comparable<Observation> {
    private Integer position;
    private Double volume;

    @Override
    public int compareTo(Observation o) {
        return Integer.compare(this.position, o.position);
    }
}
