'use strict';
let cartButton = document.querySelector('.cart-button');
let cart = document.querySelector('.cart');
let closeCartButton = document.querySelector('.close-cart');

// Open and Close Cart Sidebar
cartButton.onclick = () => cart.classList.add('active');
closeCartButton.onclick = () => cart.classList.remove('active');

/**
 * Cart Management Logic
 */
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', ready);
} else {
  ready();
}

function ready() {
  loadCartFromLocalStorage(); // Load the cart from localStorage on page load
  document.querySelectorAll('.cart-remove').forEach(button => button.addEventListener('click', removeCartItem));
  document.querySelectorAll('.cart-quantity').forEach(input => input.addEventListener('change', quantityChanged));
  document.querySelectorAll('.cart-btn').forEach(button => button.addEventListener('click', addCartClicked));
  document.querySelector('.btn-buy').addEventListener('click', checkoutButtonClicked);
}

// Update total and cart count
function updateCartUI() {
  updateTotal();
  updateCartCount();
  saveCartToLocalStorage();
}

// Remove Cart Item
function removeCartItem(event) {
  const cartBox = event.target.closest('.cart-box');
  const productTitle = cartBox.querySelector('.cart-product-title').innerText;
  cartBox.remove();

  let cart = loadCartFromLocalStorage();
  cart = cart.filter(item => item.title !== productTitle);
  saveCartToLocalStorage(cart);
  updateCartUI();
}

// Change quantity
function quantityChanged(event) {
  if (isNaN(event.target.value) || event.target.value <= 0) event.target.value = 1;

  const cartBox = event.target.closest('.cart-box');
  const productTitle = cartBox.querySelector('.cart-product-title').innerText;
  const newQuantity = parseInt(event.target.value);

  const cart = loadCartFromLocalStorage();
  const item = cart.find(item => item.title === productTitle);
  if (item) item.quantity = newQuantity;

  saveCartToLocalStorage(cart);
  updateCartUI();
}

// Checkout Logic
function checkoutButtonClicked() {
  alert('Your order has been placed');
  document.querySelector('.cart-content').innerHTML = '';
  localStorage.removeItem('cart');
  updateCartUI();
}

// Add Product to Cart
function addProductToCart(title, price, image) {
  const cartContent = document.querySelector('.cart-content');
  if ([...cartContent.children].some(item => item.querySelector('.cart-product-title').innerText === title)) {
    alert('This item is already in your cart');
    return;
  }

  const cartBox = document.createElement('div');
  cartBox.classList.add('cart-box');
  cartBox.innerHTML = `
    <img src="${image}" alt="${title}" class="cart-img">
    <div class="detail-box">
      <div class="cart-product-title">${title}</div>
      <div class="cart-price">${price}</div>
      <input type="number" value="1" min="1" class="cart-quantity">
    </div>
    <button class="cart-remove"><ion-icon name="trash-outline"></ion-icon></button>`;
  cartContent.appendChild(cartBox);

  cartBox.querySelector('.cart-remove').addEventListener('click', removeCartItem);
  cartBox.querySelector('.cart-quantity').addEventListener('change', quantityChanged);

  const cart = loadCartFromLocalStorage();
  cart.push({ title, price, image, quantity: 1 });
  saveCartToLocalStorage(cart);

  updateCartUI();
}

// Save cart to localStorage
function saveCartToLocalStorage(cart) {
  localStorage.setItem('cart', JSON.stringify(cart));
}

// Load cart from localStorage
function loadCartFromLocalStorage() {
  const storedCart = localStorage.getItem('cart');
  const cartContent = document.querySelector('.cart-content');
  cartContent.innerHTML = '';
  if (storedCart) {
    const cart = JSON.parse(storedCart);
    cart.forEach(item => {
      const cartBox = document.createElement('div');
      cartBox.classList.add('cart-box');
      cartBox.innerHTML = `
        <img src="${item.image}" alt="${item.title}" class="cart-img">
        <div class="detail-box">
          <div class="cart-product-title">${item.title}</div>
          <div class="cart-price">${item.price}</div>
          <input type="number" value="${item.quantity}" class="cart-quantity" min="1">
        </div>
        <button class="cart-remove"><ion-icon name="trash-outline"></ion-icon></button>`;
      cartContent.appendChild(cartBox);
    });
  }
  updateCartUI();
}

function updateTotal() {
  const cart = loadCartFromLocalStorage();
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  document.querySelector('.total-price').innerText = `$${total.toFixed(2)}`;
}

function updateCartCount() {
  const cart = loadCartFromLocalStorage();
  document.getElementById('cart-count').innerText = cart.length;
}
