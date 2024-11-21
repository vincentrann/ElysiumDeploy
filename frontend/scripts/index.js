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

  document.getElementsByClassName('btn-buy')[0].addEventListener("click", checkoutButtonClicked)
}

function checkoutButtonClicked() {
  alert('Your order has been placed')
  var cartContent = document.getElementsByClassName('cart-content')[0]
  while (cartContent.hasChildNodes()) {
    cartContent.removeChild(cartContent.firstChild)
  }
  updateTotal();
updateCartCount();
}

function removeCartItem(event) {
  var buttonClicked = event.target;
  buttonClicked.parentElement.remove()
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
  var button = event.target;
  
  var productCard = button.closest('.product-card');
  
  var title = productCard.querySelector('.card-title').innerText;
  var price = productCard.querySelector('.item-price').innerText;
  var productImage = productCard.querySelector('#item-img').src;
  
  addProductToCart(title, price, productImage);
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