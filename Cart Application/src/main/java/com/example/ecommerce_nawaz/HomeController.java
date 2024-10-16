package com.example.ecommerce_nawaz;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService userService() {
        return userService;
    }

    public HomeController setUserService(UserService userService) {
        this.userService = userService;
        return this;
    }

    public UserRepository userRepository() {
        return userRepository;
    }

    // Home page
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartQty", cartItems.size());
        model.addAttribute("total", calculateTotal(cartItems));
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        // Check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "home";
    }

    // Sign-up form
    @GetMapping("/signup")
    public String addUserForm(Model model) {
        return "signup";
    }

    // Process sign-up
    @PostMapping("/signup-v")
    public String saveUser(@ModelAttribute("user") User user, @RequestParam("vpassword") String vpassword,
                           Model model, HttpSession session) {
        if (user.getName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty() || vpassword.isEmpty()) {
            model.addAttribute("error", "All fields are required");
            return "signup";
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("error", "Invalid email format");
            return "signup";
        }
        if (user.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            return "signup";
        }
        if (!user.getPassword().equals(vpassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }

        userService.saveUser(user);
        session.setAttribute("message", "User Created Successfully");
        return "redirect:/login";
    }

    // Login form
    @GetMapping("/login")
    public String index() {
        return "login";
    }

    // Process login
    @PostMapping("/login-v")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, Model model) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            // Redirect admin to add product form, others to home
            if ("admin".equals(user.getType())) {
                return "redirect:/admin/add-product"; // Redirect to add product form
            }
            return "redirect:/"; // Redirect to home for regular users
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    // Add to cart
    @PostMapping("/add")
    public String addToCart(@RequestParam String item, @RequestParam String image, @RequestParam double price, HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        boolean itemExists = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getName().equals(item)) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            cartItems.add(new CartItem(item, image, price));
        }

        return "redirect:/";
    }

    // Remove from cart
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam int index, HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
        }
        return "redirect:/cart";
    }

    // Calculate total cart amount
    private double calculateTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalAmount();
        }
        return total;
    }

    // View cart
    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartQty", cartItems.size());

        double totalAmount = calculateTotal(cartItems);
        DecimalFormat df = new DecimalFormat("#0.00");
        String formattedTotal = df.format(totalAmount);

        model.addAttribute("total", formattedTotal);

        return "cart";
    }
}