package es.cic.curso2025.proy009.uc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso2025.proy009.model.Arbol;
import es.cic.curso2025.proy009.model.Rama;

@SpringBootTest
@AutoConfigureMockMvc
public class SeCreaRamaDesdeArbolIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testEstablecerRelacionLibroEditorial() throws Exception {
        /**
         * Creo primero una editorial
         */
       
        Arbol arbol = new Arbol();
        arbol.setPais("Andorra");
        arbol.setEdadAnios(194);
        arbol.setDescripcion("Un árbol muy chulo en Andorra");

        Rama ramaTest = new Rama();
        ramaTest.setLongitud(47);
        ramaTest.setNumHojas(13);

        ramaTest.setArbol(arbol);


        //convertimos el objeto de tipo Libro en json con ObjectMapper
        String ramaACrearJson = objectMapper.writeValueAsString(ramaTest);
        
        
        //con MockMvc simulamos la peticion HTTP para crear una editorial
        MvcResult mvcResult = mockMvc.perform(post("/arboles/ramas")
        .contentType("application/json")
        .content(ramaACrearJson))
        .andExpect(status().isOk())
        .andExpect( arbolResult ->{
            assertNotNull(
                objectMapper.readValue(
                    arbolResult.getResponse().getContentAsString(), Arbol.class), 
                "Se ha creado la relación entre rama y árbol");
            })
        .andReturn();


        Rama ramaCreada = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Rama.class);
        Long id = ramaCreada.getId();

        mockMvc.perform(get("/ramas/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), Rama.class).getId(),
                            id);
                });   
         
                
        ramaCreada.getArbol().setPais("Congo");


        String ramaJson = objectMapper.writeValueAsString(ramaCreada);

        mockMvc.perform(put("/ramas/" + ramaCreada.getId())
                .contentType("application/json")
                .content(ramaJson))
                .andDo(print())                
                .andExpect(status().isOk());





        mockMvc.perform(delete("/ramas/" + id))
                .andDo(print())        
                .andExpect(status().isOk());                
    }
}
