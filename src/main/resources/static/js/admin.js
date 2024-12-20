// FETCH AND LOAD ORDER HISTORY
document.addEventListener("DOMContentLoaded", () => {
    const customerFilterForm = document.querySelector("#filter-customer").closest("form");
    const productFilterForm = document.querySelector("#filter-product").closest("form");
    const dateFilterForm = document.querySelector("#filter-date").closest("form");
    const orderTableBody = document.querySelector(".order-history table tbody");

    // Utility to fetch and render order data
    async function fetchAndRenderOrders(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                if (response.status === 404) {
                    orderTableBody.innerHTML = "<tr><td colspan='5'>No orders found</td></tr>";
                    throw new Error("User not found.");
                } else {
                    throw new Error("Failed to fetch order history.");
                }
            }
            const orders = await response.json();
            renderOrders(orders);
        } catch (error) {
            console.error("Error fetching orders:", error);
        }
    }

    // Render order rows in the table
    function renderOrders(orders) {
        orderTableBody.innerHTML = ""; // Clear existing rows
        if (orders.length === 0) {
            orderTableBody.innerHTML = "<tr><td colspan='5'>No orders found</td></tr>";
            return;
        }

        // Group orders by order ID
        const groupedOrders = orders.reduce((acc, item) => {
            const orderId = item.order.id;
            if (!acc[orderId]) {
                acc[orderId] = {
                    ...item.order,
                    items: [],
                };
            }
            acc[orderId].items.push({
                name: item.product.name,
                quantity: item.quantity,
                price: item.priceAtTime,
            });
            return acc;
        }, {});

        // Create rows for each order
        Object.values(groupedOrders).forEach((order) => {
            const orderRow = document.createElement("tr");
            orderRow.innerHTML = `
                <td>#${order.id}</td>
                <td>${order.userId}</td>
                <td>$${order.totalPrice.toFixed(2)}</td>
                <td>${order.dateOfPurchase}</td>
                <td>
                    <ul>
                        ${order.items
                            .map(
                                (item) =>
                                    `<li><strong>${item.name}</strong> (x${item.quantity}) - $${item.price.toFixed(2)}</li>`
                            )
                            .join("")}
                    </ul>
                </td>
            `;
            orderTableBody.appendChild(orderRow);
        });
    }

    // Event listeners for filtering forms
    customerFilterForm.addEventListener("submit", (event) => {
        event.preventDefault();
        const fullNameList = document.querySelector("#filter-customer").value.trim().split(" ");
        const username = fullNameList[0] + fullNameList[1];
        if (username) {
            fetchAndRenderOrders(`https://elysiumdeploy-production.up.railway.app/admin/OrderHistory/user/${username}`);
        }
    });

    productFilterForm.addEventListener("submit", (event) => {
        event.preventDefault();
        const productName = document.querySelector("#filter-product").value.trim();
        if (productName) {
            fetchAndRenderOrders(`https://elysiumdeploy-production.up.railway.app/admin/OrderHistory/product/${productName}`);
        }
    });

    dateFilterForm.addEventListener("submit", (event) => {
        event.preventDefault();
        const date = document.querySelector("#filter-date").value;
        if (date) {
            fetchAndRenderOrders(`https://elysiumdeploy-production.up.railway.app/admin/OrderHistory/date/${date}`);
        }
    });

    // Load all orders initially
    fetchAndRenderOrders("https://elysiumdeploy-production.up.railway.app/admin/OrderHistory");
});

// LOADING CUSTOMER INFORMATION
// Fetch customers from the API and populate the table
async function loadCustomers() {
    try {
      const response = await fetch("https://elysiumdeploy-production.up.railway.app/api/users");
      if (!response.ok) throw new Error("Failed to fetch customer data.");
  
      const customers = await response.json();
      const tbody = document.querySelector(".customer-management table tbody");
      if (customers.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center;">No customers in database.</td>
            </tr>
        `;
        return;
      }
  
      // Clear the existing rows
      tbody.innerHTML = "";
  
      // Populate the table with customer data
      customers.forEach((customer) => {
        // Only display members
        if (customer.role == "MEMBER") {
            const row = document.createElement("tr");
            row.innerHTML = `
            <td>#${customer.id}</td>
            <td>${customer.firstName} ${customer.lastName}</td>
            <td>${customer.email}</td>
            <td>${customer.address}</td>
            <td><button id="edit-info" data-id="${customer.id}">Edit Info</button></td>
            `;
            tbody.appendChild(row);
        }
      });
  
      // Add event listeners to "Edit Info" buttons
      document.querySelectorAll("#edit-info").forEach((button) => {
        button.addEventListener("click", (e) => {
          const customerId = e.target.getAttribute("data-id");
          localStorage.setItem("adminUserId", customerId);
          window.location.replace("adminProfile.html");
        });
      });
    } catch (error) {
      console.error("Error loading customers:", error);
    }
  }

loadCustomers();

// INVENTORY MANAGEMENT
const productList = document.getElementById("product-list");
const productSearchInput = document.querySelector('input[name="search-product"]');

// Update product list dynamically
function updateProductList(products) {
    productList.innerHTML = ""; // Clear current products
  
    products.forEach((product) => {
        const productItem = document.createElement("li");
        productItem.innerHTML = `
          <div class="product-item">
              <img src="${product.imageUrl}" alt="${product.name}" class="product-image">
              <div class="product-details">
                  <h3 class="item-name">${product.name}</h3>
                  <p><strong>Price:</strong> $${product.price.toFixed(2)}</p>
                  <p><strong>Brand:</strong> ${product.brand}</p>
                  <p><strong>Category:</strong> ${product.category.name}</p>
              </div>
              <div class="update-quantity">
                  <p><strong>Quantity in Inventory:</strong> <span class="item-quantity">${product.stockQuantity}</span></p>
                  <form class="update-quantity-form">
                      <input type="number" class="update-quantity-input" placeholder="Value">
                      <button type="submit" class="update-quantity-btn">Add/Reduce</button>
                  </form>
              </div>
          </div>
        `;
      
        // Append the product item to the product list
        productList.appendChild(productItem);
      
        // Add event listener to handle the update form submission
        const updateForm = productItem.querySelector(".update-quantity-form");
        const quantityInput = productItem.querySelector(".update-quantity-input");
        const quantityDisplay = productItem.querySelector(".item-quantity");
      
        updateForm.addEventListener("submit", async (e) => {
          e.preventDefault();
      
          const quantityChange = parseInt(quantityInput.value);
          if (isNaN(quantityChange)) {
            alert("Please enter a valid quantity.");
            return;
          } else if (product.stockQuantity + quantityChange < 0) {
            alert("Overall quantity cannot be less than 0.");
            return;
          }
      
          try {
            const response = await fetch(`https://elysiumdeploy-production.up.railway.app/admin/inventory/${encodeURIComponent(product.name)}?quantityChange=${quantityChange}`, {
              method: "PATCH",
              headers: {
                "Content-Type": "application/json",
              },
            });
      
            if (response.ok) {
              const updatedProduct = await response.json();
              // Update the displayed quantity
              quantityDisplay.textContent = updatedProduct.stockQuantity;
              quantityInput.value = ""; // Clear the input field
              alert("Quantity updated successfully!");
            }
          } catch (error) {
            console.error("Error updating quantity:", error);
          }
        });
    });
}
    
async function searchProducts(query) {
    const url = `https://elysiumdeploy-production.up.railway.app/api/products/search/name?name=${encodeURIComponent(query)}`;
    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error("Failed to fetch products");
      }
      const products = await response.json();
      if (products.length === 0) {
        productList.innerHTML = "<p>No products found matching your search query.</p>";
        return;
      }
      updateProductList(products);
    } catch (error) {
      console.error("Error fetching products by search:", error);
      productList.innerHTML = "<p>No products found. Please try a different search string.</p>";
    }
  }

productSearchInput.addEventListener("input", (event) => {
const query = event.target.value.trim();
if (query) {
    searchProducts(query);
} else {
    productList.innerHTML = "<p>Search for a product to view inventory information.</p>";
}
});

function logout(){
    localStorage.removeItem("role");
    localStorage.removeItem("userId");
    window.location.replace("/index.html");
}