package es.cic.curso2025.proy009.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.cic.curso2025.proy009.model.Arbol;
import es.cic.curso2025.proy009.model.Rama;
import es.cic.curso2025.proy009.service.ArbolService;

@RestController
@RequestMapping("/arboles")
public class ArbolController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArbolController.class);

    @Autowired
    private ArbolService arbolService;

    @GetMapping("/{id}")
    public Optional<Arbol> getArbol(@PathVariable Long id) {

        LOGGER.info("Enpoint GET /arboles/id obtener arboles por id");

        Optional<Arbol> arbol = arbolService.getArbol(id);

        return arbol;

    }

    @GetMapping("/ramas/{id}")
    public Optional<Rama> getRama(@PathVariable Long id) {

        LOGGER.info("Enpoint GET /arboles/ramas/id obtener ramas por id");

        Optional<Rama> rama = arbolService.getRama(id);

        return rama;

    }

    @GetMapping
    public List<Arbol> getAllArbol() {

        LOGGER.info("Enpoint GET /arboles obtener todos los árboles");
        List<Arbol> arboles = arbolService.getAllArbol();

        return arboles;

    }

    @GetMapping("/ramas")
    public List<Rama> getAllRama() {

        LOGGER.info("Enpoint GET /arboles obtener todas las ramas");
        List<Rama> ramas = arbolService.getAllRama();

        return ramas;

    }

    @PostMapping
    public Arbol createArbol(@RequestBody Arbol arbol) {
        if (arbol.getId() != null) {
            throw new ModificacionSecurityException("Has tratado de modificar mediante creación");
        }

        LOGGER.info("Enpoint POST /arboles subir árbol a BBDD");
        Arbol arbolCreado = arbolService.createArbol(arbol);

        return arbolCreado;
    }

    @PostMapping("/ramas")
    public Rama createRama(@RequestBody Rama rama) {
        if (rama.getId() != null) {
            throw new ModificacionSecurityException("Has tratado de modificar mediante creación");
        }

        LOGGER.info("Enpoint POST /arboles/ramas subir rama a BBDD");

        Rama ramaCreada = arbolService.createRama(rama);

        return ramaCreada;
    }

    @PutMapping("/{id}")
    public Arbol updateArbol(@PathVariable Long id, @RequestBody Arbol arbolActualizado) {
        LOGGER.info("Endpoint PUT /arboles/{} actualizar árbol en BBDD", id);
        if (arbolActualizado.getId() != null && !arbolActualizado.getId().equals(id)) {
            throw new ModificacionSecurityException("El ID del árbol no coincide con el ID de la URL");
        }
        return arbolService.updateArbol(id, arbolActualizado);
    }

    @PutMapping("/ramas/{id}")
    public Rama updateRama(@PathVariable Long id, @RequestBody Rama ramaActualizada) {
        LOGGER.info("Endpoint PUT /arboles/ramas/{} actualizar rama en BBDD", id);
        if (ramaActualizada.getId() != null && !ramaActualizada.getId().equals(id)) {
            throw new ModificacionSecurityException("El ID de la rama no coincide con el ID de la URL");
        }
        return arbolService.updateRama(id, ramaActualizada);
    }

    @DeleteMapping("/{id}")
    public void deleteArbol(@PathVariable Long id) {

        LOGGER.info("Enpoint DELETE /arboles/id eliminar árbol por id");

        arbolService.deleteArbol(id);
    }

    @DeleteMapping("/ramas/{id}")
    public void deleteRama(@PathVariable Long id) {

        LOGGER.info("Enpoint DELETE /arboles/ramas/id eliminar rama por id");

        arbolService.deleteRama(id);
    }

}
