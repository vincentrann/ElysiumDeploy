'use strict';

const apiUrl = "https://elysiumdeploy-production.up.railway.app/api/products";
const productList = document.getElementById("product-list");
const searchInput = document.querySelector('input[name="search"]'); // Get the search input field



// Function to reset other filters
function resetOtherFilters(exclude) {
  if (exclude !== "brand") {
    document.getElementById("brand").value = "all";
  }
  if (exclude !== "category") {
    document.getElementById("category").value = "all";
  }
  if (exclude !== "sort") {
    document.getElementById("sort").value = "name-asc";
  }
}

async function fetchFilteredProducts(selectedFilter) {
  const brand = document.getElementById("brand").value;
  const category = document.getElementById("category").value;
  const sort = document.getElementById("sort").value;

  // Reset other filters
  resetOtherFilters(selectedFilter);

  let url = apiUrl;

  // Apply filtering and sorting
  if (selectedFilter === "brand" && brand !== "all") {
    url = `${apiUrl}/search/brand?brand=${brand}`;
  } else if (selectedFilter === "category" && category !== "all") {
    url = `${apiUrl}/filter/category?categoryName=${category}`;
  } else if (selectedFilter === "sort") {
    // Sorting logic
    switch (sort) {
      case "name-asc":
        url = `${apiUrl}/filter/sort/name/asc`;
        break;
      case "name-desc":
        url = `${apiUrl}/filter/sort/name/desc`;
        break;
      case "price-asc":
        url = `${apiUrl}/filter/sort/price/asc`;
        break;
      case "price-desc":
        url = `${apiUrl}/filter/sort/price/desc`;
        break;
      default:
        break;
    }
  } else {
    url = apiUrl; // Default to fetching all products
  }

  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error("Failed to fetch products");
    }
    const products = await response.json();
    updateProductList(products);
  } catch (error) {
    console.error("Error fetching filtered products:", error);
    productList.innerHTML = "<p>Unable to load products. Please try again later.</p>";
  }
}

// Function to search for products by name
async function searchProducts(query) {
  const url = `${apiUrl}/search/name?name=${encodeURIComponent(query)}`;
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error("Failed to fetch products");
    }
    const products = await response.json();
    updateProductList(products);
  } catch (error) {
    console.error("Error fetching products by search:", error);
    productList.innerHTML = "<p>No products found. Please try a different search string.</p>";
  }
}

// Update product list dynamically
function updateProductList(products) {
  productList.innerHTML = ""; // Clear current products

  products.forEach((product) => {
    const productItem = document.createElement("li");
    productItem.innerHTML = `
      <div class="product-card" data-product-id="${product.id}">
        <figure class="card-banner">
          <a href="product.html?id=${product.id}">
            <img src="${product.imageUrl}" alt="${product.name}" loading="lazy" class="w-100">
          </a>
          <div class="card-actions">
            <button class="card-action-btn cart-btn">
              <ion-icon name="bag-handle-outline" aria-hidden="true"></ion-icon>
              <p>Add to Cart</p>
            </button>
          </div>
        </figure>
        <div class="card-content">
          <h3 class="h3 card-title">${product.name}</h3>
          <div class="card-price">
            <data class="item-price" value="${product.price}">$${product.price.toFixed(2)}</data>
          </div>
        </div>
      </div>
    `;
    productList.appendChild(productItem);
  });

  document.querySelectorAll('.cart-btn').forEach((btn) => {
    btn.addEventListener('click', addCartClicked);
  });
}
// Add to Cart Logic
function addCartClicked(event) {
  const button = event.target.closest('.cart-btn');
  const productCard = button.closest('.product-card');

  const title = productCard.querySelector('.card-title').innerText;
  const price = productCard.querySelector('.item-price').innerText;
  const image = productCard.querySelector('img').src;

  addProductToCart(title, price, image);
}

document.getElementById("brand").addEventListener("change", () => fetchFilteredProducts("brand"));
document.getElementById("category").addEventListener("change", () => fetchFilteredProducts("category"));
document.getElementById("sort").addEventListener("change", () => fetchFilteredProducts("sort"));

searchInput.addEventListener("input", (event) => {
  const query = event.target.value.trim();
  if (query) searchProducts(query);
  else fetchFilteredProducts();
});

fetchFilteredProducts();
