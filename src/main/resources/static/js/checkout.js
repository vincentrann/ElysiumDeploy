const userId = localStorage.getItem("userId");

// Fetch and display cart items
async function loadCart(userId) {
    const cartSummaryElement = document.querySelector(".cart-summary ul");
    const totalElement = document.querySelector(".cart-summary .total span");

    try {
        const response = await fetch(`http://localhost:8080/api/cart/${userId}`);
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
        totalElement.innerHTML = `<strong>Total: </strong> $${totalPrice.toFixed(2)}`;

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

// Submit order with selected payment method
document.querySelector(".checkout-form form").addEventListener("submit", async function (event) {
    event.preventDefault();

    const selectedCard = document.querySelector('input[name="credit-card"]:checked');
    let paymentMethod;

    if (selectedCard) {
        // Use saved card
        paymentMethod = {
            type: "SAVED_CARD",
            cardId: selectedCard.value,
        };
    } else {
        // Use new card
        const cardNumber = document.getElementById("card-number").value.trim();
        const expiryDate = document.getElementById("expiry-date").value.trim();
        const cvv = document.getElementById("cvv").value.trim();

        if (!cardNumber || !expiryDate || !cvv) {
            alert("Please fill out all credit card fields.");
            return;
        }

        try {
            // Save new card to backend
            const saveCardResponse = await fetch(`https://elysiumdeploy-production.up.railway.app/api/credit-cards/user/${userId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ cardNumber, expiryDate, cvv }),
            });

            if (!saveCardResponse.ok) throw new Error("Failed to save new credit card");
            const savedCard = await saveCardResponse.json();

            paymentMethod = {
                type: "SAVED_CARD",
                cardId: savedCard.id, // Use the ID of the newly saved card
            };
        } catch (error) {
            console.error("Error saving new credit card:", error);
            alert("Failed to save new credit card. Please try again.");
            return;
        }
    }

    // Checkout with selected or new card
    try {
        const queryParams = new URLSearchParams({
            userId,
            creditCardId: paymentMethod.cardId,
        });

        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/orders/checkout?${queryParams.toString()}`, {
            method: "POST",
        });

        if (!response.ok) throw new Error("Failed to place order");

        const order = await response.json();
        alert(`Order placed successfully! Order ID: ${order.id}`);
    } catch (error) {
        console.error("Error placing order:", error);
        alert("Failed to place order. Please try again.");
    }
});
loadCreditCards(userId);

// MODAL MENU FOR ORDER CONFIRMATION
// Open the modal and populate it with the order info
// async function openEditModal(customerId, ) {
//     try {
//       const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/users/${customerId}`);
//       if (!response.ok) throw new Error("Failed to fetch customer details.");
  
//       const customer = await response.json();
  
//         // Populate modal fields
//         document.getElementById("first-name").value = customer.firstName;
//         document.getElementById("last-name").value = customer.lastName;
//         document.getElementById("email").value = customer.email;
//         document.getElementById("street").value = customer.address.street;
//         document.getElementById("city").value = customer.address.city;
//         document.getElementById("state").value = customer.address.state;
//         document.getElementById("zip").value = customer.address.zip;

//         const addressParts = customer.address.split(",");
//         document.getElementById("street").value = addressParts[0]?.trim();
//         document.getElementById("city").value = addressParts[1]?.trim();
//         document.getElementById("state").value = addressParts[2]?.trim();
//         document.getElementById("zip").value = addressParts[3]?.trim();
  
//         // Show modal
//         const modal = document.getElementById("orderConfirmed")
//         modal.style.display = "flex";
//         modal.dataset.customerId = customerId;
//         modal.classList.remove("hidden");
//     } catch (error) {
//         console.error("Error confirming checkout:", error);
//     }
//   }
  
//   // Close the modal when the close button is clicked
//   document.getElementById("closeModal").addEventListener("click", () => {
//     const modal = document.getElementById("editCustomerModal");
//     modal.style.display = "none";
//     modal.classList.add("hidden");
//     loadCustomers();
//   });