package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import py.edu.uc.jpasseratplp32025.dto.SaludoDto;

@RestController
public class IndexController {

    @GetMapping("/")
    public RedirectView redirectToHolaMundo() {
        return new RedirectView("/HolaMundo");
    }

    @GetMapping("/HolaMundo")
    public SaludoDto holaMundo(@RequestParam(name = "nombre", required = false, defaultValue = "Mundo") String nombre) {
        String mensaje = "¡Hola " + nombre + "!";
        return new SaludoDto(mensaje);
    }
}