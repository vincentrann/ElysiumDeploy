'use strict';

/**
 * add active class on header when scrolled 200px from top
 */

const header = document.querySelector("[data-header]");

window.addEventListener("scroll", function () {
  window.scrollY >= 200 ? header.classList.add("active")
      : header.classList.remove("active");
})

/**
 * cart
 */

let cartButton = document.querySelector('.cart-button')
let cart = document.querySelector('.cart')
let closeCartButton = document.querySelector('.close-cart')

// Open Cart Sidebar
cartButton.onclick = () => {
  cart.classList.add('active');
}

// Close Cart Sidebar
closeCartButton.onclick = () => {
  cart.classList.remove('active');
}

// Cart Logic
if (document.readyState == 'loading') {
  document.addEventListener('DOMContentLoaded', ready);
} else {
  ready();
}

function ready() {
  loadCart()
  var removeCartButtons = document.getElementsByClassName('cart-remove');
  for (var i = 0; i < removeCartButtons.length; i++) {
    var button = removeCartButtons[i];
    button.addEventListener('click', removeCartItem)
  }

  var quantityInputs = document.getElementsByClassName('cart-quantity')
  for (var i = 0; i < quantityInputs.length; i++) {
    var input = quantityInputs[i]
    input.addEventListener('change', quantityChanged);
  }

  var addToCart = document.getElementsByClassName('cart-btn')
  for (var i = 0; i < addToCart.length; i++) {
    var button = addToCart[i]
    button.addEventListener("click", addCartClicked)
  }
  var addToCart_product = document.getElementsByClassName('add-to-cart-btn')
  for (var i = 0; i < addToCart_product.length; i++) {
    console.log("inside loop")
    var button = addToCart_product[i]
    button.addEventListener("click", addCartClicked)
  }

  document.getElementsByClassName('btn-buy')[0].addEventListener("click", checkoutButtonClicked)
}

function checkoutButtonClicked() {

  let user = localStorage.getItem("userId")
  if(user == null || user == undefined )
  {

    var cartContentDev = document.getElementsByClassName('cart-box')
    var cartContent = [];
    for (var i = 0; i < cartContentDev.length; i++)
    {
      let title = cartContentDev[i].getElementsByClassName('cart-product-title')[0].innerHTML
      let quantity = cartContentDev[i].getElementsByClassName('cart-quantity')[0].value
      let item = { title,quantity}
      console.log(item)
      cartContent.push(item)
    }
    alert(JSON.stringify(cartContent))
    localStorage.setItem("cartContent",JSON.stringify(cartContent))
    window.location.replace("pages/login.html")
  }
  else
  {
    window.location.replace("pages/checkout.html")
  }


  // emptying cart
  // var cartContent = document.getElementsByClassName('cart-content')[0]
  // while (cartContent.hasChildNodes()) {
  //   cartContent.removeChild(cartContent.firstChild)
  // }
  // updateTotal();
  // updateCartCount();
}

function loadCart() {
  console.log("Start loading cart...");

  // Clear any existing cart items in the UI
  document.getElementsByClassName('cart-content')[0].innerHTML = '';

  // Check if user is logged in
  let user = localStorage.getItem("userId");

  if (user !== null && user !== undefined) {
    // User is logged in, fetch cart items from the backend
    console.log("User is logged in, fetching cart items from API...");

    fetch(`https://elysiumdeploy-production.up.railway.app/api/cart/${user}`)
        .then((response) => {
          if (!response.ok) {
            throw new Error("Failed to load user's cart from the backend");
          }
          return response.json();
        })
        .then((cartItems) => {
          console.log("Response from backend:", cartItems);

          // Check if the response is an array
          if (!Array.isArray(cartItems)) {
            throw new Error("Invalid response: Expected an array of cart items");
          }

          // Process cart items
          cartItems.forEach((item) => {
            const { product: { name: title, price, imageUrl: productImage }, quantity } = item;

            // Add each item to the cart UI
            for (let i = 0; i < quantity; i++) {
              addProductToCart(title, price, productImage);
            }
          });
        })
        .catch(error => {
          console.error("Error loading user's cart:", error);
        });

  } else {
    // User is a guest, load cart from localStorage
    console.log("User is not logged in, loading cart from localStorage...");

    let cartContent = JSON.parse(localStorage.getItem("cartContent")) || [];
    console.log("Cart content from localStorage:", cartContent);

    cartContent.forEach(item => {
      const { title, quantity } = item;

      // Call API to get product details by title (like price and image)
      fetch(`https://elysiumdeploy-production.up.railway.app/api/products/title/${encodeURIComponent(title)}`)
          .then(response => {
            if (!response.ok) {
              throw new Error(`Failed to load product details for ${title}`);
            }
            return response.json();
          })
          .then(productDetails => {
            console.log("Product details from backend:", productDetails);
            const price  = productDetails.price
            const image = productDetails.imageUrl

            // Add product to the cart based on its quantity
            for (let i = 0; i < quantity; i++) {
              addProductToCart(title, price, image);
            }
          })
          .catch(error => {
            console.error(`Error loading product details for ${title}:`, error);
          });
    });
  }
}
function removeCartItem(event) {
  console.log("Start-RemoveCartItem");

  // Get the userId from localStorage to check if the user is logged in
  let user = localStorage.getItem("userId");

  // Get the cart item details from the cart-box that was clicked
  var button = event.target;
  var cartBox = button.closest('.cart-box');

  // Extract product title and quantity
  let title = cartBox.querySelector('.cart-product-title').innerText;
  let quantity = cartBox.querySelector('.cart-quantity').value;

  // Remove the product from the UI (HTML)
  cartBox.remove();
  console.log("Removed item from cart UI:", title);

  if (user !== null && user !== undefined) {
    // If user is logged in, call the API to remove the product from the database
    console.log("User is logged in, calling API to remove item from cart");

    const products = [{ title, quantity }]; // Pass title and quantity as a JSON object like in the "add" function

    fetch(`https://elysiumdeploy-production.up.railway.app/api/cart/${user}/remove`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(products) // Send only this item in the request body
    })
        .then(response => {
          if (!response.ok) {
            throw new Error("Failed to remove item from the user's cart");
          }
          return response.text(); // Return plain text response
        })
        .then(data => {
          console.log("Successfully removed from cart:", data);
        })
        .catch(error => {
          console.error("Error removing item from user's cart:", error);
        });

  } else {
    // If user is not logged in, update localStorage cartContent
    console.log("User is not logged in, updating localStorage cart");

    // Get the existing cartContent from localStorage
    let cartContent = JSON.parse(localStorage.getItem("cartContent")) || [];
    console.log("Before filtering:", cartContent);

    // Parse quantity as an integer for local filtering (but not affecting the backend logic above)
    let parsedQuantity = parseInt(quantity, 10);

    // Filter out the item to be removed (filter checks title case-insensitively AND quantity as integer)
    cartContent = cartContent.filter(item => {
      console.log(
          "Comparing:",
          item.title.trim().toLowerCase(),
          "with",
          title.trim().toLowerCase(),
          "|",
          item.quantity,
          "with",
          parsedQuantity
      );
      return !(item.title.trim().toLowerCase() === title.trim().toLowerCase() && item.quantity === parsedQuantity);
    });

    // Update localStorage with the new cartContent
    localStorage.setItem("cartContent", JSON.stringify(cartContent));
    console.log("Updated cartContent in localStorage:", cartContent);
  }

  updateTotal();
  updateCartCount();
}


function quantityChanged(event) {
  var input = event.target
  if (isNaN(input.value) || input.value <= 0) {
    input.value = 1;
  }
  updateTotal();
  updateCartCount();
}

function addCartClicked(event) {
  // Get the userId from localStorage (to check if user is logged in)
  let user = localStorage.getItem("userId");

  // Get the product details from the clicked product card
  var button = event.target;
  var productCard = button.closest('.product-card');

  var title = productCard.querySelector('.card-title').innerText;
  var price = productCard.querySelector('.item-price').innerText;
  var productImage = productCard.querySelector('.item-img').src;

  // Default quantity is 1 when a new product is added
  let quantity = 1;

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
      // If product exists, increase the quantity
      cartContent[existingProductIndex].quantity += 1;
    } else {
      // Otherwise, add it to the cart
      cartContent.push(product);
    }

    // Update localStorage with the new cartContent
    localStorage.setItem("cartContent", JSON.stringify(cartContent));
    console.log("Updated cartContent in localStorage:", cartContent);
  }

  addProductToCart(title, price, productImage);

  updateTotal();
  updateCartCount();
}


function addProductToCart(title, price, productImage) {
  var cartShopBox = document.createElement('div');
  cartShopBox.classList.add('cart-box');
  var cartItems = document.getElementsByClassName('cart-content')[0];
  var cartItemNames = cartItems.getElementsByClassName('cart-product-title');

  // Check if the product already exists in the cart
  for (var i = 0; i < cartItemNames.length; i++) {
    if (cartItemNames[i].innerText.toLowerCase() === title.toLowerCase()) {
      console.log('Product already in cart, updating quantity...');

      // If product is already in the cart, update its quantity
      var quantityInput = cartItemNames[i].parentElement.querySelector('.cart-quantity');
      quantityInput.value = parseInt(quantityInput.value) + 1; // Increment the quantity
      updateTotal();
      updateCartCount();
      return; // Exit the function since product is already in the cart
    }
  }

  // If product is not in the cart, add a new product
  var cartBoxContent = `
    <img src="${productImage}" alt="${title}" class="cart-img">
    <div class="detail-box">
      <div class="cart-product-title">${title}</div>
      <div class="cart-price">${price}</div>
      <input type="number" value="1" min="1" class="cart-quantity"> <!-- Dynamic quantity input -->
    </div>
    <!-- Remove Cart -->
    <button class="cart-remove">
      <ion-icon name="trash-outline" aria-hidden="true"></ion-icon>
    </button>`;

  cartShopBox.innerHTML = cartBoxContent;
  cartItems.append(cartShopBox);

  // Attach event listeners to new elements
  cartShopBox.getElementsByClassName('cart-remove')[0].addEventListener("click", removeCartItem);
  cartShopBox.getElementsByClassName('cart-quantity')[0].addEventListener("change", quantityChanged);

  // Update the total and cart count since a new item was added
  updateTotal();
  updateCartCount();
}


// Attach the event listener to all cart buttons
document.querySelectorAll('.cart-btn').forEach((btn) => {
  btn.addEventListener('click', addCartClicked);
});

function updateTotal() {
  var cartContent = document.getElementsByClassName('cart-content')[0]
  var cartBoxes = cartContent.getElementsByClassName('cart-box')
  var total = 0;

  for (var i = 0; i < cartBoxes.length; i++) {
    var cartBox = cartBoxes[i]
    var priceElement = cartBox.getElementsByClassName('cart-price')[0]
    var quantityElement = cartBox.getElementsByClassName('cart-quantity')[0]
    var price = parseFloat(priceElement.innerHTML.replace("$", ""))
    var quantity = quantityElement.value
    total = total + (price * quantity)
  }
  total = Math.round(total * 100) / 100

  document.getElementsByClassName('total-price')[0].innerText = '$' + total
}

function updateCartCount() {
  var cartItems = document.querySelector('.cart-content')
  var cartCount = document.getElementById('cart-count')
  cartCount.innerHTML = cartItems.children.length
}