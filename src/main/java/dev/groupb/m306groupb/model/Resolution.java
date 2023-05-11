package dev.groupb.m306groupb.model;

import dev.groupb.m306groupb.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resolution {
    private Unit timeUnit;
    private int resolution;
}
