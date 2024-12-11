// Extract the product ID from the URL
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get("id");

// Fetch product details using the backend API
const apiUrl = `http://localhost:8080/api/products/${productId}`;

async function fetchProductDetails() {
  try {
    const response = await fetch(apiUrl);
    if (!response.ok) {
      throw new Error("Failed to fetch product details");
    }

    const product = await response.json();

    document.getElementById("product-name").textContent = product.name;
    document.getElementById("product-brand").innerHTML = `<strong>Brand:</strong> <span>${product.brand}</span>`;
    document.getElementById("product-category").innerHTML = `<strong>Category:</strong> <span>${product.category.name}</span>`;
    document.getElementById("product-price").innerHTML = `<strong>Price:</strong> <span>$${product.price.toFixed(2)}</span>`;
    document.getElementById("product-stock").innerHTML = `<strong>Stock Quantity:</strong> <span>${product.stockQuantity}</span>`;
    document.getElementById("product-image").innerHTML = `<img src="/${product.imageUrl}" alt="${product.name} Image" loading="lazy" width="800" height="1034">`;
    document.getElementById("add-to-cart-section").innerHTML = `<input type="number" value="1" min="1">
          <button class="add-to-cart-btn">
            <ion-icon name="bag-handle-outline" aria-hidden="true"></ion-icon>
            <p>Add to Cart</p>
          </button>`;
  } catch (error) {
    console.error("Error fetching product details:", error);
    document.getElementById("product-details").innerHTML =
      "<p>Unable to load product details. Please try again later.</p>";
  }
}

fetchProductDetails();