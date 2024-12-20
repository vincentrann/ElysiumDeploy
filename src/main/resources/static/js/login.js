
function showLogin() {
    document.getElementById('login-screen').classList.remove('hidden');
    document.getElementById('signup-screen').classList.add('hidden');
    document.getElementById('admin-login-screen').classList.add('hidden');
}

function showSignup() {
    document.getElementById('login-screen').classList.add('hidden');
    document.getElementById('signup-screen').classList.remove('hidden');
    document.getElementById('admin-login-screen').classList.add('hidden');
}

function proceedToDetails(event)
{
    event.preventDefault(); // Prevent form submission
    document.getElementById('signup-screen').classList.add('hidden');
    document.getElementById('details-screen').classList.remove('hidden');
}

function backToSignup() {
    document.getElementById('details-screen').classList.add('hidden');
    document.getElementById('signup-screen').classList.remove('hidden');
}

async function registerUser(event) {
    event.preventDefault();

    // Step 1: Register User
    const userRegistrationData = {
        username: document.getElementById('sign-up-first-name').value + document.getElementById('sign-up-last-name').value,
        firstName: document.getElementById('sign-up-first-name').value,
        lastName: document.getElementById('sign-up-last-name').value,
        email: document.getElementById('sign-up-email').value,
        password: document.getElementById('sign-up-password').value,
        address: document.getElementById('street').value + ", " +
            document.getElementById('city').value + ", " +
            document.getElementById('state').value + ", " +
            document.getElementById('zip').value,
        billingAddress: document.getElementById('street').value + ", " +
            document.getElementById('city').value + ", " +
            document.getElementById('state').value + ", " +
            document.getElementById('zip').value,
    };

    try {
        // Step 1: Register the user
        const response = await fetch("https://elysiumdeploy-production.up.railway.app/api/users/register?role=member", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userRegistrationData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "User registration failed");
        }

        // Get the registered user data
        const newUser = await response.json();
        localStorage.setItem("userId", newUser.id);
        if (localStorage.getItem("userId")) {
            console.log("Successfully set");
        }
        console.log("User registered successfully:", newUser);

        // Step 2: Add Credit Card
        const creditCardData = {
            cardNumber: document.getElementById('card-number').value,
            expiryDate: document.getElementById('expiry-date').value,
            cvv: document.getElementById('cvv').value,
            cardHolderName: newUser.firstName + " " + newUser.lastName,
            billingAddress: newUser.billingAddress
        };

        const creditCardResponse = await fetch(`https://elysiumdeploy-production.up.railway.app/api/credit-cards/user/${newUser.id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(creditCardData)
        });

        if (!creditCardResponse.ok) {
            const errorData = await creditCardResponse.json();
            throw new Error(errorData.message || "Credit card registration failed");
        }

        alert("Registration and credit card addition successful!");
        console.log("Credit Card added successfully!");

        // 🛒 **Step 3: Transfer Guest Cart to Registered User**
        let products = JSON.parse(localStorage.getItem("cartContent")); // Convert string back to array
        if (products && products.length > 0) {
            console.log("Products to transfer:", products);
            try {
                const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/cart/${newUser.id}/transfer`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(products)
                });

                if (!response.ok) {
                    throw new Error("Failed to transfer guest cart to user cart");
                }

                const transferred = await response.text();
                console.log("Cart transfer successful:", transferred);

                // Clear the local cart after successful transfer
                localStorage.removeItem("cartContent");

                // Redirect to checkout
                window.location.replace("checkout.html");

            } catch (error) {
                console.error("Error transferring guest cart to user cart:", error);
            }
        } else {
            console.log("No guest cart to transfer.");
            window.location.replace("index.html");
        }

    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred: " + error.message);
    }
}


// Attach event listeners for login forms
document.getElementById('member-login-form')?.addEventListener('submit', loginMemberUser);
document.getElementById('admin-login-form')?.addEventListener('submit', loginAdminUser);

async function loginUser(event)
{
    event.preventDefault();
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("password").value;
    const response = await fetch("https://elysiumdeploy-production.up.railway.app/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const loggedInUser = await response.json();
        localStorage.setItem("userId",loggedInUser.id);
        localStorage.setItem("role", loggedInUser.role)

        if (localStorage.getItem("role") == "ADMIN"){
            window.location.replace("admin.html")
        }
        else if (localStorage.getItem("cartContent")== null)
            {
                window.location.replace("index.html");
            }

                let userId = loggedInUser.id;
            try{
                let products = JSON.parse(localStorage.getItem("cartContent")); // Convert string back to array
                console.log("Products to transfer:", products);
                const response = await fetch(`https://elysiumdeploy-production.up.railway.app/api/cart/${userId}/transfer`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(products)
                });
                const transfered =   await response.text();
                console.log(transfered);
                localStorage.removeItem("cartContent");
                window.location.replace("checkout.html")

            } catch (error) {
                console.error("Error transferring guest cart to user cart:", error);
            }
    } else {
        const error = await response.json();
        alert(`Error: ${error.message || "Invalid credentials"}`);
    }
}