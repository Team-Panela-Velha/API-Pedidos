package com.pedidos.api_pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.domain.entity.UserEntity;
import com.pedidos.api_pedidos.repository.TableRepository;
import com.pedidos.api_pedidos.domain.enums.UserRole;
import com.pedidos.api_pedidos.repository.CategoryRepository;
import com.pedidos.api_pedidos.repository.UserRepository;
import com.pedidos.api_pedidos.security.JwtUtil;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityRoutesTests {
    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;
    @Autowired UserRepository users;
    @Autowired CategoryRepository categories;
    @Autowired TableRepository tables;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager entityManager;

    @Test
    void onlyFieldCatalogIsPublic() throws Exception {
        mvc.perform(get("/categories")).andExpect(status().isUnauthorized());
        mvc.perform(get("/products/search")).andExpect(status().isUnauthorized());
        mvc.perform(patch("/tabs/1/close")).andExpect(status().isUnauthorized());
        mvc.perform(post("/orders").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/field/categories")).andExpect(status().isOk());
    }

    @Test
    void administrativeRoutesRequireAuthentication() throws Exception {
        mvc.perform(delete("/products/1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/users")).andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void tableTokenDoesNotGrantAdministrativeAccess() throws Exception {
        TableEntity table = new TableEntity("A1");
        table.setId(1L);
        String token = jwt.generateTableToken(table);
        mvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void authenticatedStaffCanReadAdministrativeCatalog() throws Exception {
        mvc.perform(get("/categories")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void insufficientRoleReturnsForbidden() throws Exception {
        mvc.perform(get("/users")).andExpect(status().isForbidden());
    }

    @Test
    void validManagerTokenSoftDeletesWithAuthenticatedUserId() throws Exception {
        String marker = UUID.randomUUID().toString();
        UserEntity manager = users.save(new UserEntity(
                "Manager", "manager-" + marker + "@example.com", "hash", UserRole.MANAGER));
        CategoryEntity category = categories.save(new CategoryEntity("Category " + marker, null));

        mvc.perform(delete("/categories/{id}", category.getId())
                        .header("Authorization", "Bearer " + jwt.generateUserToken(manager)))
                .andExpect(status().isNoContent());

        CategoryEntity deleted = categories.findById(category.getId()).orElseThrow();
        assertThat(deleted.getDeletedAt()).isNotNull();
        assertThat(deleted.getDeletedBy()).isEqualTo(manager.getId());
        assertThat(categories.findByIdAndDeletedAtIsNull(category.getId())).isEmpty();
    }

    @Test
    void basicCredentialsAuthenticateAndPopulateAuditFields() throws Exception {
        String marker = UUID.randomUUID().toString();
        String email = "basic-" + marker + "@example.com";
        UserEntity manager = users.save(new UserEntity(
                "Manager", email, passwordEncoder.encode("senha-forte"), UserRole.MANAGER));

        String json = mvc.perform(post("/categories")
                        .with(httpBasic(email, "senha-forte"))
                        .contentType("application/json")
                        .content("{\"name\":\"Categoria " + marker + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var response = objectMapper.readTree(json);
        assertThat(response.get("createdAt").isNull()).isFalse();
        assertThat(response.get("createdBy").asLong()).isEqualTo(manager.getId());
        assertThat(response.get("updatedAt").isNull()).isFalse();
        assertThat(response.get("updatedBy").asLong()).isEqualTo(manager.getId());
        assertThat(response.get("deletedAt").isNull()).isTrue();
        assertThat(response.get("deletedBy").isNull()).isTrue();

        Long categoryId = response.get("id").asLong();
        entityManager.flush();
        entityManager.clear();
        CategoryEntity category = categories.findById(categoryId).orElseThrow();
        assertThat(category.getCreatedAt()).isNotNull();
        assertThat(category.getCreatedBy()).isEqualTo(manager.getId());
        assertThat(category.getUpdatedAt()).isNotNull();
        assertThat(category.getUpdatedBy()).isEqualTo(manager.getId());

        mvc.perform(delete("/categories/{id}", categoryId).with(httpBasic(email, "senha-forte")))
                .andExpect(status().isNoContent());
        entityManager.flush();
        entityManager.clear();
        CategoryEntity deleted = categories.findById(categoryId).orElseThrow();
        assertThat(deleted.getDeletedAt()).isNotNull();
        assertThat(deleted.getDeletedBy()).isEqualTo(manager.getId());
    }

    @Test
    void publicTabAndTableResponsesExposeCreationDate() throws Exception {
        String code = "T" + UUID.randomUUID().toString().substring(0, 5);
        tables.save(new TableEntity(code));

        mvc.perform(get("/field/tables/code/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        mvc.perform(post("/field/tabs/start")
                        .contentType("application/json")
                        .content("{\"tableCode\":\"" + code + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.openedAt").isNotEmpty());
    }

    @Test
    void basicCredentialsRejectWrongPasswordAndInsufficientRole() throws Exception {
        String email = "waiter-" + UUID.randomUUID() + "@example.com";
        users.save(new UserEntity("Waiter", email, passwordEncoder.encode("correta"), UserRole.WAITER));

        mvc.perform(get("/categories").with(httpBasic(email, "errada")))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/users").with(httpBasic(email, "correta")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/categories").with(httpBasic(email, "correta")))
                .andExpect(status().isOk());
    }

    @Test
    void openApiDeclaresBearerAndBasicAsAlternativesAndFieldRoutesArePublic() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath("$.security[0].bearerAuth").isArray())
                .andExpect(jsonPath("$.security[1].basicAuth").isArray())
                .andExpect(jsonPath("$.paths['/field/categories'].get.security").isEmpty())
                .andExpect(jsonPath("$.paths['/auth/login'].post.security").isEmpty())
                .andExpect(jsonPath("$.paths['/categories'].get.responses['401']").exists())
                .andExpect(jsonPath("$.paths['/categories'].get.responses['403']").exists());
    }

    @Test
    void openApiResponseSchemasExposeAuditAndPublicCreationFields() throws Exception {
        String json = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var schemas = objectMapper.readTree(json).path("components").path("schemas");

        for (String name : new String[]{"CategoryResponse", "ProductResponse", "ExtraResponse",
                "TableResponse", "UserResponse"}) {
            var properties = schemas.path(name).path("properties");
            for (String field : new String[]{"createdAt", "createdBy", "updatedAt", "updatedBy",
                    "deletedAt", "deletedBy"}) {
                assertThat(properties.has(field)).as(name + "." + field).isTrue();
            }
        }

        for (String name : new String[]{"TabResponse", "OrderResponse", "OrderItemResponse",
                "ProductExtraResponse", "ItemExtraResponse"}) {
            assertThat(schemas.path(name).path("properties").has("createdAt"))
                    .as(name + ".createdAt").isTrue();
        }
    }
}
