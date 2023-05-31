package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.enums.DiagramTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Controller
public class GetDiagramTypes {
        @GetMapping("/diagramTypes")
        public ResponseEntity<String[]> getDiagramTypes() {
            ArrayList<String> diagramTypes = new ArrayList<>();
            // sort the diagram types by priority
            List<DiagramTypes> diagramTypesList = Arrays.asList(DiagramTypes.values());
            diagramTypesList.sort(Comparator.comparingInt(DiagramTypes::getPriority));

            for (DiagramTypes diagramType : diagramTypesList) {
                diagramTypes.add(diagramType.toString());
            }

            return ResponseEntity.ok().body(diagramTypes.toArray(new String[0]));
        }
}
