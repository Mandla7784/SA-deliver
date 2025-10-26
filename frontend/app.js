// Global variables
let currentUser = null;
let sessionToken = null;
const API_BASE_URL = "http://localhost:8080/api";

// DOM elements
const hamburger = document.getElementById("hamburger");
const navMenu = document.getElementById("nav-menu");
const navAuth = document.getElementById("nav-auth");
const navUser = document.getElementById("nav-user");
const welcomeUser = document.getElementById("welcome-user");

// Initialize the application
document.addEventListener("DOMContentLoaded", function () {
  initializeApp();
  setupEventListeners();
  loadProducts();
  loadCategories();
});

function initializeApp() {
  // Check for existing session
  const savedToken = localStorage.getItem("sessionToken");
  const savedUser = localStorage.getItem("currentUser");

  if (savedToken && savedUser) {
    sessionToken = savedToken;
    currentUser = JSON.parse(savedUser);
    updateUIForLoggedInUser();
  }
}

function setupEventListeners() {
  // Mobile menu toggle
  hamburger.addEventListener("click", toggleMobileMenu);

  // Form submissions
  document.getElementById("login-form").addEventListener("submit", handleLogin);
  document
    .getElementById("register-form")
    .addEventListener("submit", handleRegister);
  document
    .getElementById("update-profile-form")
    .addEventListener("submit", handleUpdateProfile);

  // Close modals when clicking outside
  window.addEventListener("click", function (event) {
    if (event.target.classList.contains("modal")) {
      event.target.style.display = "none";
    }
  });
}

function toggleMobileMenu() {
  hamburger.classList.toggle("active");
  navMenu.classList.toggle("active");
}

// Navigation functions
function showSection(sectionId) {
  // Hide all sections
  document.querySelectorAll(".section").forEach((section) => {
    section.classList.remove("active");
  });

  // Show selected section
  document.getElementById(sectionId).classList.add("active");

  // Close mobile menu if open
  hamburger.classList.remove("active");
  navMenu.classList.remove("active");
}

// Authentication functions
function showLoginModal() {
  document.getElementById("login-modal").style.display = "block";
}

function showRegisterModal() {
  document.getElementById("register-modal").style.display = "block";
}

function showUpdateProfileModal() {
  document.getElementById("update-profile-modal").style.display = "block";
}

function closeModal(modalId) {
  document.getElementById(modalId).style.display = "none";
}

async function handleLogin(event) {
  event.preventDefault();

  const username = document.getElementById("login-username").value;
  const password = document.getElementById("login-password").value;

  try {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    });

    const result = await response.json();

    if (result.success) {
      sessionToken = result.data.sessionToken;
      currentUser = { username: result.data.username };

      // Save to localStorage
      localStorage.setItem("sessionToken", sessionToken);
      localStorage.setItem("currentUser", JSON.stringify(currentUser));

      updateUIForLoggedInUser();
      closeModal("login-modal");
      showToast("Login successful!", "success");

      // Clear form
      document.getElementById("login-form").reset();
    } else {
      showToast(result.message, "error");
    }
  } catch (error) {
    showToast("Login failed. Please try again.", "error");
    console.error("Login error:", error);
  }
}

async function handleRegister(event) {
  event.preventDefault();

  const username = document.getElementById("register-username").value;
  const password = document.getElementById("register-password").value;
  const email = document.getElementById("register-email").value;

  try {
    const response = await fetch(`${API_BASE_URL}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password, email }),
    });

    const result = await response.json();

    if (result.success) {
      closeModal("register-modal");
      showToast("Registration successful! Please login.", "success");

      // Clear form
      document.getElementById("register-form").reset();

      // Show login modal
      setTimeout(() => showLoginModal(), 1000);
    } else {
      showToast(result.message, "error");
    }
  } catch (error) {
    showToast("Registration failed. Please try again.", "error");
    console.error("Registration error:", error);
  }
}

async function logout() {
  try {
    if (sessionToken) {
      await fetch(`${API_BASE_URL}/logout`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${sessionToken}`,
        },
      });
    }
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    // Clear session data
    sessionToken = null;
    currentUser = null;
    localStorage.removeItem("sessionToken");
    localStorage.removeItem("currentUser");

    updateUIForLoggedOutUser();
    showSection("home");
    showToast("Logged out successfully", "info");
  }
}

function updateUIForLoggedInUser() {
  navAuth.style.display = "none";
  navUser.style.display = "flex";
  welcomeUser.textContent = `Welcome, ${currentUser.username}!`;
}

function updateUIForLoggedOutUser() {
  navAuth.style.display = "flex";
  navUser.style.display = "none";
}

// Product functions
async function loadProducts() {
  const productsGrid = document.getElementById("products-grid");
  const loading = document.getElementById("products-loading");

  loading.style.display = "block";
  productsGrid.innerHTML = "";

  try {
    const response = await fetch(`${API_BASE_URL}/products`);
    const result = await response.json();

    if (result.success) {
      displayProducts(result.data);
    } else {
      showToast("Failed to load products", "error");
    }
  } catch (error) {
    showToast("Failed to load products", "error");
    console.error("Load products error:", error);
  } finally {
    loading.style.display = "none";
  }
}

function displayProducts(products) {
  const productsGrid = document.getElementById("products-grid");

  if (products.length === 0) {
    productsGrid.innerHTML = '<p class="text-center">No products found.</p>';
    return;
  }

  productsGrid.innerHTML = products
    .map(
      (product) => `
        <div class="product-card">
            <div class="product-image">
                <i class="fas fa-box"></i>
            </div>
            <div class="product-info">
                <h3 class="product-name">${product.name}</h3>
                <p class="product-description">${product.description}</p>
                <div class="product-price">$${product.price}</div>
                <div class="product-stock">Stock: ${product.stock}</div>
                <div class="product-category">${product.category}</div>
            </div>
        </div>
    `
    )
    .join("");
}

async function searchProducts() {
  const searchInput = document.getElementById("search-input");
  const query = searchInput.value.trim();

  if (query.length < 2) {
    loadProducts();
    return;
  }

  const productsGrid = document.getElementById("products-grid");
  const loading = document.getElementById("products-loading");

  loading.style.display = "block";
  productsGrid.innerHTML = "";

  try {
    const response = await fetch(
      `${API_BASE_URL}/products/search/${encodeURIComponent(query)}`
    );
    const result = await response.json();

    if (result.success) {
      displayProducts(result.data);
    } else {
      showToast("Search failed", "error");
    }
  } catch (error) {
    showToast("Search failed", "error");
    console.error("Search error:", error);
  } finally {
    loading.style.display = "none";
  }
}

// Category functions
async function loadCategories() {
  const categoriesGrid = document.getElementById("categories-grid");

  try {
    const response = await fetch(`${API_BASE_URL}/categories`);
    const result = await response.json();

    if (result.success) {
      displayCategories(result.data);
    } else {
      showToast("Failed to load categories", "error");
    }
  } catch (error) {
    showToast("Failed to load categories", "error");
    console.error("Load categories error:", error);
  }
}

function displayCategories(categories) {
  const categoriesGrid = document.getElementById("categories-grid");

  if (categories.length === 0) {
    categoriesGrid.innerHTML =
      '<p class="text-center">No categories found.</p>';
    return;
  }

  categoriesGrid.innerHTML = categories
    .map(
      (category) => `
        <div class="category-card" onclick="filterByCategory('${category}')">
            <h3>${category}</h3>
            <p>Browse products in this category</p>
        </div>
    `
    )
    .join("");
}

async function filterByCategory(category) {
  const productsGrid = document.getElementById("products-grid");
  const loading = document.getElementById("products-loading");

  loading.style.display = "block";
  productsGrid.innerHTML = "";

  try {
    const response = await fetch(
      `${API_BASE_URL}/products/category/${encodeURIComponent(category)}`
    );
    const result = await response.json();

    if (result.success) {
      displayProducts(result.data);
      showSection("products");
    } else {
      showToast("Failed to load products for this category", "error");
    }
  } catch (error) {
    showToast("Failed to load products for this category", "error");
    console.error("Filter by category error:", error);
  } finally {
    loading.style.display = "none";
  }
}

// Profile functions
async function showProfile() {
  if (!currentUser) {
    showToast("Please login to view your profile", "warning");
    return;
  }

  showSection("profile");
  await loadProfile();
}

async function loadProfile() {
  const profileInfo = document.getElementById("profile-info");

  try {
    const response = await fetch(`${API_BASE_URL}/profile`, {
      headers: {
        Authorization: `Bearer ${sessionToken}`,
      },
    });

    const result = await response.json();

    if (result.success) {
      const user = result.data;
      profileInfo.innerHTML = `
                <div class="profile-field">
                    <strong>Username:</strong>
                    <span>${user.username}</span>
                </div>
                <div class="profile-field">
                    <strong>Email:</strong>
                    <span>${user.email || "Not provided"}</span>
                </div>
                <div class="profile-field">
                    <strong>Status:</strong>
                    <span>${user.active ? "Active" : "Inactive"}</span>
                </div>
            `;
    } else {
      showToast("Failed to load profile", "error");
    }
  } catch (error) {
    showToast("Failed to load profile", "error");
    console.error("Load profile error:", error);
  }
}

async function handleUpdateProfile(event) {
  event.preventDefault();

  const newPassword = document.getElementById("update-password").value;

  try {
    const response = await fetch(`${API_BASE_URL}/profile`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${sessionToken}`,
      },
      body: JSON.stringify({ password: newPassword }),
    });

    const result = await response.json();

    if (result.success) {
      closeModal("update-profile-modal");
      showToast("Profile updated successfully", "success");

      // Clear form
      document.getElementById("update-profile-form").reset();
    } else {
      showToast(result.message, "error");
    }
  } catch (error) {
    showToast("Failed to update profile", "error");
    console.error("Update profile error:", error);
  }
}

async function deleteProfile() {
  if (
    !confirm(
      "Are you sure you want to delete your account? This action cannot be undone."
    )
  ) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/profile`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${sessionToken}`,
      },
    });

    const result = await response.json();

    if (result.success) {
      showToast("Account deleted successfully", "success");
      logout();
    } else {
      showToast(result.message, "error");
    }
  } catch (error) {
    showToast("Failed to delete account", "error");
    console.error("Delete profile error:", error);
  }
}

// Utility functions
function showToast(message, type = "info") {
  const toastContainer = document.getElementById("toast-container");
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.textContent = message;

  toastContainer.appendChild(toast);

  // Auto remove after 5 seconds
  setTimeout(() => {
    toast.remove();
  }, 5000);
}

// Helper function to make authenticated requests
async function makeAuthenticatedRequest(url, options = {}) {
  if (!sessionToken) {
    throw new Error("No session token available");
  }

  const defaultOptions = {
    headers: {
      Authorization: `Bearer ${sessionToken}`,
      "Content-Type": "application/json",
    },
  };

  const mergedOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers,
    },
  };

  return fetch(url, mergedOptions);
}
