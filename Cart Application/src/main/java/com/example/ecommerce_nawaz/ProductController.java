package com.example.ecommerce_nawaz;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/")
public class ProductController {

    @Autowired
    private ProductService productService;

    private static final String UPLOAD_DIR = "src/main/resources/static/";

    // Show all products
    @GetMapping("products")
    public String getAllProducts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getType().equals("admin")) {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("user", user);
            return "product-list";
        } else {
            return "redirect:/";  // Redirect to home or login if not admin
        }
    }

    // Form to add a new product
    @GetMapping("add-product")
    public String addProductForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getType().equals("admin")) {
            model.addAttribute("product", null);  // Set product to null for adding a new product
            model.addAttribute("user", user);
            return "add-product";  // Return the add-product.html form
        } else {
            return "redirect:/";  // Redirect to home or login if not admin
        }
    }

    // Add a new product
    @PostMapping("add-product")
    public String addProduct(@RequestParam String productName, @RequestParam String description,
                             @RequestParam double price, @RequestParam String category,
                             @RequestParam MultipartFile imageFile, HttpSession session,
                             Model model) throws IOException {

        User user = (User) session.getAttribute("user");
        if (user != null && user.getType().equals("admin")) {
            // Save the image
            String imageName = imageFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + imageName);
            Files.createDirectories(path.getParent());
            Files.write(path, imageFile.getBytes());

            // Create and save the new product
            Product product = new Product();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setImage(imageName);

            productService.saveProduct(product);  // Save product in the database

            model.addAttribute("user", user);
            return "redirect:/admin/products";  // Redirect to product list after saving
        } else {
            return "redirect:/";  // Redirect to home or login if not admin
        }
    }

    // Form to update a product (similar to add form but with pre-filled fields)
    @GetMapping("update-product")
    public String updateProductForm(@RequestParam Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getType().equals("admin")) {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                model.addAttribute("product", product.get());
                model.addAttribute("user", user);
                return "update-product";  // Return the update-product.html form
            }
        }
        return "redirect:/";  // Redirect if not admin or product not found
    }

    // Update product
    @PostMapping("update-product")
    public String updateProduct(@RequestParam Long id, @RequestParam String productName,
                                @RequestParam String description, @RequestParam double price,
                                @RequestParam String category, @RequestParam MultipartFile imageFile,
                                HttpSession session) throws IOException {

        User user = (User) session.getAttribute("user");
        if (user != null && user.getType().equals("admin")) {
            Optional<Product> optionalProduct = productService.getProductById(id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();

                product.setProductName(productName);
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);

                if (!imageFile.isEmpty()) {  // Update the image only if a new one is uploaded
                    String imageName = imageFile.getOriginalFilename();
                    Path path = Paths.get(UPLOAD_DIR + imageName);
                    Files.createDirectories(path.getParent());
                    Files.write(path, imageFile.getBytes());
                    product.setImage(imageName);
                }

                productService.saveProduct(product);  // Save updated product in the database
            }
            return "redirect:/admin/products";  // Redirect to product list after updating
        }
        return "redirect:/";  // Redirect if not admin
    }

    // Delete product
    @PostMapping("delete-product")
    public String deleteProduct(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getType().equals("admin")) {
            productService.deleteProduct(id);  // Delete product by id
        }
        return "redirect:/admin/products";  // Redirect to product list after deletion
    }
}