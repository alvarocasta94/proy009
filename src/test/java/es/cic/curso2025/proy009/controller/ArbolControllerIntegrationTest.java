package es.cic.curso2025.proy009.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso2025.proy009.model.Arbol;
import es.cic.curso2025.proy009.model.Rama;
import es.cic.curso2025.proy009.repository.RamaRepository;
import es.cic.curso2025.proy009.repository.ArbolRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ArbolControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArbolRepository arbolRepository;

    @Autowired
    private RamaRepository ramaRepository;

    @AfterEach
    void limpiarBaseDeDatos() {
        ramaRepository.deleteAll();
        arbolRepository.deleteAll();
    }

    @Test
    void testCreate() throws Exception {
        Arbol arbol = new Arbol();
        arbol.setPais("Ucrania");
        arbol.setEdadAnios(422);
        arbol.setDescripcion("Un árbol muy chulo en Ucrania");

        String arbolJson = objectMapper.writeValueAsString(arbol);

        mockMvc.perform(post("/arboles")
                .contentType("application/json")
                .content(arbolJson))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String respuesta = result.getResponse().getContentAsString();
                    Arbol registroCreado = objectMapper.readValue(respuesta, Arbol.class);
                    assertTrue(registroCreado.getId() > 0, "El valor debe ser mayor que 0");

                    Optional<Arbol> registroRealmenteCreado = arbolRepository.findById(registroCreado.getId());
                    assertTrue(registroRealmenteCreado.isPresent());
                });
    }

    @Test
    void testCreateIntentandoModificacion() throws Exception {
        Arbol arbol = new Arbol();
        arbol.setId(3L);
        arbol.setPais("Francia");
        arbol.setEdadAnios(138);
        arbol.setDescripcion("Un árbol muy chulo en París");

        String arbolJson = objectMapper.writeValueAsString(arbol);

        mockMvc.perform(post("/arboles")
                .contentType("application/json")
                .content(arbolJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGet() throws Exception {
        Arbol arbol = new Arbol();
        arbol.setPais("Italia");
        arbol.setEdadAnios(446);
        arbol.setDescripcion("Un árbol muy chulo en Roma");

        arbol = arbolRepository.save(arbol);

        mockMvc.perform(get("/arboles/" + arbol.getId())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Arbol recibido = objectMapper.readValue(json, Arbol.class);
                    assertEquals("Italia", recibido.getPais());
                    assertEquals(446, recibido.getEdadAnios());
                    assertEquals("Un árbol muy chulo en Roma", recibido.getDescripcion());
                });
    }

    @Test
    void testGetAll() throws Exception {
        Arbol arbol1 = new Arbol();
        arbol1.setPais("Alemania");
        arbol1.setEdadAnios(333);
        arbol1.setDescripcion("Un árbol muy chulo en Berlín");

        Arbol arbol2 = new Arbol();
        arbol2.setPais("Portugal");
        arbol2.setEdadAnios(336);
        arbol2.setDescripcion("Un árbol muy chulo en Lisboa");

        arbolRepository.save(arbol1);
        arbolRepository.save(arbol2);

        mockMvc.perform(get("/arboles"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Arbol[] arboles = objectMapper.readValue(json, Arbol[].class);
                    assertEquals(2, arboles.length);
                });
    }

    @Test
    void testUpdate() throws Exception {
        Arbol arbol = new Arbol();
        arbol.setPais("Canadá");
        arbol.setEdadAnios(187);
        arbol.setDescripcion("Un árbol muy chulo en Toronto");
        arbol = arbolRepository.save(arbol);

        Arbol arbolActualizado = new Arbol();
        arbolActualizado.setPais("Eslovaquia");
        arbolActualizado.setEdadAnios(587);
        arbolActualizado.setDescripcion("Un árbol muy chulo en Bratislava");
        String jsonActualizado = objectMapper.writeValueAsString(arbolActualizado);

        mockMvc.perform(put("/arboles/" + arbol.getId())
                .contentType("application/json")
                .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Arbol recibido = objectMapper.readValue(json, Arbol.class);
                    assertEquals("Eslovaquia", recibido.getPais());
                });
    }

    @Test
    void testDelete() throws Exception {
        Arbol arbol = new Arbol();
        arbol.setPais("Colombia");
        arbol.setEdadAnios(189);
        arbol.setDescripcion("Un árbol muy chulo en Cali");

        arbol = arbolRepository.save(arbol);

        Long id = arbol.getId();

        mockMvc.perform(delete("/arboles/" + id))
                .andExpect(status().isOk());

        Optional<Arbol> eliminado = arbolRepository.findById(id);
        assertTrue(eliminado.isEmpty());
    }

    // Nuevo test para crear, obtener, actualizar y eliminar una Rama vinculada a un
    // Arbol guardado
    @Test
    public void testCRUDRamaConArbolExistente() throws Exception {
        // Primero guardar un árbol para usar en la rama
        Arbol arbol = new Arbol();
        arbol.setPais("Andorra");
        arbol.setEdadAnios(194);
        arbol.setDescripcion("Un árbol muy chulo en Andorra");
        Arbol arbolGuardado = arbolRepository.save(arbol);

        // Crear una Rama vinculada al árbol guardado
        Rama rama = new Rama();
        rama.setLongitud(47);
        rama.setNumHojas(13);
        rama.setArbol(arbolGuardado);

        String ramaJson = objectMapper.writeValueAsString(rama);

        // Crear rama
        MvcResult mvcResult = mockMvc.perform(post("/arboles/ramas")
                .contentType("application/json")
                .content(ramaJson))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Rama ramaCreada = objectMapper.readValue(result.getResponse().getContentAsString(), Rama.class);
                    assertNotNull(ramaCreada.getId(), "La rama debe tener ID");
                    assertEquals(47, ramaCreada.getLongitud());
                    assertEquals(13, ramaCreada.getNumHojas());
                    assertNotNull(ramaCreada.getArbol(), "La rama debe tener un árbol asociado");
                    assertEquals(arbolGuardado.getId(), ramaCreada.getArbol().getId());
                })
                .andReturn();

        Rama ramaCreada = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Rama.class);
        Long ramaId = ramaCreada.getId();

        // Obtener rama
        mockMvc.perform(get("/arboles/ramas/" + ramaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Rama obtenida = objectMapper.readValue(result.getResponse().getContentAsString(), Rama.class);
                    assertEquals(ramaId, obtenida.getId());
                    assertEquals(47, obtenida.getLongitud());
                    assertEquals(13, obtenida.getNumHojas());
                });

        // Actualizar rama (ejemplo: cambiar longitud y hojas)
        ramaCreada.setLongitud(50);
        ramaCreada.setNumHojas(15);
        String ramaActualizadaJson = objectMapper.writeValueAsString(ramaCreada);

        mockMvc.perform(put("/arboles/ramas/" + ramaId)
                .contentType("application/json")
                .content(ramaActualizadaJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Rama actualizada = objectMapper.readValue(result.getResponse().getContentAsString(), Rama.class);
                    assertEquals(50, actualizada.getLongitud());
                    assertEquals(15, actualizada.getNumHojas());
                });

        // Eliminar rama
        mockMvc.perform(delete("/arboles/ramas/" + ramaId))
                .andDo(print())
                .andExpect(status().isOk());

        // Verificar que ya no existe la rama
        Optional<Rama> eliminado = ramaRepository.findById(ramaId);
        assertTrue(eliminado.isEmpty(), "La rama debe haber sido eliminada");
    }
}
