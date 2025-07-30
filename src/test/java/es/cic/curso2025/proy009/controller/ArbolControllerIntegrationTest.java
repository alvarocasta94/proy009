package es.cic.curso2025.proy009.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso2025.proy009.model.Arbol;
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
        arbolRepository.deleteAll();
        ramaRepository.deleteAll();
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

                    Optional<Arbol> registroRealmenteCreado = arbolRepository
                            .findById(registroCreado.getId());
                    assertTrue(registroRealmenteCreado.isPresent());

                });

    }

    @Test
    void testCreateIntentandoModificacion() throws Exception {

        Arbol arbol = new Arbol();
        arbol.setId((long) 3);
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
        // 1. Crear el arbol
        Arbol arbol = new Arbol();
        arbol.setPais("Italia");
        arbol.setEdadAnios(446);
        arbol.setDescripcion("Un árbol muy chulo en Roma");

        // 2. Guardar el arbol en la BD
        arbol = arbolRepository.save(arbol);

        // 3. Simular la solicitud get
        // 3.1. Realizar la solicitud HTTP GET
        mockMvc.perform(get("/arboles/" + arbol.getId())
                .contentType("application/json"))
                // Validar el estado HTTP
                .andExpect(status().isOk())
                // Validar el contenido del JSON
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
        // Prepara datos
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

        // Petición GET
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
        // 1. Guardar arbol inicial
        Arbol arbol = new Arbol();
        arbol.setPais("Canadá");
        arbol.setEdadAnios(187);
        arbol.setDescripcion("Un árbol muy chulo en Toronto");
        arbol = arbolRepository.save(arbol);

        // 2. Crear arbol actualizado
        Arbol arbolActualizado = new Arbol();
        arbolActualizado.setPais("Eslovaquia");
        arbolActualizado.setEdadAnios(587);
        arbolActualizado.setDescripcion("Un árbol muy chulo en Bratislava");
        String jsonActualizado = objectMapper.writeValueAsString(arbolActualizado);

        // 3. Hacer PUT
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
        // 1. Crear y guardar un libro
        Arbol arbol = new Arbol();
        arbol.setPais("Colombia");
        arbol.setEdadAnios(189);
        arbol.setDescripcion("Un árbol muy chulo en Cali");
        

        arbol = arbolRepository.save(arbol);

        Long id = arbol.getId();

        // 2. Realizar la solicitud DELETE
        mockMvc.perform(delete("/arboles/" + id))
                .andExpect(status().isOk());

        // 3. Verificar que ya no existe en la base de datos
        Optional<Arbol> eliminado = arbolRepository.findById(id);
        assertTrue(eliminado.isEmpty()); // Ya no debería estar presente
    }

}
