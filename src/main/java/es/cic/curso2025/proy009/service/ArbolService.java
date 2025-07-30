package es.cic.curso2025.proy009.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.cic.curso2025.proy009.model.Arbol;
import es.cic.curso2025.proy009.repository.ArbolRepository;
import es.cic.curso2025.proy009.model.Rama;
import es.cic.curso2025.proy009.repository.RamaRepository;

@Service
@Transactional
public class ArbolService {
    @Autowired
    private ArbolRepository arbolRepository;
    @Autowired
    private RamaRepository ramaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArbolService.class);

    @Transactional(readOnly = true)
    public Optional<Arbol> getArbol(Long id) {

        LOGGER.info(String.format("Leído el arbol con id %s", id));

        Optional<Arbol> arbol = arbolRepository.findById(id);

        return arbol;
    }

    @Transactional(readOnly = true)
    public Optional<Rama> getRama(Long id) {

        LOGGER.info(String.format("Leído la rama con id %s", id));

        Optional<Rama> rama = ramaRepository.findById(id);

        return rama;
    }

    @Transactional(readOnly = true)
    public List<Arbol> getAllArbol() {

        LOGGER.info("Obteniendo todos los árboles desde la base de datos");
        return arbolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rama> getAllRama() {

        LOGGER.info("Obteniendo todas las ramas desde la base de datos");
        return ramaRepository.findAll();
    }

    public Arbol createArbol(Arbol arbol) {

        LOGGER.info(String.format("Creación del árbol con id %s", arbol.getId()));

        arbolRepository.save(arbol);

        return arbol;

    }

    public Rama createRama(Rama rama) {

        LOGGER.info(String.format("Creación de la rama con id %s", rama.getId()));

        ramaRepository.save(rama);

        return rama;

    }

    public Arbol updateArbol(Long id, Arbol arbolActualizado) {
        LOGGER.info(String.format("Actualizando el árbol con id %d", id));

        Arbol existente = arbolRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Árbol no encontrado con ID: " + id));

        // Aquí ignoras el arbolActualizado.getId() y actualizas solo los campos
        // necesarios
        existente.setPais(arbolActualizado.getPais());
        existente.setEdadAnios(arbolActualizado.getEdadAnios());
        existente.setDescripcion(arbolActualizado.getDescripcion());
        existente.setRamas(arbolActualizado.getRamas());

        return arbolRepository.save(existente);
    }

    public Rama updateRama(Long id, Rama ramaActualizada) {
        LOGGER.info(String.format("Actualizando la rama con id %d", id));

        Rama existente = ramaRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Rama no encontrada con ID: " + id));

        // Aquí ignoras el ramaActualizada.getId() y actualizas solo los campos
        // necesarios
        existente.setLongitud(ramaActualizada.getLongitud());
        existente.setNumHojas(ramaActualizada.getNumHojas());
        existente.setArbol(ramaActualizada.getArbol());

        return ramaRepository.save(existente);
    }

    public void deleteArbol(Long id) {

        LOGGER.info(String.format("Eliminación del árbol con id %s", id));

        if (!arbolRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("No se puede eliminar: árbol no encontrado con ID: " + id);
        }

        arbolRepository.deleteById(id);

    }
    
    public void deleteRama(Long id) {

        LOGGER.info(String.format("Eliminación de la rama con id %s", id));

        if (!ramaRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("No se puede eliminar: rama no encontrada con ID: " + id);
        }

        ramaRepository.deleteById(id);

    }

}
