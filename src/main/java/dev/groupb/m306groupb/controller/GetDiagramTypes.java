package dev.groupb.m306groupb.controller;

import dev.groupb.m306groupb.enums.DiagramTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GetDiagramTypes {
    @GetMapping("/diagramTypes")
    public ResponseEntity<String> getDiagramTypes() {
        return ResponseEntity.ok().body(DiagramTypes.toJson());
    }
}
