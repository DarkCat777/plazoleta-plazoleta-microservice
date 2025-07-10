package com.pragma.plazoleta.restaurant.init;

import com.pragma.plazoleta.dish.infrastructure.adapter.output.model.JpaCategoryEntity;
import com.pragma.plazoleta.dish.infrastructure.adapter.output.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CategoryDataInitializer implements CommandLineRunner {

    private final JpaCategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<JpaCategoryEntity> categories = List.of(
                createCategory( "Entrada", "Platos ligeros servidos al inicio de la comida"),
                createCategory( "Plato principal", "Plato fuerte que representa el núcleo de la comida"),
                createCategory( "Postre", "Platos dulces que se sirven al final"),
                createCategory( "Bebida", "Bebidas frías o calientes para acompañar la comida"),
                createCategory( "Ensalada", "Combinaciones de vegetales frescos, a veces con proteína"),
                createCategory( "Sopa", "Preparaciones líquidas calientes, con vegetales o carne"),
                createCategory( "Guarnición", "Acompañamientos como papas o arroz"),
                createCategory( "Sándwich", "Ingredientes fríos o calientes entre panes"),
                createCategory( "Pasta", "Platos con fideos, como espagueti o lasaña"),
                createCategory( "Vegetariano", "Platos sin carne, para vegetarianos"),
                createCategory( "Vegano", "Platos sin productos de origen animal"),
                createCategory( "Mariscos", "Platos con pescados y mariscos")
            );
            categoryRepository.saveAll(categories);
        }
    }

    private JpaCategoryEntity createCategory(String name, String description) {
        JpaCategoryEntity category = new JpaCategoryEntity();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}
