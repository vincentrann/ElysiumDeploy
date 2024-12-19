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
    window.location.replace("/frontend/pages/login.html")
  }
  else
  {
    window.location.replace("/frontend/pages/checkout.html")
  }


  // emptying cart
  // var cartContent = document.getElementsByClassName('cart-content')[0]
  // while (cartContent.hasChildNodes()) {
  //   cartContent.removeChild(cartContent.firstChild)
  // }
  // updateTotal();
  // updateCartCount();
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

    fetch(`http://localhost:8080/api/cart/${user}/remove`, {
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

    // Filter out the item to be removed (since we don't have cartItemId for localStorage, we use title as fallback)
    cartContent = cartContent.filter(item => item.title !== title);

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
  console.log("Start-AddCartClicked");

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

    fetch(`http://localhost:8080/api/cart/${user}/add`, {
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

  console.log("before addProductToCart");
  addProductToCart(title, price, productImage);
  console.log("after addProductToCart");

  updateTotal();
  updateCartCount();
}


function addProductToCart(title, price, productImage) {
  var cartShopBox = document.createElement('div')
  cartShopBox.classList.add('cart-box')
  var cartItems = document.getElementsByClassName('cart-content')[0]
  var cartItemNames = cartItems.getElementsByClassName('cart-product-title')
  for (var i = 0; i < cartItemNames.length; i++) {
    if (cartItemNames[i].innerText.toLowerCase() == title.toLowerCase()) {
      alert('This item is already in your cart')
      return;
    }
  }
  var cartBoxContent = `
            <img src="${productImage}" alt="apple watch" class="cart-img" width="300"
              height="200">
              <div class="detail-box">
                <div class="cart-product-title">${title}</div>
                <div class="cart-price">${price}</div>
                <input type="number" value="1" min="1" class="cart-quantity">
              </div>
              <!-- Remove Cart -->
              <button class="cart-remove">
                <ion-icon name="trash-outline" aria-hidden="true"></ion-icon>
            </button>`

  cartShopBox.innerHTML = cartBoxContent
  cartItems.append(cartShopBox)
  cartShopBox.getElementsByClassName('cart-remove')[0].addEventListener("click", removeCartItem)
  cartShopBox.getElementsByClassName('cart-quantity')[0].addEventListener("change", quantityChanged)
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