package com.example.ecommerce_nawaz;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CartController {

    // Existing methods...

    @PostMapping("/checkout")
    public String checkoutSubmit(
            @RequestParam("cardName") String cardName,
            @RequestParam("cardNumber") String cardNumber,
            HttpSession session,  // Get session to manage cart
            Model model) {

        // Validate card details
        if (cardName.isEmpty() || cardNumber.isEmpty()) {
            model.addAttribute("error", "Please provide valid card details.");
            return "cart";  // Show the cart again with error
        }

        // Proceed with payment logic here (assuming successful payment)
        model.addAttribute("success", "Payment successful. Thank you for your purchase!");

        // Clear the cart after successful checkout
        clearCart(session);  // Call the method to clear the cart

        return "order-summary"; // Redirect to the order summary page
    }

    // Method to clear the cart (assuming cart is stored in session)
    private void clearCart(HttpSession session) {
        session.removeAttribute("cartItems");  // Remove cart items from session
        session.setAttribute("total", 0.0);    // Reset the total to zero
    }

    // Example of getting cart items from the session
    private List<CartItem> getCartItems(HttpSession session) {
        return (List<CartItem>) session.getAttribute("cartItems");
    }

    // Dummy method for removing from the cart
    private void removeFromCart(int index, HttpSession session) {
        List<CartItem> cartItems = getCartItems(session);
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
            session.setAttribute("cartItems", cartItems);  // Update the session
        }
    }
}