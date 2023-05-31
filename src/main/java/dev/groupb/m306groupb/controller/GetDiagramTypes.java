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
                // to string -> to lowercase -> replace underscore with space -> capitalize first letter
                String replace = diagramType.toString().toLowerCase().replace("_", " ");
                diagramTypes.add(replace.substring(0, 1).toUpperCase() + replace.substring(1));
            }

            return ResponseEntity.ok().body(diagramTypes.toArray(new String[0]));
        }
}
