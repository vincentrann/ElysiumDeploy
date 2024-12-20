// Fetching and displaying the user's data
document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem("userId");

    if (!userId) {
        alert("User not logged in!");
        window.location.href = "login.html";
        return;
    }

    try {
        // Fetch user data
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/users/${userId}`);
        if (!response.ok) {
            throw new Error("Failed to fetch user data");
        }

        const user = await response.json();
        console.log("User data:", user);

        // Populate the Current Info section
        document.querySelector(".current-info").innerHTML = `
            <h1>Profile</h1>
            <p><strong>Full Name:</strong> ${user.firstName} ${user.lastName}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Shipping Address:</strong> ${user.address}</p>
        `;

        // Pre-fill the Update Info form
        document.getElementById("first-name").value = user.firstName;
        document.getElementById("last-name").value = user.lastName;
        document.getElementById("email").value = user.email;
        const addressParts = user.address.split(",");
        document.getElementById("street").value = addressParts[0]?.trim();
        document.getElementById("city").value = addressParts[1]?.trim();
        document.getElementById("state").value = addressParts[2]?.trim();
        document.getElementById("zip").value = addressParts[3]?.trim();
    } catch (error) {
        console.error("Error fetching user data:", error);
        alert("Unable to load profile information. Please try again.");
    }

    // Fetch credit card data
    fetchCreditCards(userId);

    // Fetch purchase history
    fetchPurchaseHistory();
});

// Updating the user's data
document.querySelector(".update-info form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const userId = localStorage.getItem("userId");
    const updatedUser = {
        firstName: document.getElementById("first-name").value,
        lastName: document.getElementById("last-name").value,
        email: document.getElementById("email").value,
        address: `${document.getElementById("street").value}, ${document.getElementById("city").value}, ${document.getElementById("state").value}, ${document.getElementById("zip").value}`,
    };

    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/users/${userId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedUser),
        });

        if (!response.ok) {
            throw new Error("Failed to update user data");
        }

        alert("Profile updated successfully!");
    } catch (error) {
        console.error("Error updating user data:", error);
        alert("Failed to update profile. Please try again.");
    }
});

// Fetching purchase history
async function fetchPurchaseHistory() {
    const userId = localStorage.getItem("userId");

    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/orders/user/${userId}`);
        if (!response.ok) {
            throw new Error("Failed to fetch purchase history");
        }

        const orders = await response.json();
        const tableBody = document.querySelector(".purchase-history tbody");

        if (orders.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="4" style="text-align: center;">You have no purchase history.</td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = orders
            .map(
                (order) => `
                <tr>
                    <td>#${order.id}</td>
                    <td>${new Date(order.dateOfPurchase).toLocaleDateString()}</td>
                    <td>$${order.totalPrice}</td>
                </tr>
            `
            )
            .join("");
    } catch (error) {
        console.error("Error fetching purchase history:", error);
        alert("Unable to load purchase history. Please try again.");
    }
}

// Fetching and displaying credit card data
async function fetchCreditCards(userId) {
    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/credit-cards/user/${userId}`);
        if (!response.ok) {
            throw new Error("Failed to fetch credit card data");
        }

        const creditCards = await response.json();
        const creditCardSection = document.querySelector(".credit-card");

        creditCardSection.innerHTML = "";

        if (creditCards.length > 0) {
            creditCards.forEach((card, index) => {
                const maskedNumber = `**** **** **** ${card.cardNumber.slice(-4)}`;
                const cardElement = `
                    <div class="card-info">
                        <p><strong>Card ${index + 1}:</strong> ${maskedNumber}</p>
                        <p>Expiry Date: ${card.expiryDate}</p>
                        <button onclick="deleteCreditCard(${card.id}, '${userId}')">Delete</button>
                    </div>
                `;
                creditCardSection.innerHTML += cardElement;
            });
        } else {
            creditCardSection.innerHTML = "<p>No credit card information available.</p>";
        }
    } catch (error) {
        console.error("Error fetching credit card data:", error);
        alert("Unable to load credit card information. Please try again.");
    }
}

// Adding credit card data
document.querySelector("#credit-card-form").addEventListener("submit", async function (event) {
    event.preventDefault();

    const userId = localStorage.getItem("userId");
    const cardNumber = document.getElementById("card-number").value.trim();
    const expiryDate = document.getElementById("expiry-date").value.trim();
    const cvv = document.getElementById("cvv").value.trim();

    const creditCard = {
        cardNumber,
        expiryDate,
        cvv,
    };

    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/credit-cards/user/${userId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(creditCard),
        });

        if (!response.ok) {
            throw new Error("Failed to add credit card");
        }

        alert("Credit card added successfully!");
        fetchCreditCards(userId);
        document.getElementById("credit-card-form").reset();
    } catch (error) {
        console.error("Error adding credit card:", error);
        alert("Failed to add credit card. Please try again.");
    }
});

// Deleting a credit card
async function deleteCreditCard(cardId, userId) {
    try {
        const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/credit-cards/${cardId}/user/${userId}`, {
            method: "DELETE",
        });

        if (!response.ok) {
            throw new Error("Failed to delete credit card");
        }

        alert("Credit card deleted successfully!");
        fetchCreditCards(userId);
    } catch (error) {
        console.error("Error deleting credit card:", error);
        alert("Failed to delete credit card. Please try again.");
    }
}

// Logout
function logout() {
    fetch("https://elysiumdeploy-production.up.railway.app/api/users/logout", {
        method: "POST",
        credentials: "include",
    })
        .then(() => {
            localStorage.removeItem("userId");
            localStorage.removeItem("role");
            window.location.replace("index.html");
        })
        .catch((error) => {
            console.error("Error logging out:", error);
            alert("Failed to log out. Please try again.");
        });
}