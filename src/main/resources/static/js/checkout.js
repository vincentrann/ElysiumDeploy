const userId = localStorage.getItem("userId");

// Fetch and display cart items in checkout
async function loadCart(userId) {
    const cartSummaryElement = document.querySelector(".cart-summary ul");
    const totalElement = document.querySelector(".cart-summary .total span");

    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/cart/${userId}`);
        if (!response.ok) {
            throw new Error("Failed to load cart data");
        }
        const cartItems = await response.json();

        // Clear any existing cart items
        cartSummaryElement.innerHTML = "";

        let totalPrice = 0;

        // Loop through the cart items and create HTML for each
        cartItems.forEach(item => {
            const product = item.product;

            const cartItemHTML = `
                <li class="cart-item">
                    <div class="product-item">
                        <img src="${product.imageUrl}" alt="${product.name}" class="product-image">
                        <div class="product-details">
                            <h3 class="item-name">${product.name}</h3>
                            <p><strong>Price:</strong> $${item.priceAtTime.toFixed(2)}</p>
                            <p><strong>Quantity Selected:</strong> ${item.quantity}</p>
                        </div>
                    </div>
                </li>
            `;
            cartSummaryElement.insertAdjacentHTML("beforeend", cartItemHTML);

            totalPrice += item.priceAtTime * item.quantity;
        });

        // Update the total price
        totalElement.innerHTML = `<p><strong>Total Price: </strong> <span id="total-price">$${totalPrice.toFixed(2)}</span></p>`;

    } catch (error) {
        console.error("Error loading cart:", error);
    }
}
loadCart(userId);

// Fetch and display user shipping information
async function loadShippingInfo(userId) {
    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/users/${userId}`);
        if (!response.ok) throw new Error("Failed to fetch user information");

        const user = await response.json();
        document.querySelector(".customer-info").innerHTML = `
            <p><strong>Full Name:</strong> ${user.firstName} ${user.lastName}</p>
            <p><strong>Shipping Address:</strong> ${user.address}</p>
            <p><strong>Select a Credit Card:</strong></p>
        `;
    } catch (error) {
        console.error("Error loading shipping info:", error);
    }
}
loadShippingInfo(userId);

// Fetch and display saved credit cards
async function loadCreditCards(userId) {
    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/credit-cards/user/${userId}`);
        if (!response.ok) throw new Error("Failed to fetch credit cards");

        const creditCards = await response.json();
        const creditCardList = document.querySelector(".credit-card-list");
        creditCardList.innerHTML = ""; // Clear any existing content

        creditCards.forEach((card, index) => {
            const maskedNumber = `**** **** **** ${card.cardNumber.slice(-4)}`;
            const listItem = document.createElement("li");
            listItem.innerHTML = `
                <div class="credit-card-option">
                    <input type="radio" name="credit-card" id="card-${card.id}" value="${card.id}">
                    <label for="card-${card.id}">
                        <p><strong>Card ${index + 1}:</strong> ${maskedNumber}</p>
                        <p>Expiry Date: ${card.expiryDate}</p>
                    </label>
                </div>
            `;
            creditCardList.appendChild(listItem);
        });
    } catch (error) {
        console.error("Error loading credit cards:", error);
    }
}
loadCreditCards(userId);

document.querySelector(".checkout-form form").addEventListener("submit", async function (event) {
    event.preventDefault(); // Prevent default form submission

    // Get the cart items
    const cartItems = document.querySelector(".cart-summary ul").children;
    if (cartItems.length === 0) {
        alert("Your cart is empty. Please add items to your cart before placing the order.");
        return;
    }

    // Get selected credit card
    const selectedCard = document.querySelector('input[name="credit-card"]:checked');

    // Get new credit card inputs
    const cardNumber = document.getElementById("card-number").value.trim();
    const expiryDate = document.getElementById("expiry-date").value.trim();
    const cvv = document.getElementById("cvv").value.trim();

    // Validate new credit card inputs if they are filled
    if (cardNumber || expiryDate || cvv) {
        if (cardNumber.length !== 12 || expiryDate.length !== 5 || cvv.length !== 3) {
            alert("Invalid credit card details entered.");
            return;
        }
    } else if (!selectedCard) {
        // If no new credit card inputs and no selected card
        alert("Please select a saved credit card or enter a new one.");
        return;
    }

    // Backend API call to checkout
    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/orders/checkout/${userId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
        });

        if (!response.ok) throw new Error("Failed to place order.");

        // Order created successfully, show the modal
        const modal = document.getElementById("orderConfirmedModal");
        const modalOrderItems = modal.querySelector(".order-items ul");
        const modalTotalPrice = modal.querySelector("#modal-total-price");

        const userResponse = await fetch(`https://elysiumdeploy-production.up.railway.app/api/users/${userId}`);
        const user = await userResponse.json();

        // Populate modal content
        document.querySelector(".modal-customer-info").innerHTML = `
        <p><strong>Full Name:</strong> ${user.firstName} ${user.lastName}</p>
        <p><strong>Email Address:</strong> ${user.email}</p>
        <p><strong>Shipping Address:</strong> ${user.address}</p>
        `;
        
        // Populate order items in the modal
        modalOrderItems.innerHTML = ""; // Clear previous items
        Array.from(cartItems).forEach((item) => {
            const clonedItem = item.cloneNode(true); // Clone cart item to display in modal
            modalOrderItems.appendChild(clonedItem);
        });

        // Calculate total price and set it in modal
        const totalPrice = document.querySelector("#total-price").textContent;
        modalTotalPrice.innerHTML = `
        <p><strong>Total Price: </strong> <span>${totalPrice}</span></p>
        `;

        // Display the modal
        modal.style.display = "flex";
        modal.classList.remove("hidden");

        // Add event listener for the close button
        document.getElementById("closeModal").addEventListener("click", function () {
            modal.style.display = "none";
            modal.classList.add("hidden");
        });

        // Automatically redirect after 5 seconds
        setTimeout(() => {
            modal.style.display = "none";
            modal.classList.add("hidden");
            window.location.href = "index.html";
        }, 10000);
    } catch (error) {
        console.error("Error placing order:", error);
    }
});