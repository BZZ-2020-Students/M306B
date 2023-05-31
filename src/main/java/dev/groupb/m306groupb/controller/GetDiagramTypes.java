package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.enums.DiagramTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class GetDiagramTypes {
        @GetMapping("/diagramTypes")
        public ResponseEntity<String[]> getDiagramTypes() {
            ArrayList<String> diagramTypes = new ArrayList<>();
            for (DiagramTypes diagramType : DiagramTypes.values()) {
                diagramTypes.add(diagramType.toString());
            }

            return ResponseEntity.ok().body(diagramTypes.toArray(new String[0]));
        }
}
