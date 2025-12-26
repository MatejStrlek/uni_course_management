package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public String viewUsers(@RequestParam(required = false) String role, Model model) {
        List<User> users;
        if (role != null && !role.isEmpty()) {
            users = userService.getUsersByRole(UserRole.valueOf(role.toUpperCase()));
        } else {
            users = userService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", UserRole.values());
        return "admin/users/list";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        return "admin/users/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute User user,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            userService.updateUser(id, user.getUsername(), user.getFirstName(),
                    user.getLastName(), user.getRole(), user.getPassword(), user.getIsActive());
            redirectAttributes.addFlashAttribute("successMessage", "User '" + user.getUsername() + "' updated successfully!");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userService.getUserById(id));
            model.addAttribute("roles", UserRole.values());
            return "redirect:/admin/users/edit/";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User '" + user.getUsername() + "' deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String role,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            userService.registerUser(username, password, firstName, lastName, UserRole.valueOf(role.toUpperCase()));
            redirectAttributes.addFlashAttribute("successMessage", "User '" + username + "' created successfully!");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/register?error=" + e.getMessage();
        }
    }
}