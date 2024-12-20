// Extract the product ID from the URL
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get("id");

// Fetch product details using the backend API
const apiUrl = `https://elysiumdeploy-production.up.railway.app/api/products/${productId}`;



function addProductClicked() {
  // Get the userId from localStorage (to check if user is logged in)
  let user = localStorage.getItem("userId");

  var title = document.getElementById("product-name").innerHTML
  var quantity = document.getElementById("quantity-selector").value;
  var price = document.getElementById("product-price").innerHTML;
  var productImage =document.getElementById("productImage").src;


  // Create product object
  const product = { title, quantity };
  console.log("Product to be added:", product);

  if (user !== null && user !== undefined) {
    // If the user is logged in, call the backend API to add this product
    console.log("User is logged in, calling API to add item to cart");

    fetch(`https://elysiumdeploy-production.up.railway.app/api/cart/${user}/add`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify([product]) // Send the current product as an array
    })
        .then(response => {
          if (!response.ok) {
            throw new Error("Failed to add item to the user's cart");
          }
          return response.text(); // Return plain text response
        })
        .then(data => {
          console.log("Successfully added to cart:", data);
        })
        .catch(error => {
          console.error("Error adding item to user's cart:", error);
        });

  } else {
    // If the user is not logged in, store the product in localStorage
    console.log("User is not logged in, storing cart in localStorage");

    // Get the existing cartContent from localStorage or create a new array
    let cartContent = JSON.parse(localStorage.getItem("cartContent")) || [];

    // Check if the product already exists in the cart
    const existingProductIndex = cartContent.findIndex(item => item.title === product.title);
    if (existingProductIndex !== -1) {
      // If product exists, increase the quantity as a number
      cartContent[existingProductIndex].quantity += parseInt(quantity);
    } else {
      // Otherwise, add it to the cart
      cartContent.push({ title, quantity, price, productImage });
    }

    // Update localStorage with the new cartContent
    localStorage.setItem("cartContent", JSON.stringify(cartContent));
    console.log("Updated cartContent in localStorage:", cartContent);
  }

  addProductToCart(title, price, productImage, parseInt(quantity));

  updateTotal();
  updateCartCount();
}

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
    document.getElementById("product-image").innerHTML = `<img src="/${product.imageUrl}" alt="${product.name} Image" loading="lazy" width="800" height="1034" id="productImage">`;
    document.getElementById("add-to-cart-section").innerHTML = `<input type="number" value="1" min="1" id="quantity-selector">
          <button class="add-to-cart-btn" onclick="addProductClicked()">
            <ion-icon name="bag-handle-outline" aria-hidden="true"></ion-icon>
            <p>Add to Cart</p>
          </button>`;
  } catch (error) {
    console.error("Error fetching product details:", error);
    document.getElementById("product-details").innerHTML =
      "<p>Unable to load product details. Please try again later.</p>";
  }
}
// var addToCart = document.getElementsByClassName('add-to-cart-btn')
// for (var i = 0; i < addToCart.length; i++) {
//   var button = addToCart[i]
//   button.addEventListener("click", addProductClicked)
// }
// document.querySelectorAll('.add-to-cart-btn').forEach((btn) => {
//   btn.addEventListener('click', addCartClicked);
// });

fetchProductDetails();