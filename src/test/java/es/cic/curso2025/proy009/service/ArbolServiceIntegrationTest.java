package es.cic.curso2025.proy009.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.cic.curso2025.proy009.model.Arbol;
import es.cic.curso2025.proy009.model.Rama;
import es.cic.curso2025.proy009.repository.ArbolRepository;
import es.cic.curso2025.proy009.repository.RamaRepository;

@SpringBootTest
public class ArbolServiceIntegrationTest {

    @Autowired
    private ArbolService arbolService;

    @Autowired
    private ArbolRepository arbolRepository;

    @Autowired
    private RamaRepository ramaRepository;

    @AfterEach
    void limpiarBaseDeDatos() {
        arbolRepository.deleteAll();
        ramaRepository.deleteAll();
    }

    @Test
    void testCreateArbol() {

        Arbol arbol = new Arbol();
        arbol.setPais("Argentina");
        arbol.setEdadAnios(128);
        arbol.setDescripcion("Un arbol muy chulo en la Patagonia");

        Arbol arbolGuardado = arbolService.createArbol(arbol);

        assertNotNull(arbolGuardado.getId(), "El ID no debe ser null tras guardar");
        assertEquals("Argentina", arbolGuardado.getPais());
        assertEquals(128, arbolGuardado.getEdadAnios());
        assertEquals("Un árbol muy chulo en la Patagonia", arbolGuardado.getDescripcion());

        Optional<Arbol> desdeBD = arbolRepository.findById(arbolGuardado.getId());
        assertTrue(desdeBD.isPresent(), "El árbol debe existir en la BBDD");
    }

    @Test
    void testCreateRama() {

        Rama rama = new Rama();
        rama.setLongitud(524);
        rama.setNumHojas(12);

        Rama ramaGuardada = arbolService.createRama(rama);

        assertNotNull(ramaGuardada.getId(), "El ID no debe ser null tras guardar");
        assertEquals(524, ramaGuardada.getLongitud());
        assertEquals(12, ramaGuardada.getNumHojas());

        Optional<Rama> desdeBD = ramaRepository.findById(ramaGuardada.getId());
        assertTrue(desdeBD.isPresent(), "La rama debe existir en la BBDD");
    }

    @Test
    void testGetArbol() {

        Arbol arbol = new Arbol();
        arbol.setPais("Marruecos");
        arbol.setEdadAnios(112);
        arbol.setDescripcion("Un arbol muy chulo en Marrakech");

        Arbol arbolGuardado = arbolService.createArbol(arbol);

        Optional<Arbol> arbolObtenido = arbolService.getArbol(arbolGuardado.getId());

        assertTrue(arbolObtenido.isPresent(), "El árbol debería estar presente");
        assertEquals("Marruecos", arbolObtenido.get().getPais());
        assertEquals(112, arbolObtenido.get().getEdadAnios());
        assertEquals("Un arbol muy chulo en Marrakech", arbolObtenido.get().getDescripcion());
    }

    @Test
    void testGetRama() {

        Rama rama = new Rama();
        rama.setLongitud(324);
        rama.setNumHojas(25);

        Rama ramaGuardada = arbolService.createRama(rama);

        Optional<Rama> ramaObtenida = arbolService.getRama(ramaGuardada.getId());

        assertTrue(ramaObtenida.isPresent(), "El libro debería estar presente");
        assertEquals(324, ramaObtenida.get().getLongitud());
        assertEquals(25, ramaObtenida.get().getNumHojas());
    }

    @Test
    void testGetAllArbol() {
        Arbol arbol1 = new Arbol();
        arbol1.setPais("Kenia");
        Arbol arbol2 = new Arbol();
        arbol2.setPais("Polonia");

        arbolService.createArbol(arbol1);
        arbolService.createArbol(arbol2);

        List<Arbol> lista = arbolService.getAllArbol();

        assertEquals(2, lista.size());
    }

    @Test
    void testGetAllRama() {
        Rama rama1 = new Rama();
        rama1.setNumHojas(85);
        Rama rama2 = new Rama();
        rama2.setNumHojas(37);

        arbolService.createRama(rama1);
        arbolService.createRama(rama2);

        List<Rama> lista = arbolService.getAllRama();

        assertEquals(2, lista.size());
    }

    @Test
    void testUpdateArbol() {
        Arbol arbol = new Arbol();
        arbol.setPais("Camerún");
        arbol.setEdadAnios(327);
        arbol.setDescripcion("Un arbol muy chulo en Camerún");
        Arbol arbolGuardado = arbolService.createArbol(arbol);

        Arbol arbolActualizado = new Arbol();
        arbolActualizado.setPais("Togo");
        arbolActualizado.setEdadAnios(314);
        arbolActualizado.setDescripcion("Un arbol muy chulo en Togo");

        Arbol resultado = arbolService.updateArbol(arbolGuardado.getId(), arbolActualizado);

        assertEquals("Togo", resultado.getPais());
        assertEquals(314, resultado.getEdadAnios());
        assertEquals("Un arbol muy chulo en Togo", resultado.getDescripcion());
    }

    @Test
    void testUpdateRama() {
        Rama rama = new Rama();
        rama.setLongitud(124);
        rama.setNumHojas(8);
        Rama ramaGuardada = arbolService.createRama(rama);

        Rama ramaActualizada = new Rama();
        ramaActualizada.setLongitud(132);
        ramaActualizada.setNumHojas(17);

        Rama resultado = arbolService.updateRama(ramaGuardada.getId(), ramaActualizada);

        assertEquals(132, resultado.getLongitud());
        assertEquals(17, resultado.getNumHojas());
    }

    @Test
    void testUpdateNoExisteArbol() {
        Arbol arbolActualizado = new Arbol();
        arbolActualizado.setPais("Ruanda");

        assertThrows(EntidadNoEncontradaException.class, () -> {
            arbolService.updateArbol(999L, arbolActualizado);
        });
    }


    @Test
    void testUpdateNoExisteRama() {
        Rama ramaActualizada = new Rama();
        ramaActualizada.setLongitud(33);

        assertThrows(EntidadNoEncontradaException.class, () -> {
            arbolService.updateRama(999L, ramaActualizada);
        });
    }

    @Test
    void testDeleteArbol() {
        Arbol arbol = new Arbol();
        arbol.setPais("Madagascar");
        Arbol arbolGuardado = arbolService.createArbol(arbol);

        arbolService.deleteArbol(arbolGuardado.getId());

        Optional<Arbol> eliminado = arbolService.getArbol(arbolGuardado.getId());

        assertFalse(eliminado.isPresent());
    }

    @Test
    void testDeleteRama() {
        Rama rama = new Rama();
        rama.setNumHojas(84);
        Rama ramaGuardada = arbolService.createRama(rama);

        arbolService.deleteRama(ramaGuardada.getId());

        Optional<Rama> eliminado = arbolService.getRama(ramaGuardada.getId());

        assertFalse(eliminado.isPresent());
    }

    @Test
    void testDeleteNoExisteArbol() {
        assertThrows(EntidadNoEncontradaException.class, () -> {
            arbolService.deleteArbol(999L);
        });
    }

     @Test
    void testDeleteNoExisteRama() {
        assertThrows(EntidadNoEncontradaException.class, () -> {
            arbolService.deleteRama(999L);
        });
    }

}
